package sms.demo.com.smsdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

public class LockDialogFragment extends DialogFragment {

    public interface LockDialogListener {
        public void onDialogNeutralClick(String password);
    }
    LockDialogListener lockDialogListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_password, null));
        builder.setMessage("Enter Password")
                .setNeutralButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
lockDialogListener.onDialogNeutralClick(((EditText)getDialog().findViewById(R.id.password)).getText().toString().trim());
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            lockDialogListener = (LockDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LockDialogListener");
        }
    }
}