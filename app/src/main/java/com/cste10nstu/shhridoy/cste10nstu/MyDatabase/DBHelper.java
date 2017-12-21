package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CSTE.db";

    private static final String STUDENT_TB_NAME = "STUDENT";
    private static final String id = "ID";
    private static final String name = "TITLE";
    private static final String student_id = "STUDENT_ID";
    private static final String mobile_no = "MOBILE_NO";
    private static final String date_of_birth = "BIRTH_DATE";
    private static final String down_image_url = "DOWN_IMAGE_URL";
    private static final String mobile_no_2 = "MOBILE_NO_2";
    private static final String email_1 = "EMAIL_1";
    private static final String email_2 = "EMAIL_2";
    private static final String facebook_url = "FACEBOOK_URL";
    private static final String other_url = "OTHER_URL";
    private static final String home_city = "HOME_CITY";

    private static final String FAVORITE_TB_NAME = "FAVORITE";
    private static final String fav_id = "FAV_ID";
    private static final String stud_id = "FAV_STUD_ID";

    private static final int DB_VERSION = 1;

    private static final String CREATE_TB_STUDENT = "CREATE TABLE "+ STUDENT_TB_NAME +
            "( " +
            id +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            name +" TEXT, " +
            student_id +" TEXT UNIQUE, " +
            mobile_no + " TEXT UNIQUE, " +
            date_of_birth + " TEXT, " +
            down_image_url + " TEXT, " +
            mobile_no_2 + " TEXT, " +
            email_1 + " TEXT , " +
            email_2 + " TEXT, " +
            facebook_url + " TEXT UNIQUE, " +
            other_url + " TEXT, " +
            home_city + " TEXT);";

    private static final String CREATE_TABLE_FAVORITE = "CREATE TABLE " + FAVORITE_TB_NAME +
            "( " +
            fav_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " ;


    private static final String DROP_TB_STUDENT = "DROP TABLE IF EXISTS " + STUDENT_TB_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TB_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TB_STUDENT);
        onCreate(sqLiteDatabase);
    }

    public void insertData (
            String Name, String St_id, String Mbl_no, String DateOfBirth,
            String dwn_img_url, String Mbl_no_2, String Email_1, String Email_2,
            String FB_Url, String Other_Url, String Home_City) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(name, Name);
        contentValues.put(student_id, St_id);
        contentValues.put(mobile_no, Mbl_no);
        contentValues.put(date_of_birth, DateOfBirth);
        contentValues.put(down_image_url, dwn_img_url);
        contentValues.put(mobile_no_2, Mbl_no_2);
        contentValues.put(email_1, Email_1);
        contentValues.put(email_2, Email_2);
        contentValues.put(facebook_url, FB_Url);
        contentValues.put(other_url, Other_Url);
        contentValues.put(home_city, Home_City);
        this.getWritableDatabase().insertOrThrow(STUDENT_TB_NAME, "", contentValues);
        this.getWritableDatabase().close();

    }


    public void updateData(String oldMobileNo, String newMobileNo) {
        this.getWritableDatabase().execSQL("UPDATE "+ STUDENT_TB_NAME +" SET "+mobile_no+" = '"+newMobileNo+"' "+" WHERE "+mobile_no+" = '"+oldMobileNo+"'");
        this.getWritableDatabase().close();
    }

    public boolean updateData (String Name, String St_id, String Mbl_no,
                            String DateOfBirth, String dwn_img_url, String Mbl_no_2, String Email_1,
                            String Email_2, String FB_Url, String Other_Url, String Home_City ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(name, Name);
        contentValues.put(student_id, St_id);
        contentValues.put(mobile_no, Mbl_no);
        contentValues.put(date_of_birth, DateOfBirth);
        contentValues.put(down_image_url, dwn_img_url);
        contentValues.put(mobile_no_2, Mbl_no_2);
        contentValues.put(email_1, Email_1);
        contentValues.put(email_2, Email_2);
        contentValues.put(facebook_url, FB_Url);
        contentValues.put(other_url, Other_Url);
        contentValues.put(home_city, Home_City);

        int result = this.getWritableDatabase().update(STUDENT_TB_NAME, contentValues, student_id+"='"+St_id+"'",null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public Cursor retrieveData (){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+ STUDENT_TB_NAME, null);
        return cursor;
    }

    public Cursor retrieveDataInOrderToBirthdate () {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ STUDENT_TB_NAME +" ORDER BY "+date_of_birth+" ASC",null);
        return c;
    }

}
