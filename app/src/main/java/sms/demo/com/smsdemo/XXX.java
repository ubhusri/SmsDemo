package sms.demo.com.smsdemo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by UMANG on 05-Jul-17.
 */

public class XXX extends AccessibilityService{

    public static String TAG = "XXXX";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");
        String text = event.getText().toString();

        if (event.getClassName().equals("android.app.AlertDialog")) {
            performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(TAG, text);
            Intent intent = new Intent("com.times.ussd.action.REFRESH");
            intent.putExtra("message", text);
            sendBroadcast(intent);
            // write a broad cast receiver and call sendbroadcast() from here, if you want to parse the message for balance, date
        }

    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

}
