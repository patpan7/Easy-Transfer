package com.easytransfer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vouchers.db";
    private static final int DATABASE_VERSION = 1;

    // Πίνακας Voucher
    private static final String TABLE_VOUCHERS = "vouchers";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADULTS = "adults";
    private static final String COL_CHILDREN = "children";
    private static final String COL_TYPE = "type";
    private static final String COL_PICKUP = "pickup";
    private static final String COL_DROPOFF = "dropoff";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_NOTE = "note";
    private static final String COL_CREATED_AT = "created_at";

    // Πίνακας Ρυθμίσεων
    private static final String TABLE_SETTINGS = "settings";
    private static final String COL_COMPANY_TITLE = "company_title";
    private static final String COL_COMPANY_NAME = "company_name";
    private static final String COL_COMPANY_AFM = "company_afm";
    private static final String COL_COMPANY_ADDRESS = "company_address";
    private static final String COL_COMPANY_CITY = "company_city";
    private static final String COL_COMPANY_POSTCODE = "company_postcode";
    private static final String COL_COMPANY_EMAIL = "company_email";
    private static final String COL_PHONE = "company_phone";
    private static final String COL_COMPANY_MOBILE = "company_mobile";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Δημιουργία πίνακα vouchers
        String createVouchersTable = "CREATE TABLE " + TABLE_VOUCHERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADULTS + " INTEGER, " +
                COL_CHILDREN + " INTEGER, " +
                COL_TYPE + " TEXT, " +
                COL_PICKUP + " TEXT, " +
                COL_DROPOFF + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_NOTE + " TEXT, " +
                COL_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createVouchersTable);

        // Δημιουργία πίνακα settings
        String createSettingsTable = "CREATE TABLE " + TABLE_SETTINGS + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_COMPANY_TITLE + " TEXT, " +
                COL_COMPANY_NAME + " TEXT, " +
                COL_COMPANY_AFM + " TEXT, " +
                COL_COMPANY_ADDRESS + " TEXT, " +
                COL_COMPANY_CITY + " TEXT, " +
                COL_COMPANY_POSTCODE + " TEXT, " +
                COL_COMPANY_EMAIL + " TEXT, " +
                COL_COMPANY_MOBILE + " TEXT, " +
                COL_PHONE + " TEXT)";
        db.execSQL(createSettingsTable);

        // Εισαγωγή αρχικής γραμμής στις ρυθμίσεις (id = 1)
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, 1);
        cv.put(COL_COMPANY_TITLE, "");
        cv.put(COL_COMPANY_NAME, "");
        cv.put(COL_COMPANY_AFM, "");
        cv.put(COL_COMPANY_ADDRESS, "");
        cv.put(COL_COMPANY_CITY, "");
        cv.put(COL_COMPANY_POSTCODE, "");
        cv.put(COL_COMPANY_EMAIL, "");
        cv.put(COL_COMPANY_MOBILE, "");
        cv.put(COL_PHONE, "");
        db.insert(TABLE_SETTINGS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOUCHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    // Εισαγωγή voucher
    public boolean insertVoucher(String name, String email, int adults, int children, String type, String date, String time, String pickupLocation, String dropoffLocation, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_EMAIL, email);
        cv.put(COL_ADULTS, adults);
        cv.put(COL_CHILDREN, children);
        cv.put(COL_TYPE, type);
        cv.put(COL_PICKUP, pickupLocation);
        cv.put(COL_DROPOFF, dropoffLocation);
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);
        cv.put(COL_NOTE, notes);
        long result = db.insert(TABLE_VOUCHERS, null, cv);
        return result != -1;
    }

    // Αποθήκευση ρυθμίσεων
    public boolean saveSettings(String companyTitle, String companyName, String companyAfm, String companyAddress, String companyCity, String companyPostcode, String companyEmail, String companyMobile, String companyPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_COMPANY_TITLE, companyTitle);
        cv.put(COL_COMPANY_NAME, companyName);
        cv.put(COL_COMPANY_AFM, companyAfm);
        cv.put(COL_COMPANY_ADDRESS, companyAddress);
        cv.put(COL_COMPANY_CITY, companyCity);
        cv.put(COL_COMPANY_POSTCODE, companyPostcode);
        cv.put(COL_COMPANY_EMAIL, companyEmail);
        cv.put(COL_COMPANY_MOBILE, companyMobile);
        cv.put(COL_PHONE, companyPhone);
        int result = db.update(TABLE_SETTINGS, cv, COL_ID + " = ?", new String[]{"1"});
        return result > 0;
    }

    // Ανάκτηση ρυθμίσεων
    public Cursor getSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COL_ID + " = 1", null);
    }

    public Cursor getAllVouchers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VOUCHERS + " ORDER BY id DESC", null);
    }

    public Cursor getVoucherById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VOUCHERS + " WHERE id = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateVoucher(int id, String name, String email, int adults, int children,
                                 String type, String pickup, String dropoff, String date, String time, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_ADULTS, adults);
        values.put(COL_CHILDREN, children);
        values.put(COL_TYPE, type);
        values.put(COL_PICKUP, pickup);
        values.put(COL_DROPOFF, dropoff);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_NOTE, note);

        int rows = db.update(TABLE_VOUCHERS, values, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteVoucher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_VOUCHERS, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }



}
