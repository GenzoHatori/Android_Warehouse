package com.example.genz_com.wh;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by Genz_Com on 2017/10/06.
 */

public class Settings extends Activity {

    EditText txtwarehouse;
    EditText txtmail;
    EditText txtcount;
    String KEY_WareHouse = "WareHouse";
    String KEY_Mail = "Mail";
    String KEY_Count = "Count";
    String[] KEY = {"WareHouse", "Mail", "Count"};
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtwarehouse = (EditText) findViewById(R.id.txtwarehouse);
        txtmail = (EditText)findViewById(R.id.txtmail);
        txtcount = (EditText)findViewById(R.id.txtcount);

        sp = getSharedPreferences(KEY_WareHouse, Context.MODE_PRIVATE);
        String warehouse = sp.getString(KEY_WareHouse, "");

        sp = getSharedPreferences(KEY_Mail, Context.MODE_PRIVATE);
        String mail = sp.getString(KEY_Mail, "");

        sp = getSharedPreferences(KEY_Count, Context.MODE_PRIVATE);
        String count = sp.getString(KEY_Count, "");

        txtwarehouse.setText(warehouse);
        txtmail.setText(mail);
        txtcount.setText(count);
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
    }
}
