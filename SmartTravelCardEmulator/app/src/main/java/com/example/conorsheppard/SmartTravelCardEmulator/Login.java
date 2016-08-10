package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button bLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegisterLink;
    UserLocalStore userLocalStore;
    public static Context loginContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);

        // Checks to see if this button has been clicked
        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        userLocalStore = MainActivity.getUserLocalStore();
    }

    // When the login button is clicked, this method is notified
    @Override
    public void onClick(View v) {
        // gets the id of the view which notified this OnClick method
        switch(v.getId()) {
            // if the login button was the notifier of this OnClick method then enter this case statement
            case R.id.bLogin:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                User user = new User(email, password);
                authenticate(user);

                break;

            // this will be called when the register link is clicked
            case R.id.tvRegisterLink:
                startActivity(new Intent(this, Register.class));
               break;
        }
    }

    // fetch user's data in the background to authenticate them
    private void authenticate(User user) {
        ServerRequestTasks serverRequestTasks = new ServerRequestTasks(this);
        serverRequestTasks.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                // if no user has been returned (i.e. the username/password is wrong) then show an error message
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        MainActivity.getUserLocalStore().storeUserData(returnedUser);
        MainActivity.getUserLocalStore().setUserLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
    }
}