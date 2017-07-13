package sms.demo.com.smsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutgoingCallReceiver extends BroadcastReceiver {
    OutgoingCallResponseInterface ussdInterface;

    public OutgoingCallReceiver() {

    }

    public OutgoingCallReceiver(OutgoingCallResponseInterface ussdInterface) {
        this.ussdInterface = ussdInterface;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ussdInterface.parseCallResponse(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
