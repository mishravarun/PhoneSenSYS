package com.snu.msl.phonesensys.SyncAdapter.Provider;

/**
 * Created by varun on 16/9/14.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

public class Provider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.snu.msl.phonesensys.provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/datacollected";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String timestamp = "timestamp";
    public static final String firstTime = "firsttime";
    public static final String gpsLatitude = "gpslatitude";
    public static final String gpsLongitude = "gpslongitude";
    public static final String gpsAltitude = "gpsaltitude";
    public static final String phoneTemperature = "phonetemperature";
    public static final String phoneHumidity = "phonehumidity";
    public static final String phonePressure = "phonepressure";
    public static final String phoneLight = "phonelight";
    public static final String phoneSound = "phonesound";
    public static final String phoneAccX = "phoneaccx";
    public static final String phoneAccY = "phoneaccy";
    public static final String phoneAccZ = "phoneaccz";


    public static final int uriCode = 1;
    public static final UriMatcher uriMatcher;
    public  static HashMap<String, String> values;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "datacollected", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "datacollected/*", uriCode);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/datacollected";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case uriCode:
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = timestamp;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "collecteddata";
    static final String TABLE_NAME = "temperature";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (timestamp TEXT NOT NULL, "
            + " firsttime TEXT NOT NULL, "
            + " gpslatitude TEXT NOT NULL, "
            + " gpslongitude TEXT NOT NULL, "
            + " gpsaltitude TEXT NOT NULL, "
            + " phonetemperature TEXT NOT NULL, "
            + " phonehumidity TEXT NOT NULL, "
            + " phonepressure TEXT NOT NULL, "
            + " phonelight TEXT NOT NULL, "
            + " phonesound TEXT NOT NULL, "
            + " phoneaccx TEXT NOT NULL, "
            + " phoneaccy TEXT NOT NULL, "
            + " phoneaccz TEXT NOT NULL); ";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}