package com.example.MyHealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {


    private  static  final String DB_NAME = "Watchdata.db";

    private static final int DB_VERSION = 1;

    static final String TABLE1_NAME = "Stepdetails";

   static final String _ID = "_id";
    static final String STEPS = "steps";
    static final String GRAPHDATE = "graphdate";
    static final String DISTANCE = "distance";
    static final String CALORIES = "calories";
    static final String RINGS = "rings";

    static final String TABLE2_NAME = "SPOdetails";
    static final  String SPOTIME ="_id";
    static  final  String SPODATE = "date";
    static  final  String BLOODOX = "bloodox";



    static  final  String TABLE3_NAME = "BPdetails";
    static  final  String BPTIME = "_id";
    static  final  String BPDATE = "date";
    static final  String SYSDIAL = "sysdial";


    static  final  String TABLE4_NAME = "Sleepdetails";
    static  final  String SLEEPDATE = "_id";
    static  final  String SLEEPGRAPHDATE = "graphdate";
    static final  String LIGHTSLEEP = "light";
    static final  String RESTSLEEP = "restful";
    static final  String SLEEPTOTAL = "sleeptotal";
    static final  String LIGHTPERC = "lightperc";
    static final  String RESTPERC = "restperc";
    static final  String SLEEPSCORE = "score";


    private static final String CREATE_TABLE1 = "CREATE TABLE " + TABLE1_NAME + "(" + _ID
            + " DATE PRIMARY KEY, " + STEPS + " TEXT , " + GRAPHDATE +  " DATE ,"  + DISTANCE + " TEXT , " + RINGS + " TEXT , "  + CALORIES + " TEXT);";


    private static final String CREATE_TABLE2 = " CREATE TABLE " + TABLE2_NAME + "(" + SPOTIME
            + " TIME PRIMARY KEY, " + SPODATE + " TEXT , " +BLOODOX +  " TEXT );";

    private static final String CREATE_TABLE3 = " CREATE TABLE " + TABLE3_NAME + "(" + BPTIME
            + " TIME PRIMARY KEY, " + BPDATE + " TEXT , " + SYSDIAL +  " TEXT );";

    private static final String CREATE_TABLE4 = "CREATE TABLE " + TABLE4_NAME + "(" + SLEEPDATE
            + " DATE PRIMARY KEY, " + SLEEPGRAPHDATE + " DATE , " + LIGHTSLEEP +  " TEXT ,"  + RESTSLEEP + " TEXT , " + SLEEPTOTAL + " TEXT , " + LIGHTPERC + " TEXT , "+ RESTPERC + " TEXT , " + SLEEPSCORE + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
      //  DB.execSQL("create Table Stepdetails(date DATE primary key, steps TEXT, distance TEXT, calories TEXT)");
        DB.execSQL(CREATE_TABLE1);
        DB.execSQL(CREATE_TABLE2);
        DB.execSQL(CREATE_TABLE3);
        DB.execSQL(CREATE_TABLE4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL(" DROP TABLE IF EXISTS " + TABLE1_NAME);
        DB.execSQL(" DROP TABLE IF EXISTS " + TABLE2_NAME);
        DB.execSQL(" DROP TABLE IF EXISTS " + TABLE3_NAME);
        DB.execSQL(" DROP TABLE IF EXISTS " + TABLE4_NAME);

        onCreate(DB);
    }

    public Boolean insertstepsdata(String date, String steps,String graphdate, String distance,String rings, String calories)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, date);
        contentValues.put("steps", steps);
        contentValues.put("graphdate", graphdate);
        contentValues.put("distance", distance);
        contentValues.put("calories", calories);
        contentValues.put("rings", rings);
        long result = DB.insert("Stepdetails", null, contentValues);
        if ((result == -1)) {
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertsleepdata(String date,String graphdate, String light,String restful,String lightperc, String restperc, String sleeptotal,String score)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SLEEPDATE, date);
        contentValues.put("graphdate", graphdate);
        contentValues.put("light", light);
        contentValues.put("restful", restful);
        contentValues.put("lightperc", lightperc);
        contentValues.put("restperc", restperc);
        contentValues.put("sleeptotal", sleeptotal);
        contentValues.put("score", score);
        long result = DB.insert("Sleepdetails", null, contentValues);
        if ((result == -1)) {
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertBPdata(String _id,String date, String sysdial)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BPTIME, _id);
        contentValues.put("date", date);
        contentValues.put("sysdial", sysdial);


        long result = DB.insert("BPdetails", null, contentValues);
        if ((result == -1)) {
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertSPOdata(String _id,String date, String bloodox)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPOTIME, _id);
        contentValues.put("date", date);
        contentValues.put("bloodox", bloodox);

        long result = DB.insert("SPOdetails", null, contentValues);
        if ((result == -1)) {
            return false;
        }else{
            return true;
        }
    }

    public Boolean updatestepsdata(String date, String steps
            ,String graphdate, String distance,String rings, String calories
    )
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("steps", steps);
        contentValues.put("graphdate", graphdate);
        contentValues.put("distance", distance);
        contentValues.put("calories", calories);
        contentValues.put("rings", rings);
        Cursor cursor = DB.rawQuery("Select * from Stepdetails where _id = ?", new String[] {date});
        if (cursor.getCount()>0) {

            long result = DB.update("Stepdetails", contentValues, "_id=?", new String[] {date});
            if ((result == -1)) {
                return false;
            } else {
                return true;
            }

        }else {
            return false;
        }




    }

    public Boolean updatesleepdata(String date,String graphdate, String light,String restful, String sleeptotal,String lightperc,String restperc,String score
    )
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("graphdate", graphdate);
        contentValues.put("light", light);
        contentValues.put("restful", restful);
        contentValues.put("lightperc", lightperc);
        contentValues.put("restperc", restperc);
        contentValues.put("sleeptotal", sleeptotal);
        contentValues.put("score", score);
        Cursor cursor = DB.rawQuery("Select * from Sleepdetails where _id = ?", new String[] {date});
        if (cursor.getCount()>0) {

            long result = DB.update("Sleepdetails", contentValues, "_id=?", new String[] {date});
            if ((result == -1)) {
                return false;
            } else {
                return true;
            }

        }else {
            return false;
        }




    }

    public Cursor getstepdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
       // String orderByNewest

        //   String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};

        //  Cursor cursor = DB.rawQuery("SELECT * FROM Stepdetails LIMIT 7 OFFSET (SELECT COUNT(*)FROM Stepdetails)-7;", null);
       // String query = "select * from Stepdetails where _id  >= date('now','-7 days') order by _id DESC";
        Cursor cursor = DB.rawQuery("select * from Stepdetails order by _id DESC LIMIT  7;", null);
        //Cursor cursor = DB.rawQuery(query,null);
     //   Cursor cursor = DB.rawQuery("SELECT _id(max(date, 'weekday 0', '-7 day'))WeekStart,max(-id(date, 'weekday 0, '-1 day));", null);

       if(cursor != null){
            cursor.moveToFirst();
       }
        return cursor;

    }

    public Cursor getsleepdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        // String orderByNewest

        //   String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};

        //  Cursor cursor = DB.rawQuery("SELECT * FROM Stepdetails LIMIT 7 OFFSET (SELECT COUNT(*)FROM Stepdetails)-7;", null);
        // String query = "select * from Stepdetails where _id  >= date('now','-7 days') order by _id DESC";
        Cursor cursor = DB.rawQuery("select * from Sleepdetails order by _id DESC LIMIT  7;", null);
        //Cursor cursor = DB.rawQuery(query,null);
        //   Cursor cursor = DB.rawQuery("SELECT _id(max(date, 'weekday 0', '-7 day'))WeekStart,max(-id(date, 'weekday 0, '-1 day));", null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;

    }

    public Cursor getspo2data() {
        SQLiteDatabase DB = this.getWritableDatabase();
        // String orderByNewest

        //   String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};

        //  Cursor cursor = DB.rawQuery("SELECT * FROM Stepdetails LIMIT 7 OFFSET (SELECT COUNT(*)FROM Stepdetails)-7;", null);
        // String query = "select * from Stepdetails where _id  >= date('now','-7 days') order by _id DESC";
        Cursor cursor = DB.rawQuery("select time, date, bloodox from SPOdetails order by time DESC LIMIT  7;", null);
        //Cursor cursor = DB.rawQuery(query,null);
        //   Cursor cursor = DB.rawQuery("SELECT _id(max(date, 'weekday 0', '-7 day'))WeekStart,max(-id(date, 'weekday 0, '-1 day));", null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;

    }

    public Cursor getbloodox(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT _id, date,bloodox FROM SPOdetails order by _id DESC LIMIT 7;", null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getBP(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select _id,date,sysdial from BPdetails order by _id DESC LIMIT 7;", null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public String getsleepscore(){
        SQLiteDatabase DB = this.getWritableDatabase();
        String sleepscore;
        String scorequery = "select score from Sleepdetails order by _id DESC LIMIT 1;";
        Cursor cursor = DB.rawQuery(scorequery, null);

        if (cursor.moveToFirst()){
            sleepscore = String.valueOf(cursor.getInt(0));
        }else {
            sleepscore = "0";
        }
        cursor.close();
        DB.close();

        return sleepscore;
    }

    public Cursor stepChartdata2() {
        SQLiteDatabase DB = this.getWritableDatabase();
      //  ArrayList<String> yData = new ArrayList<String>();
      //  String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};
        String query = "select graphdate, steps from Stepdetails order by _id DESC  LIMIT  7";
       // Cursor cursor = DB.rawQuery("select steps from Stepdetails order by _id DESC  LIMIT  7;", null);
        Cursor cursor = DB.rawQuery(query, null);
    /*    if(cursor != null && !cursor.isAfterLast() && cursor.moveToNext()){
            cursor.moveToFirst();
            {
                yData.add(cursor.getString(0));
            }
            cursor.close();



        } */
        return  cursor;
    }


    public ArrayList<String> dateChartdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ArrayList<String> xData = new ArrayList<String>();
        //  String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};
        String query = "select graphdate from Stepdetails order by _id DESC  LIMIT  7 ";
        // Cursor cursor = DB.rawQuery("select steps from Stepdetails order by _id DESC  LIMIT  7;", null);
        Cursor cursor = DB.rawQuery(query, null);

        if(cursor != null && !cursor.isAfterLast() && cursor.moveToNext()){
            cursor.moveToFirst();
            xData.add(cursor.getString(0));
        }
        cursor.close();

        return  xData;
    }



    public Cursor distanceChartdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        //  String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};
        Cursor cursor = DB.rawQuery("select graphdate, distance from Stepdetails order by _id DESC  LIMIT  7;", null);
        return cursor;
    }

    public Cursor stepChartdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        //  String[] columns = new String[]{_ID, STEPS,DISTANCE,CALORIES};
      //  String query = "select * from Stepdetails where _id  >= date('now','-7 days') order by _id DESC";
      //  SELECT * from "+TABLE_Birthday +" WHERE "+ KEY_Event_Date +" >= date('now','localtime', '-30 day')
     //   Cursor cursor = DB.rawQuery("SELECT graphdate,steps from \"Stepdetails\" WHERE \"_id\" >= date('now','localtime', '-7 day');", null);
        Cursor cursor = DB.rawQuery("select graphdate, steps from Stepdetails order by _id DESC  LIMIT  7;", null);
        return cursor;
    }


    public  String ringSum(){
        SQLiteDatabase DB = this.getWritableDatabase();
        String ringTotal;
        String ringQuery = "select sum(rings) from Stepdetails order by _id DESC LIMIT 7" ;
        Cursor cursor = DB.rawQuery(ringQuery, null);
         if (cursor.moveToFirst()){
             ringTotal = String.valueOf(cursor.getInt(0));
         }else {
             ringTotal = "0";
         }
         cursor.close();
         DB.close();

         return ringTotal;
    }

    public int addringData(){
        int total = 0;
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT SUM(" + (DBHelper.STEPS) + ") FROM " +DBHelper.TABLE1_NAME , null);

        if(cursor!=null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(cursor.getColumnIndex("Steps"));

            }
        }
        Cursor c = DB.rawQuery("select steps from Stepdetails order by _id DESC  LIMIT  7;", null);
        MergeCursor merged = new MergeCursor(new Cursor[]{cursor,c});
        return total;

    }


    public ArrayList<String> querydate(){
        SQLiteDatabase DB = this.getWritableDatabase();
        ArrayList<String> xData = new ArrayList<String>();

        String query = "SELECT _id FROM  Stepdetails GROUP BY _ID";

        Cursor cursor = DB.rawQuery(query, null);

       for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext());{
            xData.add(cursor.getString(0));
        }
        cursor.close();

        return  xData;
    }

    public ArrayList<String> querysteps(){
        SQLiteDatabase DB = this.getWritableDatabase();
        ArrayList<String> yData = new ArrayList<String>();

        String query = "SELECT * FROM  Stepdetails WHERE STEPS IS NOT NULL GROUP BY _id";

        Cursor cursor = DB.rawQuery(query, null);
        if (cursor !=null && cursor.moveToFirst()) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) ;
            {
                yData.add(cursor.getString(0));
            }
            cursor.close();



        }
        return yData;
    }

    public Cursor getstepdate() {
        SQLiteDatabase DB = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM" + " Stepdetails ";
        Cursor cursor = DB.rawQuery(selectQuery,null);
        cursor.moveToLast();

        return cursor;

    }


}
