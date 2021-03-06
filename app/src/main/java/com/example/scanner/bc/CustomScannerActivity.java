package com.example.scanner.bc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.BackPressHandler;
import com.example.scanner.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomScannerActivity extends Activity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BackPressHandler backPressHandler;
    private ImageButton setting_btn,switchFlashlightButton;
    private Boolean switchFlashlightButtonCheck;

    private ListView listView;
    private CustomListViewAdapter adapter;
    private TextView tot_price_textView;

    public static Context context_scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        context_scanner = this;

        /*장바구니 리스트 데이터 로드*/
        listView = (ListView)findViewById(R.id.shopping_list_view);
//        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,((MainActivity)MainActivity.context_main).items);
        adapter = new CustomListViewAdapter();

        setData();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dialogEvent2(view, position);
            }
        });
        listView.setSelection(adapter.getCount() -1);

        /*총 금액 변경*/
        setTot_Price(((BCMainActivity) BCMainActivity.context_main).tot_price);

        /*결재 버튼 이벤트 리스너 등록*/
        Button payment_btn = (Button)findViewById(R.id.payment_btn);
        payment_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEvent(view);
            }
        });

        /*플래시 온오프 기능*/
        switchFlashlightButtonCheck = true;

        /*뒤로 가기 핸들러 등록*/
        backPressHandler = new BackPressHandler(this);

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
        backPressHandler.onBackPressed();
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

    /* 결제버튼 클릭시 다이얼로그 이벤트 */
    public void dialogEvent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String pay_price = tot_price_textView.getText().toString().split(":")[1];
        builder.setTitle("결제").setMessage("총 금액"+pay_price+"을 결제 하시겠습니까?");
        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                /* 아무런 처리 안함 */
            }
        });
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((BCMainActivity) BCMainActivity.context_main).items.clear();
                ((BCMainActivity) BCMainActivity.context_main).tot_price = 0;
                tot_price_textView.setText("총 금액 : 0 원");
                String str = "결제 완료";
                adapter.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /* 아이템 클릭시 다이얼로그 이벤트 */
    public void dialogEvent2(final View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final ListViewItem item = (ListViewItem) adapter.getItem(position);
        builder.setTitle("상품 취소").setMessage("상품 "+item.getProd_name()+"("+new DecimalFormat("###,###").format(item.getPrice())+")을"+"취소 하시겠습니까?");
        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                /* 아무런 처리 안함 */
            }
        });
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((BCMainActivity) BCMainActivity.context_main).tot_price -= item.getPrice();
                ((BCMainActivity) BCMainActivity.context_main).items.remove(position);
                adapter.remove(position);
                refresh();
                Toast.makeText(getApplicationContext(), "상품 : " +item.getProd_name()+"가 취소되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setData() {
        ArrayList<ListViewItem> items = ((BCMainActivity) BCMainActivity.context_main).items;
        for(ListViewItem item : items) {
            adapter.addItem(item);
        }
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
        setTot_Price(((BCMainActivity) BCMainActivity.context_main).tot_price);
    }

    private void setTot_Price(int tot_price) {
        tot_price_textView = (TextView)findViewById(R.id.total_price);
        tot_price_textView.setText("총 금액 : "+new DecimalFormat("###,###").format(tot_price) + " 원");
    }
}