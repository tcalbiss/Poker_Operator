package mobpro.hslu.poker_operator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;

import mobpro.hslu.poker_operator.Contract.DbObject;

/**
 * Created by manager on 04.05.2015.
 */
public class DbAdapter {
    public static String DB_NAME = "Poker_Operator";
    public static int DB_Version = 1;

    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(final Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        if(db==null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
        db.close();
        db = null;
    }

    public long CreateDbObject(DbObject dbObject) {

        long i=-1;
        if(db.isOpen() && !db.isReadOnly()) {
            db = dbHelper.getWritableDatabase();
            try {
                ContentValues contentValues = dbObject.getContentValues();
                contentValues.remove(DbHelper.COLUMN_ID);
                i = db.insert(dbObject.getTableName(), null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    /**
     * Returns ContentValues of a dbObject's id
     * @param dbObject Object filled with at least the ID
     * @return All ColumnData's of supplied Object
     */
    public ContentValues getByObject(DbObject dbObject) {
        ContentValues contentValues = new ContentValues();

        if(dbObject.getPrimaryFieldValue()!=null) {
            String dbQuery = "Select * from " + dbObject.getTableName()+" WHERE " +
                    dbObject.getPrimaryFieldName() + " =?";
            String [] args = new String[]{dbObject.getPrimaryFieldValue()};
            Cursor result = db.rawQuery(dbQuery, args);
            if(result.moveToFirst()) {
                for(int i =0; i<result.getColumnCount(); i++){
                    contentValues.put(result.getColumnName(i), result.getString(i));
                }
            }
        }

        return contentValues;
    }

    public Collection<ContentValues> getAllByTable(String table){
        Collection<ContentValues> allContentValues = new ArrayList<>();;
        try {
            String selectQuery = "Select * from " + table;

            Cursor result = db.rawQuery(selectQuery, null);
            if (result.moveToFirst()) {

                do {
                    ContentValues contentValues = new ContentValues();
                    for (int i = 0; i < result.getColumnCount(); i++) {
                        contentValues.put(result.getColumnName(i), result.getString(i));
                    }
                    allContentValues.add(contentValues);
                } while (result.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return allContentValues;
    }

    public boolean updateDbObject(DbObject dbObject) {
        return db.update(dbObject.getTableName(), dbObject.getContentValues(),
                dbObject.getPrimaryFieldName() + "=" + dbObject.getPrimaryFieldValue(), null)>0;
    }

    public boolean deleteDbObject(DbObject dbObject) {
        return db.delete(dbObject.getTableName(), dbObject.getPrimaryFieldName() +
                "=" + dbObject.getPrimaryFieldValue(), null)>0;
    }


}
