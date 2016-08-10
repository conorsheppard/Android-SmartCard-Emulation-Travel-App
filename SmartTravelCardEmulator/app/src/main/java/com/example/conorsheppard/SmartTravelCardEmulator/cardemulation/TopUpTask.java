package com.example.conorsheppard.SmartTravelCardEmulator.cardemulation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.conorsheppard.SmartTravelCardEmulator.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TopUpTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "LoyaltyCardReader";
    public static String currentMessageText;
    private Boolean result = false;
    private InputStream input;
    private static String sStringInput = "";
    private String error = "Error message";
    private Context mContext;

    public TopUpTask (Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected Void doInBackground(String... params) {
        for (String url1 : params) {
            try {
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("userId", MainActivity.getUserLocalStore().getLoggedInUser().GetUuid()));
                pairs.add(new BasicNameValuePair("topUpAmount", params[0]));
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[1]);
                post.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse response = client.execute(post);
                if(response != null) {
                    input = response.getEntity().getContent();
                    result = true;
                } else {
                    Log.i(TAG, "response is null");
                }
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
                    while ((line = reader.readLine()) != null) {
                        sStringInput += line + "\n";
                    }
                    Log.i("sStringInput: ", sStringInput);
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