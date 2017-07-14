package sms.demo.com.smsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, USSDInterface, LockDialogFragment.LockDialogListener, OutgoingCallResponseInterface {


    ApiInterface apiInterface;
    Button btnSettings, btnStartStop;
    ApiInterface apiService;
    private String id = "";
    OutgoingCallReceiver outgoingCallReceiver;
    // Create the Handler object (on the main thread by default)
    Handler handler, handlerSecond;
    // Define the code block to be executed
    private Runnable runnableCode, runnableCodeSecond;
    USSDReceiver ussdReceiver;

    // Start the initial runnable task by posting through the handler
    private void dialNumber(String code) {
        String ussdCode = code.replaceAll("#", Uri.encode("#"));
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ApiClient.isBaseUrlEmpty(this)) {
            if (ApiClient.getClient(this) != null)
                apiService = ApiClient.getClient(this).create(ApiInterface.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bugsnag.init(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_device_id)).setText("DEVICE ID: " + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        startService(new Intent(this, XXX.class));
        ussdReceiver = new USSDReceiver(this);
        outgoingCallReceiver = new OutgoingCallReceiver(this);
        registerReceiver(ussdReceiver, new IntentFilter("com.times.ussd.action.REFRESH"));
        registerReceiver(outgoingCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));


        LocalBroadcastManager.getInstance(this).registerReceiver(
                new MyReceiver(), new IntentFilter("speedExceeded"));

        btnSettings = (Button) findViewById(R.id.btn_settings);
        btnStartStop = (Button) findViewById(R.id.btn_start_stop);
        btnSettings.setOnClickListener(this);
        if (new SharedPrefHelper(this).getStringKey("base_url").equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Base url first", Toast.LENGTH_SHORT).show();
        }
        btnStartStop.setOnClickListener(this);
        findViewById(R.id.btn_lock).setOnClickListener(this);
        if (!ApiClient.isBaseUrlEmpty(this)) {
            if (ApiClient.getClient(this) != null)
                apiService = ApiClient.getClient(this).create(ApiInterface.class);
        }
        handler = new Handler();
        handlerSecond = new Handler();
        runnableCodeSecond = new Runnable() {
            @Override
            public void run() {
                if (!ApiClient.isBaseUrlEmpty(MainActivity.this)) {
                    if (ApiClient.getClient(MainActivity.this) != null)
                        apiInterface = ApiClient.getClient(MainActivity.this).create(ApiInterface.class);
                }
                if (apiInterface != null) {
                    handler.post(runnableCode);
                    String deviceId = new SharedPrefHelper(MainActivity.this).getStringKey(ApiInterface.DEVICE_ID);
                    Call<Void> call = apiInterface.sendIncomingSms("newMessage", deviceId, Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID), "No Reply Message Received", ApiClient.genRandomNumber());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Log.i("Response MessageSuccess", response.body().toString());
                            //new SmsHelper(MainActivity.this).sendSMS(response.body().to,response.body().message);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            //     Log.i("Response MessageFailure", t.toString());
                            //     Toast.makeText(context, "Not a valid device", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };


        runnableCode = new
                Runnable() {
                    @Override
                    public void run() {
                        if (apiService != null) {
                            final String deviceId = new SharedPrefHelper(MainActivity.this).getStringKey(ApiInterface.DEVICE_ID);
                            //  Call<DataModel> call = apiService.getReqStatus("getQueue", "airtel8675413483", "lorem341", "1234567890");
                            Call<DataModel> call = apiService.getReqStatus("getQueue", deviceId, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), ApiClient.genRandomNumber());

                            call.enqueue(new Callback<DataModel>() {
                                @Override
                                public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                                    //Log.i("Response Success", response.body().toString());
                                    if (response.body() == null) {
                                        return;
                                    }

                                    if (response.body().method.equals("ussd")) {
                                        handler.postDelayed(runnableCode, 7000);
                                        id = response.body().id;
                                        dialNumber(response.body().format);
                                    } else if (response.body().method.equals("ussd_multi")) {
                                        handler.postDelayed(runnableCode, 7000);
                                        dialNumber(response.body().format);
                                        String[] replies = response.body().reply.split(",");
                                    } else {
//                                        handler.postDelayed(runnableCode, response.body().time == 0 ? 7000 : response.body().time * 1000);
                                        findViewById(R.id.textViewWaiting).setVisibility(View.VISIBLE);
                                        handlerSecond.postDelayed(runnableCodeSecond,response.body().time == 0 ? 7000 : response.body().time * 1000);

                                        new SmsHelper(MainActivity.this).sendSMS(response.body().to, response.body().message);
                                        apiService.sendSentMessageStatus("rechargeStatusUpdate", deviceId, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), response.body().id, "Message Sent", ApiClient.genRandomNumber()).enqueue(new Callback<DataModel>() {
                                            @Override
                                            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                                                Log.i("Response Success", response.body().toString());
                                            }

                                            @Override
                                            public void onFailure(Call<DataModel> call, Throwable t) {
                                                Log.i("Response Success", t.toString());
                                            }
                                        });
                                    }


                                }

                                @Override
                                public void onFailure(Call<DataModel> call, Throwable t) {
                                    Log.i("Response Failure", t.toString());
                                    Toast.makeText(MainActivity.this, "Running", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }

        ;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settings:
                startActivity(new Intent(this, DeviceIdActivity.class));
                break;
            case R.id.btn_start_stop:
                if (ApiClient.isBaseUrlEmpty(this)) {
                    return;
                } else {
                    if (ApiClient.getClient(this) != null)
                        apiService = ApiClient.getClient(this).create(ApiInterface.class);
                }

                if (!new SharedPrefHelper(this).getStringKey(ApiInterface.DEVICE_ID).equalsIgnoreCase("")) {
                    if (btnStartStop.getText().toString().equalsIgnoreCase(getString(R.string.start))) {
                        //Do stop
                        btnStartStop.setText(getString(R.string.stop));
                        start();
                    } else {
                        //Do start
                        btnStartStop.setText(getString(R.string.start));
                        stop();
                    }
                } else {
                    Toast.makeText(this, "Kindly save device id first by going to settings screen", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_lock:
                showNoticeDialog();
                break;
            case R.id.btn_skip:
                handler.post(runnableCode);
                handlerSecond.removeCallbacks(runnableCodeSecond);
                findViewById(R.id.textViewWaiting).setVisibility(View.INVISIBLE);
                break;

        }
    }

    private void start() {
        handler.post(runnableCode);

    }

    @Override
    protected void onDestroy() {
        //      unregisterReceiver(ussdReceiver);
        super.onDestroy();
    }

    private void stop() {
        handler.removeCallbacks(runnableCode);

    }

    @Override
    public void parseMessage(String text) {
        if (apiService == null) {
            return;
        }
        String deviceId = new SharedPrefHelper(this).getStringKey(ApiInterface.DEVICE_ID);
        //   Call<DataModel> call = apiService.getReqStatus("getQueue", "airtel8675413483", "lorem341", "1234567890");
        Call<Void> call = apiService.sendUSSDMessage("newMessage", deviceId, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), text, "ussd_status_responce", id, ApiClient.genRandomNumber());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Log.i("Response MessageSuccess", response.body().toString());
                //new SmsHelper(MainActivity.this).sendSMS(response.body().to,response.body().message);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //     Log.i("Response MessageFailure", t.toString());
                //     Toast.makeText(context, "Not a valid device", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LockDialogFragment();
        dialog.show(getSupportFragmentManager(), "LockDialogFragment");
    }

    @Override
    public void onDialogNeutralClick(String password) {
        if (password.equalsIgnoreCase("Bisleri")) {
            //Open new screen
            startActivity(new Intent(this, ChangeBaseUrlScreen.class));
        } else {
            Toast.makeText(this, "Wrong Password, try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void parseCallResponse(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //calls getqueue for another recharge
            handler.post(runnableCode);
            handlerSecond.removeCallbacks(runnableCodeSecond);
            findViewById(R.id.textViewWaiting).setVisibility(View.INVISIBLE);
        }
    }
}
