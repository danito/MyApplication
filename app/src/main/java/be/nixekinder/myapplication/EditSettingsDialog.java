package be.nixekinder.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.R.id.edit;
import static android.content.ContentValues.TAG;

/**
 * Created by danielnix on 15/10/2016.
 */

public class EditSettingsDialog extends DialogFragment {
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String username, String url, String api, boolean hasChanged);
        public void onDialogNegativeClick();
    }
    NoticeDialogListener mListener;

    @Override
    public void  onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        SharedPreferences prefs = getActivity().getSharedPreferences("KnownApiSettings",0);
        SharedPreferences.Editor editor = prefs.edit();
        String prefUsername = prefs.getString("kUsername","");
        String prefHostname = prefs.getString("kHostname","");
        String prefApikey = prefs.getString("kApikey","");
        View dialogView = inflater.inflate(R.layout.connection_dialog, null);
        final EditText editHost = (EditText)dialogView.findViewById(R.id.editKnownHost);
        final EditText editUser = (EditText)dialogView.findViewById(R.id.editUsername);
        final EditText editApi = (EditText)dialogView.findViewById(R.id.editKnownApi);
        boolean haschanged = false;
        editHost.setText(prefHostname);
        editUser.setText(prefUsername);
        editApi.setText(prefApikey);
        final String puser = editUser.getText().toString();
        final String phost = editHost.getText().toString();
        final String papi = editApi.getText().toString();



        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.saveSettings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String user = editUser.getText().toString();
                        String host = editHost.getText().toString();
                        String api = editApi.getText().toString();
                        boolean chgd = true;
                        Log.i(TAG, "onClick: puser " + puser + " - user " + user);
                        if (!puser.equals(user)){
                            Log.i(TAG, "onClick: puser equals user");
                        }
                        if (puser.equals(user) && phost.equals(host)  && papi.equals(api)){
                            chgd = false;
                            Log.i(TAG, "onClick: nothing has changed?");
                        }

                        mListener.onDialogPositiveClick(user, host, api, chgd);
                    }
                })
                // set null to dismiss dialog
                .setNegativeButton(R.string.cancelSettings, null);
        return builder.create();
    }

}
