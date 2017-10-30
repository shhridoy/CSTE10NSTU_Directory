package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CSTE.db";
    private static final String TB_NAME = "STUDENT";

    private static final String id = "ID";
    private static final String name = "TITLE";
    private static final String student_id = "STUDENT_ID";
    private static final String mobile_no = "MOBILE_NO";
    private static final String date_of_birth = "BIRTH_DATE";
    private static final String down_image_url = "DOWN_IMAGE_URL";

    private static final int DB_VERSION = 1;

    private static final String CREATE_TB = "CREATE TABLE "+TB_NAME+
            "( " +
            id +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            name +" TEXT, " +
            student_id +" TEXT UNIQUE, " +
            mobile_no + " TEXT UNIQUE, " +
            date_of_birth + " TEXT, " +
            down_image_url + " TEXT UNIQUE);";

    private static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TB);
        onCreate(sqLiteDatabase);
    }

    public void insertData(String Name, String St_id, String Mbl_no, String DateOfBirth, String dwn_img_url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(name, Name);
        contentValues.put(student_id, St_id);
        contentValues.put(mobile_no, Mbl_no);
        contentValues.put(date_of_birth, DateOfBirth);
        contentValues.put(down_image_url, dwn_img_url);
        this.getWritableDatabase().insertOrThrow(TB_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public void updateData(String oldMobileNo, String newMobileNo) {
        this.getWritableDatabase().execSQL("UPDATE "+TB_NAME+" SET "+mobile_no+" = '"+newMobileNo+"' "+" WHERE "+mobile_no+" = '"+oldMobileNo+"'");
        this.getWritableDatabase().close();
    }

    public Cursor retrieveData (){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+TB_NAME, null);
        return cursor;
    }

}
