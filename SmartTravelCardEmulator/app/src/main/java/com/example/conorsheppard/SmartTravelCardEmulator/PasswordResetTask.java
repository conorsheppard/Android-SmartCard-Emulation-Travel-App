package com.example.conorsheppard.SmartTravelCardEmulator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PasswordResetTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "PasswordResetTask";
    public static String returnedBalance;
    private Boolean result = false;
    private InputStream input;
    private static String sStringInput = "";
    private String error = "Error message";
    private Context mContext;

    public PasswordResetTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected Void doInBackground(String... params) {
        for (String url1 : params) {
            try {
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("email", params[0]));
                pairs.add(new BasicNameValuePair("password", params[1]));
                pairs.add(new BasicNameValuePair("newPassword", params[2]));
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[3]);
                post.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse response = client.execute(post);

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
            } catch (ClientProtocolException e) {
                error += "\nClientProtocolException: " + e.getMessage();
            } catch (IOException e) {
                error += "\nClientProtocolException: " + e.getMessage();
            }
            BufferedReader reader;
            if (input != null) {

                try {
                    reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
                    String line = null;
                    sStringInput = "";
                    Log.i("in doInBackground", "log 2");
                    while ((line = reader.readLine()) != null) {
                        sStringInput += line + "\n";
                    }
                    Log.i("reset ***** ", sStringInput);
                } catch (UnsupportedEncodingException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                } catch (IOException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                }
            } else {
                Log.i(TAG, "inputstream is null");
            }
        }
        return null;
    }
}
