package com.morpho.morphosample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.manvish.sampletest.R;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosmart.sdk.MorphoDevice;

import java.util.Observable;
import java.util.Observer;


public class Mymorphoactivity extends Activity implements Observer {

    public MorphoDevice morphodevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymorphoactivity);

        morphodevice = new MorphoDevice();

        USBManager.getInstance().initialize(this, "com.manvish.fingerprint.Mymorphoactivity");

        if(USBManager.getInstance().isDevicesHasPermission() == true)
        {
            Button buttonGrantPermission = (Button) findViewById(R.id.btn_grantPermission);
            buttonGrantPermission.setEnabled(false);
        }

        int ret = initModule();

    }

    public void grantPermission(View v)
    {
        USBManager.getInstance().initialize(this, "com.manvish.fingerprint.Mymorphoactivity");
    }

    private int initModule() {


        return 0;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        morphodevice.resumeConnection(30,null);

    }

    @Override
    protected void onPause()
    {
        if (morphodevice != null && ProcessInfo.getInstance().isStarted())
        {
            morphodevice.cancelLiveAcquisition();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        closeDeviceAndFinishActivity();
    }

    public void closeDeviceAndFinishActivity()
    {
        try
        {
            morphodevice.closeDevice();
        }
        catch (Exception e)
        {
            Log.e("ERROR", e.toString());
        }
        finally
        {
            morphodevice = null;

        }
    }

    @Override
    public void update(Observable observable, Object data) {

    }
}
