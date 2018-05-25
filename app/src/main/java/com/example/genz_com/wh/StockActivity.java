package com.example.genz_com.wh;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Genz_Com on 2017/10/10.
 */

public class StockActivity extends Activity{
    DBHelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    Item item;
    String wareHouse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        SharedPreferences sp = getSharedPreferences("WareHouse", Context.MODE_PRIVATE);
        wareHouse = sp.getString("WareHouse", "");

        mHelper = new DBHelper(this);

        final EditText txtlocation = (EditText) findViewById(R.id.txtLocation);

        final EditText txtserial = (EditText) findViewById(R.id.txtSerial);
        txtserial.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() != 66) return false;
                //Toast.makeText(getApplicationContext(),"ENTER",Toast.LENGTH_SHORT).show();

                long rows = 0;

                String serial = "";
                String location = "";
                serial = txtserial.getText().toString().trim().replace("'","");
                location = txtlocation.getText().toString().trim().replace("'","");

                if(serial.length() < 10) return true;

                try {
                    //mDb = mHelper.getWritableDatabase();
                    rows = getData(serial);

                    if (rows < 1) {
                        InsertData(serial, location);
                    }else{
                        UpdateData(serial, location);
                    }

                    txtserial.selectAll();
                    txtserial.setText("");

                    rows = getData(serial);
                    Toast.makeText(StockActivity.this, "Success!" + rows, Toast.LENGTH_SHORT).show();
                    //mDb.close();
                    return true;
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    return false;
                }
            }
        });
    }

    private int getData(String txtsearch){
        mDb = mHelper.getWritableDatabase();

        mCursor = mDb.rawQuery("SELECT WareHouse, SerialNo, ItemNMBR, ItemDesC, Act_GrossWT, Status, Location, ModDate FROM P_AllPackage"
                + " Where SerialNo = '" +  txtsearch + "'", null);

        int x = mCursor.getCount();
        if(x < 1) return 0;

        //ArrayList<String> dirArray = new ArrayList<String>();
        ArrayList<Item> itemArray = new ArrayList<>();
        Item item;

        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            item = new Item();

            item.WareHouse = mCursor.getString(mCursor.getColumnIndex("WareHouse"));
            item.SerialNo = mCursor.getString(mCursor.getColumnIndex("SerialNo"));
            item.ItemNMBR = mCursor.getString(mCursor.getColumnIndex("ItemNMBR"));
            item.ItemDesC = mCursor.getString(mCursor.getColumnIndex("ItemDesC"));
            item.Act_GrossWT = mCursor.getString(mCursor.getColumnIndex("Act_GrossWT"));
            item.Status = mCursor.getString(mCursor.getColumnIndex("Status"));
            item.Location = mCursor.getString(mCursor.getColumnIndex("Location"));
            item.ModDate = mCursor.getString(mCursor.getColumnIndex("ModDate"));
            //dirArray.add(mCursor.getString(mCursor.getColumnIndex("id")));
            //dirArray.add(mCursor.getString(mCursor.getColumnIndex("SerialNo")));
            //dirArray.add(mCursor.getString(mCursor.getColumnIndex("Act_GrossWT")));
            //dirArray.add(mCursor.getString(mCursor.getColumnIndex("Status")));

            itemArray.add(item);
            mCursor.moveToNext();
        }

        mDb.close();
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), itemArray);

        ListView listView1 = (ListView)findViewById(R.id.Listview1);

        listView1.setAdapter(adapter);
        //listView1.setAdapter(new ArrayAdapter(this
        //        , android.R.layout.simple_list_item_1, dirArray));
        return x;
    }

    private void UpdateData(String serial, String location){
        String status = "CHECK";
        Date date = Calendar.getInstance().getTime();
        ContentValues Val = new ContentValues();
        Val.put("Status", status);
        Val.put("Location", location);
        Val.put("ModDate", date.toString());

        mDb = mHelper.getWritableDatabase();

        long rows = mDb.update("P_AllPackage", Val, "SerialNo = ? AND Status = ? "
                , new String[] { String.valueOf(serial), "UNCHECK"});

        mDb.close();
    }

    private void InsertData(String serialno, String location){
        String status = "NEW";
        ContentValues Val = new ContentValues();
        Val.put("WareHouse", "");
        Val.put("SerialNo", serialno);
        Val.put("ItemNMBR", "");
        Val.put("ItemDesC", "");
        Val.put("Act_GrossWT", "0");
        Val.put("Status", status);
        Val.put("Location", location);

        mDb = mHelper.getWritableDatabase();

        long rows = mDb.insert("P_AllPackage", null, Val);
        mDb.close();
    }
}
