package com.example.genz_com.wh;

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by Genz_Com on 2017/10/24.
 */

public class DoBackground extends AsyncTask<Void, Void, Void> {

    private ProgressDialog dialog;

    public DoBackground(MainActivity activity) {
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Doing something, please wait.");
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
