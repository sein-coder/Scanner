package com.example.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanner.bc.BCMainActivity;
import com.example.scanner.db.DbOpenHelper;
import com.example.scanner.tf.CameraActivity;
import com.example.scanner.tf.ClassifierActivity;

public class MainActivity extends AppCompatActivity  {

    private BackPressHandler backPressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton bc_btn = findViewById(R.id.bc_btn);
        ImageButton tf_btn = findViewById(R.id.tf_btn);

        /* 바코드 스캐너 앱 실행 */
        bc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BCMainActivity.class) ;
                startActivity(intent) ;
            }
        });

        /* 이미지 분류 앱 실행 */
        tf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClassifierActivity.class) ;
                startActivity(intent) ;
            }
        });

        /*DB 초기화*/
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();
        dbOpenHelper.clearDB();
        dbOpenHelper.insertColumn("097855151612","로지텍 마우스", 100000);
        dbOpenHelper.insertColumn("8809081260028","꾸이맨", 2500);
        dbOpenHelper.insertColumn("8801045571362","진라면 순한맛(컵라면)", 1450);
        dbOpenHelper.insertColumn("8801051064223","페리오", 2000);
        dbOpenHelper.insertColumn("8801094017606","코카콜라 (500ml)", 1450);
        dbOpenHelper.insertColumn("8801121103319","카페라떼 마일드 (Maeil)", 1200);
        dbOpenHelper.insertColumn("8801007310572","햇반", 2200);
        dbOpenHelper.insertColumn("8801062636822","가나 마일드 초콜릿", 1200);


        backPressHandler = new BackPressHandler(this);



    }

    public void onBackPressed() { backPressHandler.onBackPressedClose(); }



}