package com.example.genz_com.wh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//import java.net.PasswordAuthentication;

public class MainActivity extends Activity {

    AlertDialog.Builder adb;
    DBHelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    private static String SOAP_ACTION1 = "http://JSC.COM/getDataSerial";
    private static String NAMESPACE = "http://JSC.COM/";
    private static String METHOD_NAME1 = "getDataSerial";
    private static String WEB_URL = "http://192.168.10.29/WareHouseService/WHService.asmx";

    private static String webResponse = "";
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("Mail", Context.MODE_PRIVATE);
        mail = sp.getString("Mail", "");

        mHelper = new DBHelper(this);


        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final ListView listview = (ListView)findViewById(R.id.listView);

        String[] list = { "Check stock"
                , "Import Data"
                , "Export Data"
                , "Data"
                , "Send Mail"
                , "Settings"
                , "Delete Data"};

        listview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                //adb = new AlertDialog.Builder(
                //        MainActivity.this);
                //adb.setTitle("SelectedItem");
                //adb.setMessage("Selected Item is = "
                //        + listview.getItemAtPosition(position));
                Intent intent;

                switch (position){
                    case 0:
                        //adb.setMessage("Selected Item is = 0");
                        intent = new Intent(getApplicationContext(),StockActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        //adb.setMessage("Selected Item is = 1");
                        importData();
                        break;
                    case 2:
                        //adb.setMessage("Selected Item is = 2");
                        exportData();
                        break;
                    case 3:
                        //adb.setMessage("Selected Item is = 3");
                        intent = new Intent(getApplicationContext(),DataActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        //adb.setMessage("Selected Item is = 4");
                        sendMail();
                        break;
                    case 5:
                        //adb.setMessage("Selected Item is = 5");
                        intent = new Intent(getApplicationContext(),Settings.class);
                        startActivity(intent);
                        break;
                    case 6:
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("ต้องการลบข้อมูล ใช่หรือไม่ ?");
                        dlgAlert.setTitle("คำเตือน");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setPositiveButton("Cancel", null);

                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteData();
                                    }
                                });
                        dlgAlert.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                        break;
                }

                //adb.setPositiveButton("Ok", null);
                //adb.show();
            }


        });
    }

    private void deleteData(){
        new DeleteData().execute();
    }

    private void importData() {
        if (checkRowData() > 0)
        {
            Toast.makeText(MainActivity.this,"มีข้อมูลค้างอยู่ กรุณาลบออกก่อน", Toast.LENGTH_LONG).show();
            return;
        }

        new InsertData().execute();
    }

    private int checkRowData(){
        mHelper = new DBHelper(this);
        mDb = mHelper.getWritableDatabase();

        mCursor = mDb.rawQuery("SELECT WareHouse, SerialNo, ItemNMBR, ItemDesC, Act_GrossWT, Status, Location, ModDate FROM P_AllPackage Where Status = 'UNCHECK'", null);

        return mCursor.getCount();
    }

    private void exportData() {
        new ExportData().execute();
    }

    private void writeToFile(String data,Context context, String filename) {
        try {

            final File path =
                    Environment.getExternalStoragePublicDirectory
                            (
                                    //Environment.DIRECTORY_PICTURES
                                    Environment.DIRECTORY_DCIM + "/WareHouse/"
                            );

            if(!path.exists())
            {
                // Make it, if it doesn't exit
                path.mkdirs();
            }

            final File file = new File(path, filename);

            if(!file.exists()){
                file.createNewFile();
            }

            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("itemArray.txt", Context.MODE_PRIVATE));
            //outputStreamWriter.write(data);
            //outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void sendMail(){
        new SendMail().execute();
    }

    private void addAttachment(Multipart multipart, String file, String name){
        try{
            DataSource source = new FileDataSource(file);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(name);
            multipart.addBodyPart(messageBodyPart);
        }
        catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private class InsertData extends AsyncTask<Void, Integer, Integer> {
        ProgressDialog progressDialog;
        int x;
            @Override
            protected Integer doInBackground(Void... voids) {

                mHelper = new DBHelper(MainActivity.this);

                mDb = mHelper.getWritableDatabase();
                try{
                    SharedPreferences sp = getSharedPreferences("WareHouse", Context.MODE_PRIVATE);
                    String SiteID = sp.getString("WareHouse", "");

                    sp = getSharedPreferences("Count", Context.MODE_PRIVATE);
                    String count = sp.getString("Count", "");

                    sp = getSharedPreferences("Type", Context.MODE_PRIVATE);
                    String type = sp.getString("Type", "");

                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
                    request.addProperty("WareHouse",SiteID);
                    request.addProperty("Count", count);
                    request.addProperty("Type", type);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(WEB_URL);
                    androidHttpTransport.call(SOAP_ACTION1, envelope);
                    SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                    webResponse = response.toString();

                    //adb.setMessage(webResponse);

                    //Toast.makeText(getApplicationContext(),webResponse,Toast.LENGTH_SHORT).show();

                    JSONArray arr = new JSONArray(webResponse);

                    int y = arr.length();

                    JSONObject jObj;
                    String warehouse;
                    String serialno;
                    String itemnmbr;
                    String stickerdesc;
                    String act_grosswt;
                    String status;
                    String location;
                    String modedate;
                    String createdate;
                    ContentValues Val;

                    long rows;
                    mDb.beginTransaction();

                    for (int i = 0; i < arr.length() ; i++){
                        jObj = arr.getJSONObject(i);
                        warehouse = jObj.getString("WareHouse");
                        serialno = jObj.getString("SerialNo");
                        itemnmbr = jObj.getString("ItemNMBR");
                        stickerdesc = jObj.getString("StickerDesC");
                        act_grosswt = jObj.getString("ACT_GrossWT");
                        status = "UNCHECK";
                        location = "UNKNOW";
                        modedate = Calendar.getInstance().getTime().toString();
                        createdate = Calendar.getInstance().getTime().toString();

                        Val = new ContentValues();
                        Val.put("WareHouse", warehouse);
                        Val.put("SerialNo", serialno);
                        Val.put("ItemNMBR", itemnmbr);
                        Val.put("ItemDesC", stickerdesc);
                        Val.put("Act_GrossWT", act_grosswt);
                        Val.put("Status", status);
                        Val.put("Location", location);
                        Val.put("ModDate", modedate);
                        Val.put("CreateDate", createdate);

                        rows = mDb.insert("P_AllPackage", null, Val);
                        Val.clear();
                        //return (i * 100) / arr.length();
                    }
                    mDb.setTransactionSuccessful();

                    x = arr.length();

                }
                catch (Exception ex){
                    ex.printStackTrace();
                } finally {
                    mDb.endTransaction();
                    mDb.close();
                }
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                //progressDialog.setProgress(values[0]);
            }

            protected void onPostExecute(Integer result)  {
                Toast.makeText(MainActivity.this,"ดึงข้อมูลทั้งหมด " + x + " รายการ เรียบร้อย", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Import data...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //progressDialog.setProgress(0);
                //progressDialog.setMax(100);
                progressDialog.show();
            }
        }

    private class ExportData extends AsyncTask<Void, Integer, Integer>{
            ProgressDialog progressDialog;
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    String itemArray;

                    mHelper = new DBHelper(MainActivity.this);
                    mDb = mHelper.getWritableDatabase();

                    mCursor = mDb.rawQuery("SELECT Warehouse, SerialNo, ItemNMBR, ItemDesC, Act_GrossWT, Status, Location, ModDate FROM P_AllPackage Where Status = 'UNCHECK'", null);

                    int x = mCursor.getCount();
                    //if(x < 1) return;

                    //itemArray = "[";

                    final File path =
                            Environment.getExternalStoragePublicDirectory
                                    (
                                            //Environment.DIRECTORY_PICTURES
                                            Environment.DIRECTORY_DCIM + "/WareHouse/"
                                    );

                    if(!path.exists())
                    {
                        // Make it, if it doesn't exit
                        path.mkdirs();
                    }

                    final File file = new File(path, "UncheckItem.csv");

                    if(!file.exists()){
                        file.createNewFile();
                    }

                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, "tis-620");

                    itemArray = "Warehouse,SerialNo,ItemNMBR,ItemDesC,Act_GrossWT,Status,Location \n";

                    mCursor.moveToFirst();

                    while (!mCursor.isAfterLast()) {
                        itemArray += mCursor.getString(mCursor.getColumnIndex("WareHouse"));
                        itemArray += ",";

                        itemArray += "'" + mCursor.getString(mCursor.getColumnIndex("SerialNo"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemNMBR"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemDesC"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Act_GrossWT"));
                        itemArray += ",";

                        //itemArray += DateFormat.getDateTimeInstance().format(new Date()).toString();
                        //itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Status"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Location"));
                        itemArray += "\n";
                        //itemArray += "{";

                        //itemArray += "\"SerialNo\": \"";
                        //itemArray += mCursor.getString(mCursor.getColumnIndex("SerialNo"));

                        //itemArray += "\", \"ACT_GrossWT\": ";
                        //itemArray += mCursor.getString(mCursor.getColumnIndex("Act_GrossWT"));

                        //itemArray += " , \"Status\": \"";
                        //itemArray += mCursor.getString(mCursor.getColumnIndex("Status"));
                        //itemArray += "\"";

                        //itemArray += "}";
                        myOutWriter.append(itemArray);
                        itemArray = "";

                        if (mCursor.moveToNext()) ;


                    }
                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();

                    //itemArray += "]";
                    //mDb.delete("P_AllPackage", null, null);
                    //writeToFile(itemArray, getApplicationContext(), "Item.csv");
                    mDb.close();

                    mDb = mHelper.getWritableDatabase();
                    mCursor = mDb.rawQuery("SELECT Warehouse, SerialNo, ItemNMBR, ItemDesC , Act_GrossWT, Status, Location, ModDate FROM P_AllPackage Where Status = 'CHECK'", null);

                    //x = mCursor.getCount();
                    //if(x < 1) return;

                    //itemArray = "[";
                    final File file2 = new File(path, "CheckItem.csv");

                    if(!file2.exists()){
                        file2.createNewFile();
                    }

                    file2.createNewFile();
                    fOut = new FileOutputStream(file2);
                    myOutWriter = new OutputStreamWriter(fOut, "tis-620");

                    itemArray = "Warehouse,SerialNo,ItemNMBR,ItemDesC,Act_GrossWT,Status,Location \n";

                    mCursor.moveToFirst();
                    while ( !mCursor.isAfterLast() ){
                        itemArray += mCursor.getString(mCursor.getColumnIndex("WareHouse"));
                        itemArray += ",";

                        itemArray += "'" + mCursor.getString(mCursor.getColumnIndex("SerialNo"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemNMBR"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemDesC"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Act_GrossWT"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Status"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Location"));
                        itemArray += "\n";

                        myOutWriter.append(itemArray);
                        itemArray = "";

                        if (mCursor.moveToNext());
                    }

                    //writeToFile(itemArray, getApplicationContext(), "chkItem.csv");

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();

                    mDb.close();

                    mDb = mHelper.getWritableDatabase();
                    mCursor = mDb.rawQuery("SELECT Warehouse, SerialNo, ItemNMBR, ItemDesC , Act_GrossWT, Status, Location, ModDate FROM P_AllPackage Where Status = 'NEW'", null);

                    //x = mCursor.getCount();
                    //if(x < 1) return;

                    //itemArray = "[";
                    final File file3 = new File(path, "NewItem.csv");

                    if(!file3.exists()){
                        file3.createNewFile();
                    }

                    file3.createNewFile();
                    fOut = new FileOutputStream(file3);
                    myOutWriter = new OutputStreamWriter(fOut, "tis-620");

                    itemArray = "Warehouse,SerialNo,ItemNMBR,ItemDesC,Act_GrossWT,Status,Location \n";

                    mCursor.moveToFirst();
                    while ( !mCursor.isAfterLast() ){
                        itemArray += mCursor.getString(mCursor.getColumnIndex("WareHouse"));
                        itemArray += ",";

                        itemArray += "'" + mCursor.getString(mCursor.getColumnIndex("SerialNo"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemNMBR"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("ItemDesC"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Act_GrossWT"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Status"));
                        itemArray += ",";

                        itemArray += mCursor.getString(mCursor.getColumnIndex("Location"));
                        itemArray += "\n";

                        myOutWriter.append(itemArray);
                        itemArray = "";

                        if (mCursor.moveToNext());
                    }

                    //writeToFile(itemArray, getApplicationContext(), "newItem.csv");

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();

                    mDb.close();

                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                //progressDialog.setProgress(values[0]);
            }

            protected void onPostExecute(Integer result)  {
                Toast.makeText(MainActivity.this,"เขียนไฟล์ข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Export data...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //progressDialog.setProgress(0);
                //progressDialog.setMax(100);
                progressDialog.show();
            }
        }

    private class DeleteData extends AsyncTask<Void, Integer, Integer>{
        ProgressDialog progressDialog;
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                mHelper = new DBHelper(MainActivity.this);
                mDb = mHelper.getWritableDatabase();
                mDb.delete("P_AllPackage", null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            //progressDialog.setProgress(values[0]);
        }

        protected void onPostExecute(Integer result)  {
            Toast.makeText(MainActivity.this,"ลบข้อมูลทั้งหมดแล้ว", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Delete data...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setProgress(0);
            //progressDialog.setMax(100);
            progressDialog.show();
        }
    }

    private class SendMail extends AsyncTask<Void, Integer, Integer>{
        ProgressDialog progressDialog;
        @Override
        protected Integer doInBackground(Void... voids) {
            /*Intent newActivity = new Intent(Intent.ACTION_SEND);
        newActivity.putExtra(Intent.EXTRA_EMAIL, new String[]{"patanat@jscomp.com"});
        newActivity.putExtra(Intent.EXTRA_SUBJECT, "Your Default Subject");
        newActivity.putExtra(Intent.EXTRA_TEXT, "Your Default Body");
        newActivity.setType("plain/text");
        startActivity(Intent.createChooser(newActivity, "Email Sending Option :"));*/


            //Declare recipient's & sender's e-mail id.
            String destmailid = "patanat@jscomp.com";
            String sendrmailid = "patanat@jscomp.com";
            //Mention user name and password as per your configuration
            final String uname = "patanat@jscomp.com";
            final String pwd = "patanat";
            //We are using relay.jangosmtp.net for sending emails
            String smtphost = "192.168.1.4";
            Session session = null;


            if(mail == "") return null;

            Properties props = new Properties();
            props.put("mail.smtp.host", smtphost);
            props.put("mail.smtp.socketFactory.port", "25");
            //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.port", "25");

            session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("patanat@jscomp.com", "patanat");
                }
            });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("patanat@jscomp.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
                message.setSubject("Test send mail from android");


                final File path =
                        Environment.getExternalStoragePublicDirectory
                                (
                                        //Environment.DIRECTORY_PICTURES
                                        Environment.DIRECTORY_DCIM + "/Warehouse/"
                                );

                if(!path.exists())
                {
                    // Make it, if it doesn't exit
                    path.mkdirs();
                }

                final File file1 = new File(path, "UncheckItem.csv");
                final File file2 = new File(path, "CheckItem.csv");
                final File file3 = new File(path, "NewItem.csv");

                Multipart multipart = new MimeMultipart();

                //
                addAttachment(multipart, file1.toString(), "UncheckItem.csv");
                addAttachment(multipart, file2.toString(), "CheckItem.csv");
                addAttachment(multipart, file3.toString(), "NewItem.csv");
                //multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);//, "text/html; charset=utf-8");

                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            //progressDialog.setProgress(values[0]);
        }

        protected void onPostExecute(Integer result)  {
            Toast.makeText(MainActivity.this,"ส่ง mail เรียบร้อย", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Send mail...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setProgress(0);
            //progressDialog.setMax(100);
            progressDialog.show();
        }
    }
}
