package com.lazycoder.cakevpn.view;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.viewbinding.BuildConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс myDbHandlerBook представляет обработчик базы данных для работы с закладками.
 */
public class myDbHandlerBook extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "url";
    private static final String DATABASE_NAME = "bookmarks.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_BOOKMARK = "bookmarks";

    /**
     * Конструктор класса myDbHandlerBook.
     *
     * @param context контекст приложения
     * @param name имя базы данных
     * @param factory фабрика курсора
     * @param version версия базы данных
     */
    public myDbHandlerBook(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, 1);
    }

    /**
     * Метод onCreate создает таблицу bookmarks при создании базы данных.
     *
     * @param db объект SQLiteDatabase для работы с базой данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks(_id INTEGER PRIMARY KEY AUTOINCREMENT ,url TEXT )");
    }

    /**
     * Метод onUpgrade удаляет таблицу bookmarks и вызывает метод onCreate для обновления базы данных.
     *
     * @param db объект SQLiteDatabase для работы с базой данных
     * @param i старая версия базы данных
     * @param i1 новая версия базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }

    /**
     * Метод addUrl добавляет URL в базу данных.
     *
     * @param website объект Websites, содержащий URL
     */
    public void addUrl(Websites website) {
        ContentValues values = new ContentValues();
        values.put("url", website.get_url());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BOOKMARK, null, values);
        db.close();
    }

    /**
     * Метод deleteUrl удаляет URL из базы данных по имени.
     *
     * @param urlName имя URL для удаления
     */
    public void deleteUrl(String urlName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM bookmarks WHERE url=\"" + urlName + "\";");
    }

    /**
     * Метод deleteAllBooks удаляет все закладки из базы данных.
     */
    public void deleteAllBooks() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM bookmarks");
    }

    /**
     * Метод databaseToString возвращает список всех URL из базы данных.
     *
     * @return список URL из базы данных
     */
    @SuppressLint("Range")
    public List<String> databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        List<String> dbstring = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM bookmarks", null);
        c.moveToFirst();
        if (c.moveToNext()) {
            do {
                if (c.getString(c.getColumnIndex("url")) != null) {
                    String bstring = c.getString(c.getColumnIndex("url"));
                    dbstring.add(bstring);
                }
            } while (c.moveToNext());
            return dbstring;
        }
        return dbstring;
    }
}
