package pl.edu.uj.laciak.gesturedetector.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class PrivateDatabase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "GDDB.db";
    public static final String OPTIONS_TABLE = "GestureDetectorOptions";
    private Context context;

    public PrivateDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
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

    public int getOptionId(String option){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select id from " + OPTIONS_TABLE + " where option='" + option + "'", null);
        if(!cur.moveToNext()){
            return 0;
        }
        return cur.getInt(0);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        checkDatabase();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+OPTIONS_TABLE+"(id integer primary key, Option VARCHAR,Value VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        checkDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
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
}
