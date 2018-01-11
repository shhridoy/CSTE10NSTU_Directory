package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CSTE.db";
    private static final int DB_VERSION = 1;

    // STUDENT TABLES VALUES
    private static final String STUDENT_TABLE = "STUDENT";
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

    private static final String CREATE_TB_STUDENT = "CREATE TABLE "+ STUDENT_TABLE +
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

    private static final String DROP_TB_STUDENT = "DROP TABLE IF EXISTS " + STUDENT_TABLE;

    // FAVORITE TABLE VALUES
    private static final String FAVORITE_TABLE = "favorite";
    private static final String FAV_ID = "id";
    private static final String FAV_STUD_ID = "fav_stud_id";

    private static final String CREATE_TB_FAVORITE = "CREATE TABLE " + FAVORITE_TABLE +
            "( " +
            FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FAV_STUD_ID + " TEXT NOT NULL" +
            ");";

    private static final String DROP_TB_FAVORITE = "DROP TABLE IF EXISTS " + FAVORITE_TABLE;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TB_STUDENT);
        sqLiteDatabase.execSQL(CREATE_TB_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TB_STUDENT);
        sqLiteDatabase.execSQL(DROP_TB_FAVORITE);
        onCreate(sqLiteDatabase);
    }

    // ADD DATA IN STUDENT TABLE
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
        this.getWritableDatabase().insertOrThrow(STUDENT_TABLE, "", contentValues);
        this.getWritableDatabase().close();

    }

    // UPDATE DATA IN STUDENT TABLE
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

        int result = this.getWritableDatabase().update(STUDENT_TABLE, contentValues, student_id+"='"+St_id+"'",null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE
    public Cursor retrieveData (){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+ STUDENT_TABLE, null);
        return cursor;
    }

    // RETRIEVE ALL DATA FROM STUDENT TABLE ORDERED BY BIRTH DATE
    public Cursor retrieveDataInOrderToBirthdate () {
        Cursor c = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+ STUDENT_TABLE +" ORDER BY "+date_of_birth+" ASC",null);
        return c;
    }

    // ADD DATA IN FAVORITE TABLE
    public void insertIntoFav(String sId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAV_STUD_ID, sId);
        this.getWritableDatabase().insertOrThrow(FAVORITE_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    // CHECK IF DATA IS EXISTS IN FAVORITE TABLE
    public boolean isExistsInFav(String searchItem) {
        String[] columns = { FAV_STUD_ID };
        String selection = FAV_STUD_ID + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = this.getReadableDatabase()
                .query(FAVORITE_TABLE, columns, selection, selectionArgs, null, null, null, limit);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // REMOVE A ROW FROM FAVORITE TABLE BASED ON STUDENT ID
    public boolean deleteFromFav(String sId){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(FAVORITE_TABLE, FAV_STUD_ID +" = '"+sId+"'", null);
        db.close();
        return result > 0;
    }

    // GET ALL STUDENT DATA WHICH IS ADDED IN FAVORITE TABLE
    public Cursor retrieveFavData () {
        String sql = "SELECT * FROM "+STUDENT_TABLE+" INNER JOIN "+FAVORITE_TABLE+" ON "+
                STUDENT_TABLE+"."+student_id+" = "+FAVORITE_TABLE+"."+FAV_STUD_ID;

        return this.getReadableDatabase().rawQuery(sql, null);
    }

}
