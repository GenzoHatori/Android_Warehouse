package com.example.genz_com.wh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Genz_Com on 2017/10/05.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Production.db", null, 6);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_Item_TABLE = "CREATE TABLE P_AllPackage" +
                "( WareHouse Varchar(5)" +
                " , SerialNo Varchar(50) PRIMARY KEY" +
                " , ItemNMBR Varchar(50)" +
                " , ItemDesC Varchar(100)" +
                " , Act_GrossWT Decimal(18,2)" +
                " , Location Varchar(100)" +
                " , Status Varchar(10)" +
                " , ModDate Varchar(50)" +
                " , CreateDate Varchar(50))";

        db.execSQL(CREATE_Item_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_Item_TABLE = "DROP TABLE IF EXISTS P_AllPackage";

        db.execSQL(DROP_Item_TABLE);

        onCreate(db);
    }

    public void insertRecord(String serialno, String act_grosswt, String status) {
        SQLiteDatabase database = this.getWritableDatabase();

        String Insert_Item_TABLE = "Insert Into P_AllPackage (SerialNo ,Act_GrossWT, Status)"
                                + "Value ('"
                                + serialno
                                + "'," + act_grosswt
                                + "'," + status + ")";

        database.execSQL(Insert_Item_TABLE);
    }


}
