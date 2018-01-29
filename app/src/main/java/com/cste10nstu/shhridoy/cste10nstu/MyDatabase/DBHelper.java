package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Contants.DB_NAME, null, Contants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Contants.CREATE_TB_STUDENT);
        sqLiteDatabase.execSQL(Contants.CREATE_TB_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Contants.DROP_TB_STUDENT);
        sqLiteDatabase.execSQL(Contants.DROP_TB_FAVORITE);
        onCreate(sqLiteDatabase);
    }

    // ADD DATA IN STUDENT TABLE
    public void insertData (
            String Name, String St_id, String Mbl_no, String DateOfBirth,
            String dwn_img_url, String Mbl_no_2, String Email_1, String Email_2,
            String FB_Url, String Other_Url, String Home_City) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contants.name, Name);
        contentValues.put(Contants.student_id, St_id);
        contentValues.put(Contants.mobile_no, Mbl_no);
        contentValues.put(Contants.date_of_birth, DateOfBirth);
        contentValues.put(Contants.down_image_url, dwn_img_url);
        contentValues.put(Contants.mobile_no_2, Mbl_no_2);
        contentValues.put(Contants.email_1, Email_1);
        contentValues.put(Contants.email_2, Email_2);
        contentValues.put(Contants.facebook_url, FB_Url);
        contentValues.put(Contants.other_url, Other_Url);
        contentValues.put(Contants.home_city, Home_City);
        this.getWritableDatabase().insertOrThrow(Contants.STUDENT_TABLE, "", contentValues);
        this.getWritableDatabase().close();

    }

    // UPDATE DATA IN STUDENT TABLE
    public boolean updateData (String Name, String St_id, String Mbl_no,
                            String DateOfBirth, String dwn_img_url, String Mbl_no_2, String Email_1,
                            String Email_2, String FB_Url, String Other_Url, String Home_City ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contants.name, Name);
        contentValues.put(Contants.student_id, St_id);
        contentValues.put(Contants.mobile_no, Mbl_no);
        contentValues.put(Contants.date_of_birth, DateOfBirth);
        contentValues.put(Contants.down_image_url, dwn_img_url);
        contentValues.put(Contants.mobile_no_2, Mbl_no_2);
        contentValues.put(Contants.email_1, Email_1);
        contentValues.put(Contants.email_2, Email_2);
        contentValues.put(Contants.facebook_url, FB_Url);
        contentValues.put(Contants.other_url, Other_Url);
        contentValues.put(Contants.home_city, Home_City);

        int result = this.getWritableDatabase()
                .update(Contants.STUDENT_TABLE, contentValues, Contants.student_id+"='"+St_id+"'",null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE
    public Cursor retrieveData (){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+ Contants.STUDENT_TABLE, null);
        return cursor;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE ORDERED BY BIRTH DATE
    public Cursor retrieveDataInOrderToBirthdate () {
        Cursor c = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+ Contants.STUDENT_TABLE +" ORDER BY "+Contants.date_of_birth+" ASC",null);
        return c;
    }

    // ADD DATA IN FAVORITE TABLE
    public void insertIntoFav(String sId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contants.FAV_STUD_ID, sId);
        this.getWritableDatabase().insertOrThrow(Contants.FAVORITE_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    // CHECK IF DATA IS EXISTS IN FAVORITE TABLE
    public boolean isExistsInFav(String searchItem) {
        String[] columns = { Contants.FAV_STUD_ID };
        String selection = Contants.FAV_STUD_ID + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = this.getReadableDatabase()
                .query(Contants.FAVORITE_TABLE, columns, selection, selectionArgs, null, null, null, limit);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // REMOVE A ROW FROM FAVORITE TABLE BASED ON STUDENT ID
    public boolean deleteFromFav(String sId){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(Contants.FAVORITE_TABLE, Contants.FAV_STUD_ID +" = '"+sId+"'", null);
        db.close();
        return result > 0;
    }

    // GET ALL STUDENT DATA WHICH IS ADDED IN FAVORITE TABLE
    public Cursor retrieveFavData () {
        String sql = "SELECT * FROM "+Contants.STUDENT_TABLE+" INNER JOIN "+Contants.FAVORITE_TABLE+" ON "+
                Contants.STUDENT_TABLE+"."+Contants.student_id+" = "+Contants.FAVORITE_TABLE+"."+Contants.FAV_STUD_ID;

        return this.getReadableDatabase().rawQuery(sql, null);
    }

}
