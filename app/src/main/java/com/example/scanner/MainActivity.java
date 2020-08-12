package com.example.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static Context context_main;
    public static ArrayList<ListViewItem> items = new ArrayList<ListViewItem>();
    public static int tot_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*DB 초기화*/
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();
        dbOpenHelper.clearDB();
        dbOpenHelper.insertColumn("097855151612","로지텍 마우스", 100000);
        dbOpenHelper.insertColumn("8809081260028","꾸이꾸이", 2500);
        dbOpenHelper.insertColumn("8801045571362","진라면 순한맛(컵라면)", 1450);
        dbOpenHelper.insertColumn("8801051064223","페리오", 2000);
        dbOpenHelper.insertColumn("8801094017606","코카콜라(500ml)", 1450);
        dbOpenHelper.insertColumn("8801121103319","카페라떼 마일드(Maeil)", 1200);
        dbOpenHelper.insertColumn("8801007310572","햇반", 2200);
        dbOpenHelper.insertColumn("8801062636822","가나 초콜릿 마일드", 1200);
    }

    protected void onResume(){
        super.onResume();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("onActivityResult", "onActivityResult: .");
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String re = scanResult.getContents();
            String message = re;
            Log.d("onActivityResult", "onActivityResult: ." + re);
            DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
            dbOpenHelper.open();
            dbOpenHelper.create();

            Cursor cursor = dbOpenHelper.selectColumns();
            while (cursor.moveToNext()) {
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                String prod_name = cursor.getString(cursor.getColumnIndex("prod_name"));
                int price = cursor.getInt(cursor.getColumnIndex("price"));
                if(barcode.equals(re)) {
                    tot_price+=price;
                    ListViewItem item = new ListViewItem();
                    item.setProd_name(prod_name);
                    item.setPrice(price);
                    items.add(item);
                    break;
                }
            }
        }
    }


}