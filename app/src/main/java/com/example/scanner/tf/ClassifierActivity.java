/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.scanner.tf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.scanner.R;
import com.example.scanner.bc.BCMainActivity;
import com.example.scanner.bc.ListViewItem;
import com.example.scanner.db.DbOpenHelper;
import com.example.scanner.tf.env.BorderedText;
import com.example.scanner.tf.env.Logger;
import com.example.scanner.tf.tflite.Classifier;
import com.example.scanner.tf.tflite.Classifier.Device;
import com.example.scanner.tf.tflite.Classifier.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  private static final float TEXT_SIZE_DIP = 10;
  private Bitmap rgbFrameBitmap = null;
  private long lastProcessingTimeMs;
  private Integer sensorOrientation;
  private Classifier classifier;
  private BorderedText borderedText;
  /** Input image size of the model along x axis. */
  private int imageSizeX;
  /** Input image size of the model along y axis. */
  private int imageSizeY;

  private HashMap<String, Integer> regProd = new HashMap<>();

  @Override
  protected int getLayoutId() {
    return R.layout.tfe_ic_camera_connection_fragment;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    recreateClassifier(getModel(), getDevice(), getNumThreads());
    if (classifier == null) {
      LOGGER.e("No classifier on preview!");
      return;
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
  }

  @Override
  protected void processImage() {
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final int cropSize = Math.min(previewWidth, previewHeight);

    runInBackground(
        new Runnable() {
          @Override
          public void run() {
            if (classifier != null) {
              final long startTime = SystemClock.uptimeMillis();
              final List<Classifier.Recognition> results =
                  classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);
              lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
              LOGGER.v("Detect: %s", results);

              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      String title = results.get(0).getTitle();
                      if (results.get(0).getConfidence() * 100 >= 90) {
                        if(!regProd.keySet().contains(title)) regProd.put(title, 1);
                        else regProd.put(title, regProd.get(title)+1);

                        if(regProd.get(title) >= 30) {
                          regProd.remove(title);
                          dialogConfirm(title);
                        }
                      }
                      showResultsInBottomSheet(results);
                      showFrameInfo(previewWidth + "x" + previewHeight);
                      showCropInfo(imageSizeX + "x" + imageSizeY);
                      showCameraResolution(cropSize + "x" + cropSize);
                      showRotationInfo(String.valueOf(sensorOrientation));
                      showInference(lastProcessingTimeMs + "ms");
                    }
                  });
            }
            readyForNextImage();

          }
        });
  }

  @Override
  protected void onInferenceConfigurationChanged() {
    if (rgbFrameBitmap == null) {
      // Defer creation until we're getting camera frames.
      return;
    }
    final Device device = getDevice();
    final Model model = getModel();
    final int numThreads = getNumThreads();
    runInBackground(() -> recreateClassifier(model, device, numThreads));
  }

  private void recreateClassifier(Model model, Device device, int numThreads) {
    if (classifier != null) {
      LOGGER.d("Closing classifier.");
      classifier.close();
      classifier = null;
    }
    if (device == Device.GPU
        && (model == Model.QUANTIZED_MOBILENET || model == Model.QUANTIZED_EFFICIENTNET)) {
      LOGGER.d("Not creating classifier: GPU doesn't support quantized models.");
      runOnUiThread(
          () -> {
            Toast.makeText(this, R.string.tfe_ic_gpu_quant_error, Toast.LENGTH_LONG).show();
          });
      return;
    }
    try {
      LOGGER.d(
          "Creating classifier (model=%s, device=%s, numThreads=%d)", model, device, numThreads);
      classifier = Classifier.create(this, model, device, numThreads);
    } catch (IOException e) {
      LOGGER.e(e, "Failed to create classifier.");
    }

    // Updates the input image size.
    imageSizeX = classifier.getImageSizeX();
    imageSizeY = classifier.getImageSizeY();
  }


  public void dialogConfirm(String add_prod_name) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("상품 인식").setMessage("상품 "+add_prod_name+"추가 하시겠습니까?");
    builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        /* 아무런 처리 안함 */
        Toast.makeText(getApplicationContext(), "취소.", Toast.LENGTH_LONG).show();
        regProd.clear();
      }
    });
    builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(getApplicationContext());
        dbOpenHelper.open();
        dbOpenHelper.create();

        Cursor cursor = dbOpenHelper.selectColumns();
        while (cursor.moveToNext()) {
          String prod_name = cursor.getString(cursor.getColumnIndex("prod_name"));
          int price = cursor.getInt(cursor.getColumnIndex("price"));
          if(prod_name.equals(add_prod_name)) {
            ((BCMainActivity)BCMainActivity.context_main).tot_price+=price;
            ListViewItem item = new ListViewItem();
            item.setProd_name(prod_name);
            item.setPrice(price);
            ((BCMainActivity)BCMainActivity.context_main).items.add(item);
            adapter.addItem(item);
            break;
          }
        }
        refresh();
        Toast.makeText(getApplicationContext(), add_prod_name+"가 추가되었습니다.", Toast.LENGTH_LONG).show();
        regProd.clear();
      }
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

}
