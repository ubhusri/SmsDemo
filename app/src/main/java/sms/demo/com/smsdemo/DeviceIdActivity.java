package sms.demo.com.smsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceIdActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener, View.OnClickListener {

    private QRCodeReaderView qrCodeReaderView;
    private int battery;
    private EditText etDeviceId;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_id);
        etDeviceId = (EditText) findViewById(R.id.et_device_id);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        if (!ApiClient.isBaseUrlEmpty(this)) {
            if(ApiClient.getClient(this)!=null)
            apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        }


        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        batteryLevel();

        ((TextView) findViewById(R.id.tv_existing_device_id)).setText("Existing Device ID: " + new SharedPrefHelper(this).getStringKey(ApiInterface.DEVICE_ID));

        //  deviceId = Settings.Secure.getString(getContentResolver(),                Settings.Secure.ANDROID_ID);
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        etDeviceId.setText(text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    /**
     * Computes the battery level by registering a receiver to the intent triggered
     * by a battery status/level change.
     */
    public void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                battery = level;
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    @Override
    public void onClick(View view) {
        if (!ApiClient.isBaseUrlEmpty(this) && ApiClient.getClient(this)!=null) {
            apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        }else {
            return;
        }

        //Submit pressed
        new SharedPrefHelper(this).saveStringKey(ApiInterface.DEVICE_ID, etDeviceId.getText().toString().trim());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        /*
        //CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma)telephonyManager.getAllCellInfo().get(0);
//        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

*/

        String carrierName = telephonyManager.getNetworkOperatorName();
        Call<AddDeviceResponseModel> call = apiInterface.addDevice("addNewAndroidDevice", new SharedPrefHelper(this).getStringKey(ApiInterface.DEVICE_ID), carrierName, battery + "", (int) (Math.random() * 20 + 73) + "", false, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), ApiClient.genRandomNumber());
        call.enqueue(new Callback<AddDeviceResponseModel>() {
            @Override
            public void onResponse(Call<AddDeviceResponseModel> call, Response<AddDeviceResponseModel> response) {
             //   Log.i("Response Add device S", response.body().toString());
            }

            @Override
            public void onFailure(Call<AddDeviceResponseModel> call, Throwable t) {
               // Log.i("Response add device F", t.toString());
            }
        });
        finish();

    }
}
