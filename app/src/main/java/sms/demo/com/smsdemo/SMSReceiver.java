package sms.demo.com.smsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SMSReceiver extends BroadcastReceiver {

    // SmsManager class is responsible for all SMS related actions
    final SmsManager sms = SmsManager.getDefault();
    ApiInterface apiInterface;

    public void onReceive(Context context, Intent intent) {
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // A PDU is a "protocol data unit". This is the industrial standard for SMS message
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    // This will create an SmsMessage object from the received pdu
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    // Get sender phone number
                    String phoneNumber = sms.getDisplayOriginatingAddress();

                    String sender = phoneNumber;
                    String message = sms.getDisplayMessageBody();

                    String deviceId = new SharedPrefHelper(context).getStringKey(ApiInterface.DEVICE_ID);
                    //   Call<DataModel> call = apiService.getReqStatus("getQueue", "airtel8675413483", "lorem341", "1234567890");
                    if (!ApiClient.isBaseUrlEmpty(context)) {
                        if (ApiClient.getClient(context) != null)
                            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
                    }
                    if (apiInterface != null) {

                        Call<Void> call = apiInterface.sendIncomingSms("newMessage", deviceId, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID), message, ApiClient.genRandomNumber());

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("speedExceeded"));
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

                        //     String formattedText = String.format(context.getResources().getString(R.string.sms_message), sender, message);
                        // Display the SMS message in a Toast
                        //   Toast.makeText(context, formattedText, Toast.LENGTH_LONG).show();
                  /*  MainActivity inst = MainActivity.instance();
                    inst.updateList(formattedText);*/
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}