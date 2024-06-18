package com.mydroid.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mydroid.notesapp.Model.NoteModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "NOTES_DB";
    public static final String TABLE_NAME = "NOTES";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+TABLE_NAME+ "("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_TITLE+" TEXT,"+KEY_TEXT+" TEXT)");  // use for execute query

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public int addNote(String title, String text)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_TITLE, title);
        cv.put(KEY_TEXT, text);

        db.insert(TABLE_NAME,null,cv);
        db.close();
        return 1;
    }

    public int updateNote(NoteModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_TITLE,model.title);
        cv.put(KEY_TEXT,model.text);

        db.update(TABLE_NAME, cv, KEY_ID+" = "+model.id, null);
        db.close();
        return 1;
    }

    public int deleteNote(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID+" = ? ", new String[]{String.valueOf(id)});
        db.close();
        return 1;
    }

    public ArrayList<NoteModel> getNote()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        ArrayList<NoteModel> arrayNote =new ArrayList<>();

        while(cursor.moveToNext())
        {
            NoteModel noteModel = new NoteModel();
            noteModel.id = cursor.getInt(0);
            noteModel.title = cursor.getString(1);
            noteModel.text = cursor.getString(2);

            arrayNote.add(noteModel);
        }
        cursor.close();
        db.close();

        return arrayNote;
    }
}
