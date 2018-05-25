package com.example.genz_com.wh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Genz_Com on 2017/10/05.
 */

public class DataActivity extends Activity {

    DBHelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    Item item;
    String Status;
    EditText txtsearchserial;
    EditText txtsearchitemnmbr;
    EditText txtsearchItemdesc;
    EditText txtsearchLocation;
    ListView listView1;
    int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mHelper = new DBHelper(this);

        txtsearchserial = (EditText)findViewById(R.id.txtsearchSerial);
        txtsearchserial.setText("");

        txtsearchitemnmbr = (EditText) findViewById(R.id.txtsearchItemNMBR);
        txtsearchitemnmbr.setText("");

        final Spinner spnsearchStatus = (Spinner) findViewById(R.id.spnsearchStatus);

        String[] list = {"Status"
                , "CHECK"
                , "UNCHECK"
                , "NEW"};

        spnsearchStatus.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));
        spnsearchStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status;
                if(spnsearchStatus.getSelectedItem() == "Status"){
                    status = "";
                }else{
                    status = adapterView.getItemAtPosition(i).toString();
                }

                setStatus(status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtsearchItemdesc = (EditText) findViewById(R.id.txtsearchItemDesc);
        txtsearchItemdesc.setText("");

        txtsearchLocation = (EditText) findViewById(R.id.txtsearchLocation);
        txtsearchLocation.setText("");

        Button btnsearch = (Button) findViewById(R.id.btnSearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
/*
        txtsearchserial.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {
                getData(txtsearchserial.getText().toString().trim().replace("'",""), txtsearchitemnmbr.getText().toString().trim().replace("'",""), txtsearchStatus.getText().toString().trim().replace("'",""));
            }
        });

        txtsearchitemnmbr.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {
                getData(txtsearchserial.getText().toString().trim().replace("'",""), txtsearchitemnmbr.getText().toString().trim().replace("'",""), txtsearchStatus.getText().toString().trim().replace("'",""));
            }
        });

        txtsearchStatus.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {
                getData(txtsearchserial.getText().toString().trim().replace("'",""), txtsearchitemnmbr.getText().toString().trim().replace("'",""), txtsearchStatus.getText().toString().trim().replace("'",""));
            }
        });
*/
    }
    private void getData(){
        new getData().execute();
    }

    private void setStatus(String status){
        Status = status;
    }

    private class getData extends AsyncTask<Void, Integer, CustomAdapter> {
        ProgressDialog progressDialog;

        @Override
        protected CustomAdapter doInBackground(Void... voids) {
            try{
            mHelper = new DBHelper(DataActivity.this);
            mDb = mHelper.getWritableDatabase();

            mCursor = mDb.rawQuery("SELECT WareHouse, SerialNo, ItemNMBR, ItemDesC, Act_GrossWT, Status, Location, ModDate FROM P_AllPackage"
                    + " Where SerialNo LIKE '" +  txtsearchserial.getText().toString().trim().replace("'","") + "%' "
                    + " And ItemNMBR LIKE '" + txtsearchitemnmbr.getText().toString().trim().replace("'","") + "%' "
                    + " And ItemDesc LIKE '" + txtsearchItemdesc.getText().toString().trim().replace("'","") + "%'"
                    + " And Location LIKE '" + txtsearchLocation.getText().toString().trim().replace("'","") + "%'"
                    + " And Status LIKE '" + Status + "%'", null);

            //ArrayList<String> dirArray = new ArrayList<String>();
            ArrayList<Item> itemArray = new ArrayList<>();
            listView1 = (ListView)findViewById(R.id.Listview1);
            Item item;

            x = mCursor.getCount();
            if(x < 1) {
                progressDialog.dismiss();
                return new CustomAdapter(getApplicationContext(), itemArray);
            }

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

                //listView1.setAdapter(adapter);
            //listView1.setAdapter(new ArrayAdapter(this
            //        , android.R.layout.simple_list_item_1, dirArray));
                return new CustomAdapter(getApplicationContext(), itemArray);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            //progressDialog.setProgress(values[0]);
        }

        protected void onPostExecute(CustomAdapter result)  {
            Toast.makeText(DataActivity.this,"พบข้อมูลทั้งหมด " + x + " รายการ", Toast.LENGTH_SHORT).show();
            listView1.setAdapter(result);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DataActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Search data...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setProgress(0);
            //progressDialog.setMax(100);
            progressDialog.show();
        }
    }
}
