package be.nixekinder.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements EditSettingsDialog.NoticeDialogListener {
    private static String kHostname;
    private TextView tvUsername;
    private TextView tvHostname;
    private TextView tvApikey;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String kUsername;
    private String kApikey;
    private boolean kSet;
    private String kSignature;
    private String kProfilePictureUrl;
    private String kProfileDisplayName;
    private boolean connOK = false;
    private String TAG = "ineedcoffee";
    private String kAction;
    private Button btCheck;
    private JSONObject kJson;
    private ImageView profile;

    private TextView updStatus;
    private TextView updBookmark;
    private TextView updAudio;
    private TextView updPost;
    private TextView updLocation;
    private TextView updImage;



    public void showConnectionDialog() {
        // Create an instance of the dialog fragment and show it
        EditSettingsDialog dialog = new EditSettingsDialog();
        dialog.show(getFragmentManager(), "Connection Dialog");

    }

    public void toast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public void snack(String message) {
      
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.circlecontainer), iconFont);
        profile = (ImageView) findViewById(R.id.profile_image);
        final TextView tvProfileHost = (TextView) findViewById(R.id.profile_host);
        TextView tvProfileName = (TextView) findViewById(R.id.profile_name);
        pref = getSharedPreferences("KnownApiSettings", 0);
        editor = pref.edit();
        kUsername = getSharedPref("kUsername", "");
        kHostname = getSharedPref("kHostname", "");
        kApikey = getSharedPref("kApikey", "");
        kSet = getSharedPref("kSet", false);
        kProfileDisplayName = getSharedPref("kProfileDisplayName", "");

        // Menu Views
        updAudio = (TextView) findViewById(R.id.updAudio);
        updBookmark = (TextView) findViewById(R.id.updBookmark);
        updImage = (TextView) findViewById(R.id.updImage);
        updLocation = (TextView) findViewById(R.id.updLocation);
        updPost = (TextView) findViewById(R.id.updPost);
        updStatus = (TextView) findViewById(R.id.updStatus);


        if (!kProfileDisplayName.equals("")) {
            tvProfileName.setText(kProfileDisplayName);
        }
        if (!kHostname.equals("")) {
            tvProfileHost.setText(kHostname);
        }
        kProfilePictureUrl = getSharedPref("kProfilePictureUrl", "");
        Log.i(TAG, "onCreate: KProfileURL " + kProfilePictureUrl);
        if (!kProfilePictureUrl.equals("")) {
            Picasso.with(getBaseContext()).load(kProfilePictureUrl).transform(new RoundedCornersTransform()).into(profile);
        }


        if (kSet == false) {
            showWarning();
        }


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }

        } else {
            // Handle other intents, such as being started from the home screen
        }

        // click on the profile picture 
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = tvProfileHost.getText().toString();
                openUrl(url);
            }
        });

        // Listen on Actions

        updPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("post");
            }
        });

        updStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("status");
            }
        });

        updLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("location");
            }
        });

        updImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("image");
            }
        });

        updAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("audio");
            }
        });


        updBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatus("bookmark");
            }
        });





    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setStatus(String action) {
        toast(action);
    }

    public HashMap<String, String> getConnectionSettings() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put("kUsername", kUsername);
        settings.put("kHostname", kHostname);
        settings.put("kApikey", kApikey);
        settings.put("kSignature", kSignature);
        settings.put("kAction", kAction);
        return settings;
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
           // TextView tvIntent = (TextView) findViewById(R.id.fromIntent);

            boolean isurl = Patterns.WEB_URL.matcher(sharedText.toLowerCase()).matches();
            if (isurl) {
                // sharedText = "<a href='"+ sharedText +"'>" + sharedText + "</a>";
            }
            // tvIntent.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }


    private void showWarning() {
        toast(getString(R.string.emptysettings));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                showConnectionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(String username, String hostname, String apikey, boolean hasChanged) {


        if (hasChanged) {
            //changed = true;

            testConnection();
        }

        kUsername = username;
        kHostname = hostname;
        kApikey = apikey;


        setSharedPref("kUsername", username);
        setSharedPref("kHostname", hostname);
        setSharedPref("kApikey", apikey);
        setSharedPref("kSet", true);

    }

    private void testConnection() {
        kAction = "/profile/" + kUsername;
        Log.i(TAG, "testConnection: Start Test");
        // check connection
        kSignature = getSignature(kAction);
        new GetAsync().execute("", "");

        toast("test connection");
    }

    @Override
    public void onDialogNegativeClick() {
    }

    /**
     * save shared preferences
     *
     * @param name
     * @param value
     */
    public void setSharedPref(String name, String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public void setSharedPref(String name, boolean value) {
        editor.putBoolean(name, value);
        editor.commit();
    }

    private String getSignature(String action) {
        ApiSecurity signature = new ApiSecurity();
        signature.setSecurity(action, kApikey);
        Log.d(TAG, "getSignature: " + signature.getHash());
        return signature.getHash();
    }

    /**
     * get saved sharedpreferences, if not available, return default
     *
     * @param name         name
     * @param defaultvalue default value if pref is empty
     * @return String
     */
    public String getSharedPref(String name, String defaultvalue) {
        return pref.getString(name, defaultvalue);
    }

    public boolean getSharedPref(String name, boolean defaultvalue) {
        return pref.getBoolean(name, defaultvalue);
    }

    class PostAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private HashMap<String, String> cSettings = new HashMap<>();
        private String LOGIN_URL;
        private String USER_NAME;
        private String SIGNATURE;
        private String TAG_SUCCESS;
        private String TAG_MESSAGE;


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                cSettings = getConnectionSettings();

                HashMap<String, String> params = new HashMap<>();
                params.put("name", args[0]);
                params.put("password", args[1]);

                Log.d("request", "starting");
                LOGIN_URL = cSettings.get("kHostname");
                USER_NAME = cSettings.get("kUsername");
                SIGNATURE = cSettings.get("kSignature");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params, USER_NAME, SIGNATURE);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 0;
            String message = "";

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                Toast.makeText(MainActivity.this, json.toString(),
                        Toast.LENGTH_LONG).show();

                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {
                Log.d("Success!", message);
            } else {
                Log.d("Failure", message);
            }
        }

    }

    class GetAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private HashMap<String, String> cSettings = new HashMap<>();
        private String LOGIN_URL;
        private String USER_NAME;
        private String SIGNATURE;

        private String TAG_SUCCESS;
        private String TAG_MESSAGE;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                cSettings = getConnectionSettings();

                HashMap<String, String> params = new HashMap<>();
                params.put("name", args[0]);
                params.put("password", args[1]);

                Log.d("request", "starting");
                LOGIN_URL = cSettings.get("kHostname") + cSettings.get("kAction");
                USER_NAME = cSettings.get("kUsername");
                SIGNATURE = cSettings.get("kSignature");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "GET", params, USER_NAME, SIGNATURE);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 1;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {

                if (kAction.toLowerCase().contains("profile")) {
                    try {
                        JSONObject user = json.getJSONObject("user");
                        JSONObject image = user.getJSONObject("image");
                        String id = user.getString("id");
                        String displayname = user.getString("displayName");
                        String imageurl = image.getString("url");
                        Log.i(TAG, "onPostExecute: " + displayname + ", " + id +  ", " + imageurl);
                        kProfileDisplayName = displayname;
                        setSharedPref("kProfileDisplayName", displayname);
                        kProfilePictureUrl = imageurl;
                        setSharedPref("kProfilePictureUrl", imageurl);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

}
