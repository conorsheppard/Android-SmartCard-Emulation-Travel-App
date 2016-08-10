package com.example.conorsheppard.SmartTravelCardEmulator;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.conorsheppard.SmartTravelCardEmulator.cardemulation.TopUpTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopUpFragment extends Fragment implements View.OnClickListener {

    private Button confirmTopUp;
    private Spinner spinner;
    private EditText etCardNumber, etSecurityNumber;
    private String secNoLen;
    private String cardNoLen;
    public TopUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        etCardNumber = (EditText) view.findViewById(R.id.etCardNumber);
        etSecurityNumber = (EditText) view.findViewById(R.id.etSecurityNumber);
        // R.id.spinner is referenced from the file
        spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.top_up_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        confirmTopUp = (Button) view.findViewById(R.id.confirmTopUp);
        confirmTopUp.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        secNoLen = etSecurityNumber.getText().toString().replaceAll("\\s+", "");
        cardNoLen = etCardNumber.getText().toString().replaceAll("\\s+", "");
        if (cardNoLen.length() == 0 || secNoLen.length() == 0 ) {
            Toast.makeText(getActivity(), "Please enter your details before topping up", Toast.LENGTH_LONG).show();
        } else if (cardNoLen.length() != 16 || secNoLen.length() != 3) {
            Toast.makeText(getActivity(), "Incorrect details given", Toast.LENGTH_LONG).show();
        } else if (!(cardNoLen.matches("[0-9]+")) || !(secNoLen.matches("[0-9]+"))) {
            Toast.makeText(getActivity(), "Card details must be numbers only", Toast.LENGTH_LONG).show();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Title")
                    .setMessage("Do you really want to top up by "+spinner.getSelectedItem().toString()+"?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            TopUpTask topUpTask = new TopUpTask(getContext());
                            topUpTask.execute(spinner.getSelectedItem().toString().substring(1), "http://192.168.43.59/SmartTravel/UpdateBalance.php");
                            Toast.makeText(getActivity(), "Topped up by " + spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
        }
    }
}
