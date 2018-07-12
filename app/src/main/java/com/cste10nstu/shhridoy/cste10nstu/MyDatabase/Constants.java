package com.cste10nstu.shhridoy.cste10nstu.MyDatabase;

/**
 * Created by Dream Land on 1/29/2018.
 */

public class Constants {

    public static final String DB_NAME = "CSTE.db";
    public static final int DB_VERSION = 1;

    // STUDENT TABLES CONSTANTS
    public static final String STUDENT_TABLE = "STUDENT";

    public static final String id = "ID";
    public static final String name = "TITLE";
    public static final String student_id = "STUDENT_ID";
    public static final String mobile_no = "MOBILE_NO";
    public static final String date_of_birth = "BIRTH_DATE";
    public static final String down_image_url = "DOWN_IMAGE_URL";
    public static final String mobile_no_2 = "MOBILE_NO_2";
    public static final String email_1 = "EMAIL_1";
    public static final String email_2 = "EMAIL_2";
    public static final String facebook_url = "FACEBOOK_URL";
    public static final String other_url = "OTHER_URL";
    public static final String home_city = "HOME_CITY";

    public static final String CREATE_TB_STUDENT = "CREATE TABLE "+ STUDENT_TABLE +
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

    public static final String DROP_TB_STUDENT = "DROP TABLE IF EXISTS " + STUDENT_TABLE;


    // FAVORITE TABLE CONSTANTS
    public static final String FAVORITE_TABLE = "favorite";
    public static final String FAV_ID = "id";
    public static final String FAV_STUD_ID = "fav_stud_id";

    public static final String CREATE_TB_FAVORITE = "CREATE TABLE " + FAVORITE_TABLE +
            "( " +
            FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FAV_STUD_ID + " TEXT NOT NULL" +
            ");";

    public static final String DROP_TB_FAVORITE = "DROP TABLE IF EXISTS " + FAVORITE_TABLE;

}
