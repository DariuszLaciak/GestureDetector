package pl.edu.uj.laciak.gesturedetector.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrivateDatabase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "GDDB.db";
    public static final String OPTIONS_TABLE = "GestureDetectorOptions";
    public static final String DRAWING_GESTRURES_TABLE = "GestureDetectorDrawing";
    public static final String CIRCLE_GESTURES_TABLE = "CircleGesture";
    public static final String SERVER_NAME = "Receiver";
    private Context context;

    public PrivateDatabase(Context context) {
        super(context, DATABASE_NAME, null, 9);
        this.context = context;
    }

    public void saveNewDrawingGesure(String name, String action, String path, int gestureId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("path", path);
        contentValues.put("action", action);
        contentValues.put("GestureId", gestureId);
        db.insert(DRAWING_GESTRURES_TABLE, null, contentValues);
    }

    public void saveNewCircleGesure(String name, String action, String points) {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("points", points);
        contentValues.put("action", action);
        db.insert(CIRCLE_GESTURES_TABLE, null, contentValues);
    }

    public void saveOptionToPrivateDb(String option, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("option",option);
        contentValues.put("value",value);
        db.insert(OPTIONS_TABLE,null, contentValues);
    }

    public void updateOption(String option, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("option",option);
        contentValues.put("value",value);
        db.update(OPTIONS_TABLE,contentValues,"id = ? ",new String[] { Integer.toString(getOptionId(option)) });
    }

    public String getOptionValue(String option){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select value from " + OPTIONS_TABLE + " where option='" + option + "'", null);
        if(!cur.moveToNext()){
            return null;
        }
        return cur.getString(0);
    }

    public Cursor getCircleGestures() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from " + CIRCLE_GESTURES_TABLE, null);
        return cur;
    }

    public Cursor getDrawingDestures() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from " + DRAWING_GESTRURES_TABLE, null);
        return cur;
    }

    public int getLatestGestureId() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select max(GestureId) from " + DRAWING_GESTRURES_TABLE, null);
        if (!cur.moveToNext()) {
            return 0;
        }
        return cur.getInt(0);
    }

    public List<String> getCircleGestureData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cur = db.rawQuery("select name,action from " + CIRCLE_GESTURES_TABLE + " where id=" + id, null);
        if (!cur.moveToNext()) {
            return null;
        }
        list.add(cur.getString(0));
        list.add(cur.getString(1));
        return list;
    }

    public int getOptionId(String option){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select id from " + OPTIONS_TABLE + " where option='" + option + "'", null);
        if(!cur.moveToNext()){
            return 0;
        }
        return cur.getInt(0);
    }

    public int getDrawingId(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select id from " + DRAWING_GESTRURES_TABLE + " where url='" + url + "'", null);
        if (!cur.moveToNext()) {
            return 0;
        }
        return cur.getInt(0);
    }

    public String getServerUrl() {
        String url = "http://" + getOptionValue("address") + ":" + getOptionValue("port") + "/" + PrivateDatabase.SERVER_NAME;
        return url;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        checkDatabase();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+OPTIONS_TABLE+"(id integer primary key, Option VARCHAR,Value VARCHAR);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DRAWING_GESTRURES_TABLE + "(id integer primary key, Path VARCHAR,Name VARCHAR, Action VARCHAR, GestureId integer);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + CIRCLE_GESTURES_TABLE + "(id integer primary key, Points VARCHAR,Name VARCHAR, Action VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        checkDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OPTIONS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DRAWING_GESTRURES_TABLE);
        onCreate(sqLiteDatabase);
    }

    private boolean checkDatabase(){
        String path = context.getFilesDir().getPath()+"/"+context.getPackageName()+"/databases/";
        Log.d("",path);
        File dbFile = new File(path + DATABASE_NAME);
        if(dbFile.exists()){
            return true;
        }
        else{
            //This'll create the directories you wanna write to, so you
            //can put the DB in the right spot.
            dbFile.getParentFile().mkdirs();
            return false;
        }
    }

    public SQLiteDatabase getDatabase() {
        return getWritableDatabase();
    }
}
