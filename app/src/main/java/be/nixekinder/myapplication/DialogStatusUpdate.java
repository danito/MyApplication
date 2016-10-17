package be.nixekinder.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;

/**
 * Created by danielnix on 17/10/2016.
 */

public class DialogStatusUpdate extends DialogFragment {
    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogStatusUpdate.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.status, null);
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.saveSettings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        HashMap<String, String> syndication = new HashMap<String, String>();
                        String status = "";

                        mListener.onDialogPositiveClick(status, syndication);
                    }
                })
                // set null to dismiss dialog
                .setNegativeButton(R.string.cancelSettings, null);
        return builder.create();


    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(String status, HashMap<String, String> syndication);

        void onDialogNegativeClick();
    }


}
