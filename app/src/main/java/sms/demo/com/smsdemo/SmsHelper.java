package sms.demo.com.smsdemo;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by umang.bhusri on 6/22/2017.
 */

public class SmsHelper {
    private Context context;

    public SmsHelper(Context context) {
        this.context = context;
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
    /*        Toast.makeText(context.getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
    */    } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
