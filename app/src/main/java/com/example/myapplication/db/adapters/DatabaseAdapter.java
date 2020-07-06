package com.example.myapplication.db.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.db.DatabaseSchema;
import com.example.myapplication.model.BaseModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseAdapter<Model extends BaseModel>{
    protected String LOG_TAG = "DatabaseAdapter";

    protected final SQLiteDatabase mDb;

    protected final String mTableName;

    protected final String[] mColumns;

    protected volatile SQLiteStatement mReplaceStatement;

    protected volatile SQLiteStatement mUpdateStatement;

    protected volatile SQLiteStatement mInsertStatement;

    public enum UpdateMethod {
        insert, update, replace
    };
    public DatabaseAdapter(SQLiteDatabase db, @NonNull String tableName, @NonNull String[] columns) {
        this.mTableName = tableName;
        this.mDb = db;
        this.mColumns = columns;
        if (!db.isOpen() || db.isReadOnly())
            throw new IllegalArgumentException("Database not open or is read-only. Require writeable database");

        /*if (mDb.getVersion() >= 9) {
            createTempView();
        }*/
        // LOG_TAG = getClass().getSimpleName();
    }
    public boolean isOpen(){
        return mDb.isOpen();
    }
    public void addRecord(@NonNull final Model model){
        addRecord(model, UpdateMethod.replace);
    }
    public void addRecord(@NonNull final Model model, UpdateMethod updateMethod){
        Log.d(LOG_TAG, String.format("Adding %s record to database: ", model.getClass().getSimpleName()));
//        Log.d(LOG_TAG, mDb.isReadOnly()?"readonly":"no");

        mDb.beginTransaction();
        switch(updateMethod){
            case insert:
                synchronized(getInsertStatement()) {

                    long row = setBindings(getInsertStatement(), model).executeInsert();
                    Log.i("DatabaseAdapter", setBindings(getInsertStatement(), model).toString());
                    Log.i("DatabaseAdapter", Long.toString(row));
                }
                break;
            case update:
                synchronized(getUpdateStatement()) {
                    Log.i("DatabaseAdapter_delete", "Delete wallet");

                    setBindings(getUpdateStatement(), model).execute();
                }
                break;
            default:
                synchronized(getReplaceStatement()) {
                    setBindings(getReplaceStatement(), model).execute();
                }
                break;
        }

    }
    private long doAddModels(@NonNull final List<Model> modelList, UpdateMethod updateMethod) {
        long nRow = 0;
        switch (updateMethod) {
            case update:
                synchronized(getUpdateStatement()) {
                    for (Model model : modelList) {
                        setBindings(getUpdateStatement(), model).execute();
                        nRow++;
                    }
                }
                break;
            case insert:
                synchronized(getInsertStatement()) {
                    for (Model model : modelList) {
                        setBindings(getInsertStatement(), model).executeInsert();
                        nRow++;
                    }
                }
                break;
            default:
                synchronized(getReplaceStatement()) {
                    for (Model model : modelList) {
                        setBindings(getReplaceStatement(), model).execute();
                        nRow++;
                    }
                }
                break;
        }
        return nRow;
    }

    public long bulkAddRecords(@NonNull List<Model> modelList){
        return bulkAddRecords(modelList, UpdateMethod.replace);
    }
    public long bulkAddRecords(@NonNull List<Model> modelList, UpdateMethod updateMethod) {
        if (modelList.isEmpty()) {
            Log.d(LOG_TAG, "Empty model list. Cannot bulk add records, returning 0");
            return 0;
        }

        Log.i(LOG_TAG, String.format("Bulk adding %d %s records to the database", modelList.size(),
                modelList.size() == 0 ? "null": modelList.get(0).getClass().getSimpleName()));
        long nRow = 0;
        try {
            mDb.beginTransaction();
            nRow = doAddModels(modelList, updateMethod);
            mDb.setTransactionSuccessful();
        }
        finally {
            mDb.endTransaction();
        }

        return nRow;
    }
    public abstract Model buildModelInstance(@NonNull final Cursor cursor);
    protected final @NonNull SQLiteStatement getReplaceStatement() {
        SQLiteStatement stmt = mReplaceStatement;
        if (stmt == null) {
            synchronized (this) {
                stmt = mReplaceStatement;
                if (stmt == null) {
                    mReplaceStatement = stmt
                            = mDb.compileStatement("REPLACE INTO " + mTableName + " ( "
                            + TextUtils.join(" , ", mColumns) + " , "
                            + DatabaseSchema.CommonColumns.COLUMN_UID
                            + " ) VALUES ( "
                            + (new String(new char[mColumns.length]).replace("\0", "? , "))
                            + "?)");
                }
            }
        }
        return stmt;
    }
    protected final @NonNull SQLiteStatement getUpdateStatement() {
        SQLiteStatement stmt = mUpdateStatement;
        if (stmt == null) {
            synchronized (this) {
                stmt = mUpdateStatement;
                if (stmt == null) {
                    mUpdateStatement = stmt
                            = mDb.compileStatement("UPDATE " + mTableName + " SET "
                            + TextUtils.join(" = ? , ", mColumns) + " = ? WHERE "
                            + DatabaseSchema.CommonColumns.COLUMN_UID
                            + " = ?");
                }
            }
        }
        return stmt;
    }
    protected final @NonNull SQLiteStatement getInsertStatement() {
        SQLiteStatement stmt = mInsertStatement;
        Log.i("DatabaseAdapter", mDb.isOpen()?"Open":"Close");
        if (stmt == null) {
            synchronized (this) {
                stmt = mInsertStatement;
                if (stmt == null) {
                    mInsertStatement = stmt
                            = mDb.compileStatement("INSERT INTO " + mTableName + " ( "
                            + TextUtils.join(" , ", mColumns) + " , "
                            + DatabaseSchema.CommonColumns.COLUMN_UID
                            + " ) VALUES ( "
                            + (new String(new char[mColumns.length]).replace("\0", "? , "))
                            + "? )");
                }
            }
        }
        return stmt;
    }

    protected abstract @NonNull SQLiteStatement setBindings(@NonNull SQLiteStatement stmt, @NonNull final Model model);
    public Model getRecord(@NonNull String uid){
        Log.v(LOG_TAG, "Fetching record with GUID " + uid);

        Cursor cursor = fetchRecord(uid);
        try {
            if (cursor.moveToFirst()) {
                return buildModelInstance(cursor);
            }
            else {
                throw new IllegalArgumentException(LOG_TAG + ": Record with " + uid + " does not exist");
            }
        } finally {
            cursor.close();
        }
    }
    public Model getRecord(long id){
        return getRecord(getUID(id));
    }
    public List<Model> getAllRecords(){
        List<Model> modelRecords = new ArrayList<>();
        Cursor c = fetchAllRecords();
        try {
            while (c.moveToNext()) {
                modelRecords.add(buildModelInstance(c));
            }
        } finally {
            c.close();
        }
        return modelRecords;
    }
    protected ContentValues extractBaseModelAttributes(@NonNull ContentValues contentValues, @NonNull Model model){
        contentValues.put(DatabaseSchema.CommonColumns.COLUMN_UID, model.getUID());
        contentValues.put(DatabaseSchema.CommonColumns.COLUMN_CREATED_AT,
                model.getCreatedTimestamp().toString());
        // modified

        return contentValues;
    }
    protected void populateBaseModelAttributes(Cursor cursor, BaseModel model){
        String uid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.CommonColumns.COLUMN_UID));
        String created = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.CommonColumns.COLUMN_CREATED_AT));
        String modified= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.CommonColumns.COLUMN_MODIFIED_AT));

        model.setUID(uid);
        model.setCreatedTimestamp(Timestamp.valueOf(created));
        model.setModifiedTimestamp(Timestamp.valueOf(modified));
    }
    public Cursor fetchRecord(long rowId){
        return mDb.query(mTableName, null, DatabaseSchema.CommonColumns._ID + "=" + rowId,
                null, null, null, null);
    }
    public Cursor fetchRecord(@NonNull String uid){
        return mDb.query(mTableName, null, DatabaseSchema.CommonColumns.COLUMN_UID + "=?" ,
                new String[]{uid}, null, null, null);
    }
    public Cursor fetchAllRecordsWithUID(List<String> uids){

        return mDb.query(mTableName, null, "uid IN ( \"" + String.join("\", \"", uids) + "\" )"
                , null, null, null, null);
    }
    public Cursor fetchAllRecordsWithUID_Category(List<String> uids){

        return mDb.query(mTableName, null, "uid IN ( \"" + String.join("\", \"", uids) + "\" )"
                , null, null, null, "category" );
    }
    public Cursor fetchAllRecordsWithUID_Amount(List<String> uids){
        return mDb.query(mTableName, null, "uid IN ( \"" + String.join("\", \"", uids) + "\" )"
                , null, null, null, "amount" );
    }
    public Cursor fetchAllRecords(){
        return fetchAllRecords(null, null, null);
    }
    public Cursor fetchAllRecords(String where, String[] whereArgs, String orderBy){
        return mDb.query(mTableName, null, where, whereArgs, null, null, orderBy);
    }
    public boolean deleteRecord(long rowId){
        Log.d("Deleting", "Deleting record with id " + rowId + " from " + mTableName);
        return mDb.delete(mTableName, DatabaseSchema.CommonColumns._ID + "=" + rowId, null) > 0;
    }
    public int deleteAllRecords(){
        return mDb.delete(mTableName, null, null);
    }
    public long getID(@NonNull String uid){
        Cursor cursor = mDb.query(mTableName,
                new String[] {DatabaseSchema.CommonColumns._ID},
                DatabaseSchema.CommonColumns.COLUMN_UID + " = ?",
                new String[]{uid},
                null, null, null);
        long result = -1;
        try{
            if (cursor.moveToFirst()) {
                result = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseSchema.CommonColumns._ID));
            } else {
                throw new IllegalArgumentException(mTableName + " with GUID " + uid + " does not exist in the db");
            }
        } finally {
            cursor.close();
        }
        return result;
    }
    public String getUID(long id){
        Cursor cursor = mDb.query(mTableName,
                new String[]{DatabaseSchema.CommonColumns.COLUMN_UID},
                DatabaseSchema.CommonColumns._ID + " = " + id,
                null, null, null, null);

        String uid = null;
        try {
            if (cursor.moveToFirst()) {
                uid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.CommonColumns.COLUMN_UID));
            } else {
                throw new IllegalArgumentException(mTableName + " Record ID " + id + " does not exist in the db");
            }
        } finally {
            cursor.close();
        }
        return uid;
    }

    protected int updateRecord(String tableName, long recordId, String columnKey, String newValue) {
        ContentValues contentValues = new ContentValues();
        if (newValue == null) {
            contentValues.putNull(columnKey);
        } else {
            contentValues.put(columnKey, newValue);
        }
        return mDb.update(tableName, contentValues,
                DatabaseSchema.CommonColumns._ID + "=" + recordId, null);
    }
    public int updateRecord(@NonNull String uid, @NonNull String columnKey, String newValue) {
        return updateRecords(DatabaseSchema.CommonColumns.COLUMN_UID + "= ?", new String[]{uid}, columnKey, newValue);
    }
    public int updateRecord(@NonNull String uid, @NonNull ContentValues contentValues){
        return mDb.update(mTableName, contentValues, DatabaseSchema.CommonColumns.COLUMN_UID + "=?", new String[]{uid});
    }
    public int updateRecords(String where, String[] whereArgs, @NonNull String columnKey, String newValue){
        ContentValues contentValues = new ContentValues();
        if (newValue == null) {
            contentValues.putNull(columnKey);
        } else {
            contentValues.put(columnKey, newValue);
        }
        return mDb.update(mTableName, contentValues, where, whereArgs);
    }
    public boolean deleteRecord(@NonNull String uid){
        return deleteRecord(getID(uid));
    }
    public long getRecordsCount(){
        String sql = "SELECT COUNT(*) FROM " + mTableName;
        SQLiteStatement statement = mDb.compileStatement(sql);
        return statement.simpleQueryForLong();
    }
    public void beginTransaction() {
        mDb.beginTransaction();
    }
    public void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();
    }
    public void enableForeignKey(boolean enable) {
        if (enable){
            mDb.execSQL("PRAGMA foreign_keys=ON;");
        } else {
            mDb.execSQL("PRAGMA foreign_keys=OFF;");
        }
    }
    public void endTransaction() {
        mDb.endTransaction();
    }

/*
    public String getAttribute(@NonNull String recordUID, @NonNull String columnName){
        return getAttribute(mTableName, recordUID, columnName);
    }
    protected String getAttribute(@NonNull String tableName, @NonNull String recordUID, @NonNull String columnName){
        Cursor cursor = mDb.query(tableName,
                new String[]{columnName},
                AccountEntry.COLUMN_UID + " = ?",
                new String[]{recordUID}, null, null, null);

        try {
            if (cursor.moveToFirst())
                return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
            else {
                throw new IllegalArgumentException(String.format("Record with GUID %s does not exist in the db", recordUID));
            }
        } finally {
            cursor.close();
        }
    }*/

}
