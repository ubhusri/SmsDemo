package sms.demo.com.smsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class USSDReceiver extends BroadcastReceiver {
    USSDInterface ussdInterface;

    public USSDReceiver() {

    }

    public USSDReceiver(USSDInterface ussdInterface) {
        this.ussdInterface = ussdInterface;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ussdInterface.parseMessage(intent.getStringExtra("message"));
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
