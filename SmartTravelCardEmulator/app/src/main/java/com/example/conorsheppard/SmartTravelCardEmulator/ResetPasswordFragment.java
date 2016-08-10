package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    private Button confirmPasswordReset;
    EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    AlertDialog.Builder dialogBuilder;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        etCurrentPassword = (EditText) view.findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) view.findViewById(R.id.etNewPassword);
        etConfirmNewPassword = (EditText) view.findViewById(R.id.etConfirmNewPassword);
        confirmPasswordReset = (Button) view.findViewById(R.id.buttonPasswordReset);

        confirmPasswordReset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String password = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        if (newPassword.length() < 6) {
            dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage("Please enter valid password of min length 6 characters");
            dialogBuilder.setPositiveButton("Ok", null);
            dialogBuilder.show();
        } else if (!(newPassword.equals(confirmNewPassword))) {
            dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage("Passwords do not match");
            dialogBuilder.setPositiveButton("Ok", null);
            dialogBuilder.show();
        } else {
            UserLocalStore userLocalStore = MainActivity.getUserLocalStore();
            User user = userLocalStore.getLoggedInUser();
            PasswordResetTask passwordResetTask = new PasswordResetTask(getActivity());
            passwordResetTask.execute(user.email, password, newPassword, "http://192.168.43.59/SmartTravel/ResetPassword.php");
        }
    }

}
