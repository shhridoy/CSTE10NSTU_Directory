package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.CREATE_TB_STUDENT);
        sqLiteDatabase.execSQL(Constants.CREATE_TB_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Constants.DROP_TB_STUDENT);
        sqLiteDatabase.execSQL(Constants.DROP_TB_FAVORITE);
        onCreate(sqLiteDatabase);
    }

    // ADD DATA IN STUDENT TABLE
    public void insertData (
            String Name, String St_id, String Mbl_no, String DateOfBirth,
            String dwn_img_url, String Mbl_no_2, String Email_1, String Email_2,
            String FB_Url, String Other_Url, String Home_City) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.name, Name);
        contentValues.put(Constants.student_id, St_id);
        contentValues.put(Constants.mobile_no, Mbl_no);
        contentValues.put(Constants.date_of_birth, DateOfBirth);
        contentValues.put(Constants.down_image_url, dwn_img_url);
        contentValues.put(Constants.mobile_no_2, Mbl_no_2);
        contentValues.put(Constants.email_1, Email_1);
        contentValues.put(Constants.email_2, Email_2);
        contentValues.put(Constants.facebook_url, FB_Url);
        contentValues.put(Constants.other_url, Other_Url);
        contentValues.put(Constants.home_city, Home_City);
        this.getWritableDatabase().insertOrThrow(Constants.STUDENT_TABLE, "", contentValues);
        this.getWritableDatabase().close();

    }

    // UPDATE DATA IN STUDENT TABLE
    public boolean updateData (String Name, String St_id, String Mbl_no,
                            String DateOfBirth, String dwn_img_url, String Mbl_no_2, String Email_1,
                            String Email_2, String FB_Url, String Other_Url, String Home_City ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.name, Name);
        contentValues.put(Constants.student_id, St_id);
        contentValues.put(Constants.mobile_no, Mbl_no);
        contentValues.put(Constants.date_of_birth, DateOfBirth);
        contentValues.put(Constants.down_image_url, dwn_img_url);
        contentValues.put(Constants.mobile_no_2, Mbl_no_2);
        contentValues.put(Constants.email_1, Email_1);
        contentValues.put(Constants.email_2, Email_2);
        contentValues.put(Constants.facebook_url, FB_Url);
        contentValues.put(Constants.other_url, Other_Url);
        contentValues.put(Constants.home_city, Home_City);

        int result = this.getWritableDatabase()
                .update(Constants.STUDENT_TABLE, contentValues, Constants.student_id+"='"+St_id+"'",null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE
    public Cursor retrieveData (){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+ Constants.STUDENT_TABLE, null);
        return cursor;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE ORDERED BY BIRTH DATE
    public Cursor retrieveDataInOrderToBirthdate () {
        Cursor c = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+ Constants.STUDENT_TABLE +" ORDER BY "+ Constants.date_of_birth+" ASC",null);
        return c;
    }

    // ADD DATA IN FAVORITE TABLE
    public void insertIntoFav(String sId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.FAV_STUD_ID, sId);
        this.getWritableDatabase().insertOrThrow(Constants.FAVORITE_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    // CHECK IF DATA IS EXISTS IN FAVORITE TABLE
    public boolean isExistsInFav(String searchItem) {
        String[] columns = { Constants.FAV_STUD_ID };
        String selection = Constants.FAV_STUD_ID + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = this.getReadableDatabase()
                .query(Constants.FAVORITE_TABLE, columns, selection, selectionArgs, null, null, null, limit);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // REMOVE A ROW FROM FAVORITE TABLE BASED ON STUDENT ID
    public boolean deleteFromFav(String sId){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(Constants.FAVORITE_TABLE, Constants.FAV_STUD_ID +" = '"+sId+"'", null);
        db.close();
        return result > 0;
    }

    // GET ALL STUDENT DATA WHICH IS ADDED IN FAVORITE TABLE
    public Cursor retrieveFavData () {
        String sql = "SELECT * FROM "+ Constants.STUDENT_TABLE+" INNER JOIN "+ Constants.FAVORITE_TABLE+" ON "+
                Constants.STUDENT_TABLE+"."+ Constants.student_id+" = "+ Constants.FAVORITE_TABLE+"."+ Constants.FAV_STUD_ID;

        return this.getReadableDatabase().rawQuery(sql, null);
    }

}
