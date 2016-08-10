package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button bRegister;
    EditText etEmail, etPassword, etConfirmPassword;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)  {
        // gets the id of the view which notified this OnClick method
        switch(v.getId()) {
            // if the login button was the notifier of this OnClick method then enter this case statement
            case R.id.bRegister:
                String email = etEmail.getText().toString().trim();
                boolean emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                UUID uuid = UUID.nameUUIDFromBytes(email.getBytes());
                User user = new User(email, password, uuid.toString(), false);

                if (!emailOk) {
                    dialogBuilder = new AlertDialog.Builder(Register.this);
                    dialogBuilder.setMessage("Please enter a valid email address");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                } else if (password.length() < 6) {
                    dialogBuilder = new AlertDialog.Builder(Register.this);
                    dialogBuilder.setMessage("Please enter valid password of min length 6 characters");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else if (!(password.equals(confirmPassword))) {
                    dialogBuilder = new AlertDialog.Builder(Register.this);
                    dialogBuilder.setMessage("Passwords do not match");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                } else {
                    registerUser(user);
                }
                break;
        }
    }

    private void registerUser(User user) {
        ServerRequestTasks serverRequestTasks = new ServerRequestTasks(this);
        serverRequestTasks.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }
}