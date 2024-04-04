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
 * Класс MydbHandler представляет собой помощник для работы с базой данных SQLite, используемой для хранения URL-адресов.
 */
public class MydbHandler extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "url";
    private static final String DATABASE_NAME = "sites.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_SITES = "sites";

    /**
     * Конструктор класса MydbHandler.
     *
     * @param context  контекст приложения
     * @param name     имя базы данных
     * @param factory  фабрика для создания курсоров
     * @param version  версия базы данных
     */
    public MydbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы "sites" при создании базы данных
        db.execSQL("CREATE TABLE sites(_id INTEGER PRIMARY KEY AUTOINCREMENT ,url TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Удаление таблицы "sites" при обновлении базы данных
        db.execSQL("DROP TABLE IF EXISTS sites");
        onCreate(db);
    }

    /**
     * Метод для добавления URL-адреса в базу данных.
     *
     * @param website  объект Websites, содержащий URL-адрес
     */
    public void addUrl(Websites website) {
        ContentValues values = new ContentValues();
        values.put("url", website.get_url());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SITES, null, values);
        db.close();
    }

    /**
     * Метод для удаления URL-адреса из базы данных по имени.
     *
     * @param urlName  имя URL-адреса для удаления
     */
    public void deleteUrl(String urlName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM sites WHERE url=\"" + urlName + "\";");
    }

    /**
     * Метод для удаления всех URL-адресов из базы данных.
     */
    public void deleteAllUrls() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM sites");
    }

    /**
     * Метод для получения всех URL-адресов из базы данных в виде списка строк.
     *
     * @return список URL-адресов
     */
    @SuppressLint("Range")
    public List<String> databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        List<String> dbstring = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM sites", null);
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
