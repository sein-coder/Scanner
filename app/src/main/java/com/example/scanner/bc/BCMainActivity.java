package com.example.scanner.bc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.scanner.db.DbOpenHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


public class BCMainActivity extends Activity {

    public static Context context_main;
    public static ArrayList<ListViewItem> items = new ArrayList<ListViewItem>();
    public static int tot_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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