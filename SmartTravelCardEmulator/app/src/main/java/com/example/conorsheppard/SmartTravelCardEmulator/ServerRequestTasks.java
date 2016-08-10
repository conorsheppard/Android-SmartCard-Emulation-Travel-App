package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ServerRequestTasks {
    // show a loading bar when the server requests are being executed
    ProgressDialog progressDialog;
    // time in milliseconds for which the connection should persist before it times out
    public static final int CONNECTION_TIME = 1000 * 10;
    // constant for server address to access php files
    public static final String SERVER_ADDRESS = "http://192.168.43.59/SmartTravel/";

    public ServerRequestTasks(Context context) {
        progressDialog = new ProgressDialog(context);
        // user will be unable to cancel the process dialog while it is loading
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    // this method will start the async task and pass to it the user
    public void storeUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback) {
        progressDialog.show();
        new FetchUserDataAsyncTask(user, callback).execute();
    }

    // background task == async task
    // this async task will get the user and the user callback
    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // this array list is created to hold the data we are going to send to the server
            // replace array list with Map<String, String> dataToSend = new HashMap<>();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("email", user.email));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("unique_id", user.uuid.toString()));
            HttpParams httpRequestParams = new BasicHttpParams();
            // the time we want this to run for
            // i.e. the time we want to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            // the time we want to wait to receive a response from the sever
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            // set up the http client
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = client.execute(post);
                if(response != null) {
                    InputStream dbResponse = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(dbResponse, "iso-8859-1"), 8);
                    String sStringInput = "";
                    String bufferedInput = "";
                    while ((bufferedInput = reader.readLine()) != null) {
                        sStringInput += bufferedInput + "\n";
                    }
                } else {
                    Log.i("ServerRequestTasks", "response is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // stop the progress dialog from loading when the async task is finished
            progressDialog.dismiss();
            // let the user callback know that the background task is finished
            userCallback.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;

        public FetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("email", user.email));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Login.php");

            User returnedUser = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                /*if(httpResponse != null) {
                    InputStream dbResponse = httpResponse.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(dbResponse, "iso-8859-1"), 8);
                    String sStringInput = "";
                    String bufferedInput = "";
                    while ((bufferedInput = reader.readLine()) != null) {
                        sStringInput += bufferedInput + "\n";
                    }
                    Log.i("****** SRTs", sStringInput);
                } else {
                    Log.i("ServerRequestTasks", "response is null");
                }*/

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.length() == 0) {
                    returnedUser = null;
                } else {
                    String email = jsonObject.getString("email");
                    String cardId = jsonObject.getString("unique_id");
                    // store matching record in an object
                    returnedUser = new User(user.email, "", cardId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // sends returnedUser to the onPostExecute method below
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);

            super.onPostExecute(returnedUser);
        }

    }


}