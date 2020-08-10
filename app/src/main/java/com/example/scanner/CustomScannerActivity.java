package com.example.scanner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomScannerActivity extends Activity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BackPressCloseHandler backPressCloseHandler;
    private ImageButton setting_btn,switchFlashlightButton;
    private Boolean switchFlashlightButtonCheck;

    private GridView gridView;
    private ArrayAdapter adapter;
    private TextView tot_price_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        /*장바구니 리스트 데이터 로드*/
        gridView = (GridView)findViewById(R.id.shopping_list_view);
        final ArrayList items = ((MainActivity)MainActivity.context_main).items;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        gridView.setAdapter(adapter);

        /*총 금액 변경*/
        final int tot_price = ((MainActivity)MainActivity.context_main).tot_price;
        tot_price_textView = (TextView)findViewById(R.id.total_price);
        tot_price_textView.setText("총 금액 : "+new DecimalFormat("###,###").format(tot_price) + " 원");

        /*결재 버튼 이벤트 리스너 등록*/
        Button payment_btn = (Button)findViewById(R.id.payment_btn);
        payment_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.clear();
                tot_price_textView.setText("총 금액 : 0 원");
                String str = "결재 완료";
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
            }
        });

        /*플래시 온오프 기능*/
        switchFlashlightButtonCheck = true;

        /*뒤로 가기 핸들러 등록*/
        backPressCloseHandler = new BackPressCloseHandler(this);

        setting_btn = (ImageButton)findViewById(R.id.setting_btn);
        switchFlashlightButton = (ImageButton)findViewById(R.id.switch_flashlight);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        /*바코드 리딩*/
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void switchFlashlight(View view) {
        if (switchFlashlightButtonCheck) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_on_white_36dp);
        switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_off_white_36dp);
        switchFlashlightButtonCheck = true;
    }
}