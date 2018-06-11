package com.example.genz_com.wh;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Genz_Com on 2017/10/06.
 */

public class Settings extends Activity {

    EditText txtwarehouse;
    EditText txtmail;
    EditText txtcount;
    Spinner spnsearchType;
    String KEY_WareHouse = "WareHouse";
    String KEY_Mail = "Mail";
    String KEY_Count = "Count";
    String KEY_Type = "Type";
    String[] KEY = {"WareHouse", "Mail", "Count", "Type"};
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtwarehouse = (EditText) findViewById(R.id.txtwarehouse);
        txtmail = (EditText)findViewById(R.id.txtmail);
        txtcount = (EditText)findViewById(R.id.txtcount);
        spnsearchType = (Spinner) findViewById(R.id.spnsearchType);

        sp = getSharedPreferences(KEY_WareHouse, Context.MODE_PRIVATE);
        String warehouse = sp.getString(KEY_WareHouse, "");

        sp = getSharedPreferences(KEY_Mail, Context.MODE_PRIVATE);
        String mail = sp.getString(KEY_Mail, "");

        sp = getSharedPreferences(KEY_Count, Context.MODE_PRIVATE);
        String count = sp.getString(KEY_Count, "");

        sp = getSharedPreferences(KEY_Type, Context.MODE_PRIVATE);
        String type = sp.getString(KEY_Type, "");

        String[] list = {"ALL"
                , "FG"
                , "RM"};

        spnsearchType.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));
        spnsearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String xtype;
                xtype = adapterView.getItemAtPosition(i).toString();
                setType(xtype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtwarehouse.setText(warehouse);
        txtmail.setText(mail);
        txtcount.setText(count);
        ArrayAdapter arr = (ArrayAdapter) spnsearchType.getAdapter();
        spnsearchType.setSelection(arr.getPosition(type));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sp = getSharedPreferences("WareHouse", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(KEY_WareHouse, txtwarehouse.getText().toString());
        editor.commit();

        sp = getSharedPreferences("Mail", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(KEY_Mail, txtmail.getText().toString());
        editor.commit();

        sp = getSharedPreferences("Count", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(KEY_Count, txtcount.getText().toString());
        editor.commit();

        sp = getSharedPreferences("Type", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(KEY_Type, Type.toString());
        editor.commit();
    }

        private void setType(String type){
            Type = type;
        }
}
