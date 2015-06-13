package whereapp.kps.cpe.com.wheresapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 3/1/15.
 */
public class DBChecker extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydatabase";
    private static final String TABLE_MEMBER = "members";

    public DBChecker(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_MEMBER +
                "(MemberID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Name TEXT(100)," +
                " Pass TEXT(100));");

    }

    public void InsertData(String strMemberID,String strName, String strTel){
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            ContentValues Val = new ContentValues();
            Val.put("MemberID", strMemberID);
            Val.put("Name", strName);
            Val.put("Pass", strTel);

            db.insert(TABLE_MEMBER, null, Val);
            db.close();

        }catch (Exception e){

        }

    }
    public void Deletdata(String strMemberID){
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        db.delete(TABLE_MEMBER, "MemberID = ?",new String[] { String.valueOf(strMemberID) });
        db.close();

    }
    public String[] SelectData(String strMemberID) {
        try {
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_MEMBER, new String[] { "*" },
                    "MemberID=?",
                    new String[] { String.valueOf(strMemberID) }, null, null, null, null);
            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getColumnCount()];
                    /***
                     *  0 = MemberID
                     *  1 = Name
                     *  2 = Tel
                     */
                    arrData[0] = cursor.getString(0);
                    arrData[1] = cursor.getString(1);
                    arrData[2] = cursor.getString(2);
                }
            }
            cursor.close();
            db.close();
            return arrData;

        }catch (Exception e){
            return null;

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

