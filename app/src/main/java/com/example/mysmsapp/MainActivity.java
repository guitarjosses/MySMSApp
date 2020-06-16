package com.example.mysmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    String gDefaultSmsApp;

    private static final int PERMISSION_REQUEST_SMS = 123;

    private Switch s1;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
            gDefaultSmsApp = defaultSmsApp;

            Intent setSmsAppIntent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    getPackageName());
            startActivityForResult(setSmsAppIntent, PERMISSION_REQUEST_SMS);

        }

        s1 = (Switch) findViewById(R.id.switch1);

        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean activo) {

                if(activo){
                    mProgramarTarea.run();
                }else{

                    mHandler.removeCallbacks(mProgramarTarea);
/*
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, gDefaultSmsApp);
                    startActivity(intent);
*/
                }

            }
        });

    }

    private Runnable mProgramarTarea = new Runnable() {
        @Override
        public void run() {
            borrarMensajes();
            mHandler.postDelayed(this,10000);
        }
    };

    private void borrarMensajes(){

        Uri deleteUri = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            deleteUri = Uri.parse(String.valueOf(Telephony.Sms.CONTENT_URI));

        int count = 0;
        Cursor c = getApplicationContext().getContentResolver().query(deleteUri, new String[]{BaseColumns._ID}, null,
                null, null); // only query _ID and not everything
        try {
            while (c.moveToNext()) {
                // Delete the SMS
                String pid = c.getString(0); // Get _id;
                Uri.Builder uri = Telephony.Sms.CONTENT_URI.buildUpon().appendPath(pid);
                count = getApplicationContext().getContentResolver().delete(Uri.parse(uri.toString()),
                        null, null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally{
            if(c!=null) c.close(); // don't forget to close the cursor
            Snackbar.make(findViewById(R.id.myRelativeLayout), "SMSs borrados",
                    Snackbar.LENGTH_SHORT)
                    .show();
            System.out.println("SMSs borrados");

                }
        }
    }

}