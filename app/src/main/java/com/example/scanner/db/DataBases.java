package com.example.scanner.db;

import android.provider.BaseColumns;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String BARCODE = "barcode";
        public static final String PROD_NAME = "prod_name";
        public static final String PRICE = "price";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +BARCODE+" text not null , "
                +PROD_NAME+" text not null , "
                +PRICE+" integer not null);";
    }
}
