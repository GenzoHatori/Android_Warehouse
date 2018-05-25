package com.example.genz_com.wh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Genz_Com on 2017/10/06.
 */

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Item> mitemArray;

    public CustomAdapter(Context context, ArrayList<Item> itemArray) {
        this.mContext= context;
        mitemArray = itemArray;
    }

    @Override
    public int getCount() {
        return mitemArray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.menu_row, parent, false);

        TextView txtserialno = (TextView) view.findViewById(R.id.txtSerialNo);
        txtserialno.setText(mitemArray.get(position).SerialNo);

        TextView txtitemnmbr = (TextView) view.findViewById(R.id.txtItemNMBR);
        txtitemnmbr.setText(mitemArray.get(position).ItemNMBR);

        TextView txtitemdesc = (TextView) view.findViewById(R.id.txtItemDesc);
        txtitemdesc.setText(mitemArray.get(position).ItemDesC);

        TextView txtactgross = (TextView) view.findViewById(R.id.txtActGross);
        txtactgross.setText(mitemArray.get(position).Act_GrossWT);

        String xStatus;
        int bgcolor;
        int textcolor;
        switch (mitemArray.get(position).Status.toUpperCase()){
            case "NEW":
                xStatus = "ไม่มีในคลัง";
                bgcolor = Color.BLUE;
                textcolor = Color.WHITE;
                break;
            case "CHECK":
                xStatus = "ตรวจแล้ว";
                bgcolor = Color.GREEN;
                textcolor = Color.BLACK;
                break;
            default:
                xStatus = "ยังไม่ได้ตรวจ";
                bgcolor = Color.RED;
                textcolor = Color.WHITE;
                break;
        }

        TextView txtstatus = (TextView) view.findViewById(R.id.txtStatus);
        txtstatus.setText(xStatus);

        TextView txtlocation = (TextView) view.findViewById(R.id.txtLocation);
        txtlocation.setText(mitemArray.get(position).Location);

        txtstatus.setBackgroundColor(bgcolor);
        txtstatus.setTextColor(textcolor);
        return view;
    }
}
