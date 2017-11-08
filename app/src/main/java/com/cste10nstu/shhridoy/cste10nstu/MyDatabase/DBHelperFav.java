package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 11/8/2017.
 */

public class DBHelperFav extends SQLiteOpenHelper {

    private static final String DB_NAME = "CSTE.fav";
    private static final String TB_NAME = "FAVORITE";
    private static final int DB_VERSION = 1;

    private static final String id = "ID";
    private static final String name = "NAME";
    private static final String student_id = "STUDENT_ID";
    private static final String mobile_no = "MOBILE_NO";

    private static final String CREATE_TB = "CREATE TABLE "+TB_NAME+
            "( " +
            id +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            name +" TEXT, " +
            student_id +" TEXT UNIQUE, " +
            mobile_no + " TEXT UNIQUE);";

    private static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;

    public DBHelperFav(Context context) {
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

    public void insertFavData (String Name, String St_id, String Mbl_no) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(name, Name);
        contentValues.put(student_id, St_id);
        contentValues.put(mobile_no, Mbl_no);
        this.getWritableDatabase().insertOrThrow(TB_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteFavData(String nm){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TB_NAME, name +" = '"+nm+"'", null);
        db.close();
        return result > 0;
    }

    public Cursor retrieveFavData () {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+TB_NAME+" ORDER BY "+student_id+" ASC",null);
        return c;
    }
}
