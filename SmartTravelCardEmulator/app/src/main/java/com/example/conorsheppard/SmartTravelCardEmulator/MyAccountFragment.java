package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MyAccountFragment extends ListFragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_my_account, container, false);
        String[] myAccountItems = {"Reset Password", "Support", "Licences"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_items, R.id.txtitem, myAccountItems);
        setListAdapter(adapter);
        setRetainInstance(true);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String str = String.valueOf(arg0.getItemAtPosition(arg2));
                        Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
                    }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void onListItemClick(ListView l, View view, int position, long id) {
        ViewGroup viewGroup = (ViewGroup) view;
        TextView txt = (TextView)viewGroup.findViewById(R.id.txtitem);
        FragmentActivity fActivity = (FragmentActivity)view.getContext();
        if (txt.getText().toString().equals("Support")) {
            SupportFragment supportFragment = new SupportFragment();
            FragmentTransaction supportFragmentTransaction = fActivity.getSupportFragmentManager().beginTransaction();
            supportFragmentTransaction.replace(R.id.container, supportFragment);
            supportFragmentTransaction.commit();
        } else if (txt.getText().toString().equals("Reset Password")) {
            ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
            FragmentTransaction resetPasswordFragmentTransaction = fActivity.getSupportFragmentManager().beginTransaction();
            resetPasswordFragmentTransaction.replace(R.id.container, resetPasswordFragment);
            resetPasswordFragmentTransaction.commit();
        }
    }
}