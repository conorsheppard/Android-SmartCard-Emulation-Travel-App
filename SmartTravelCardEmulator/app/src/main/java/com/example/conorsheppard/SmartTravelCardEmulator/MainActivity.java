package com.example.conorsheppard.SmartTravelCardEmulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Button bLogout;
    private TextView userEmailTxtView;
    private TextView userBalanceTxtView;
    public static Context retrieveBalanceContext;
    public static Handler userBalanceHandler;
    private Button buttonGetBalance;

    static UserLocalStore userLocalStore;
    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    @Override
    protected void onStart() {
        super.onStart();
        if(userLocalStore == null) {
            userLocalStore = new UserLocalStore(this);
        }
        Log.i("in onStart", "Setting local store");
        if (authenticate() == true) {
            setUserDetails();
        } else {
            // if the user is not logged in, the login activity will be opened
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        userEmailTxtView = (TextView)findViewById(R.id.displayUserEmail);
        Log.i("in onCreate", "Setting local store");
        if(userLocalStore == null) {
            userLocalStore = new UserLocalStore(this);
        }
        User user = userLocalStore.getLoggedInUser();
        userEmailTxtView.setText("Logged in as: " + user.email);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);

        setBalanceButton();
    }

    private void setBalanceButton() {
        buttonGetBalance = (Button) findViewById(R.id.buttonGetBalance);
        buttonGetBalance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userBalanceTxtView = (TextView) findViewById(R.id.textView7);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    userBalanceTxtView.setText("Connecting...");
                    userBalanceTxtView.setTextSize(30);
                    RetrieveBalanceTask retrieveBalanceTask = new RetrieveBalanceTask(retrieveBalanceContext);
                    retrieveBalanceTask.execute("http://192.168.43.59/SmartTravel/RetrieveBalance.php");
                    userBalanceHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message inputMessage) {
                            // Gets the image task from the incoming Message object.
                            userBalanceTxtView.setText(RetrieveBalanceTask.returnedBalance);
                            userBalanceTxtView.setTextSize(60);
                        }
                    };
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    userBalanceTxtView.setText("Hold Down To View Balance");
                    userBalanceTxtView.setTextSize(30);
                }
                return true;
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                if(userLocalStore == null) {
                    userLocalStore = new UserLocalStore(this);
                }
                User user = userLocalStore.getLoggedInUser();
                userEmailTxtView.setText("Logged in as: " + user.email);

                if (buttonGetBalance.getVisibility() == View.INVISIBLE) {
                    buttonGetBalance.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                TextView t = (TextView)findViewById(R.id.displayUserEmail);
                t.setText("");
                buttonGetBalance.setVisibility(View.INVISIBLE);
                TopUpFragment fragment = new TopUpFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                t = (TextView)findViewById(R.id.displayUserEmail);
                t.setText("");
                buttonGetBalance.setVisibility(View.INVISIBLE);
                MyAccountFragment myAccountFragment = new MyAccountFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, myAccountFragment);
                transaction.commit();
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                t = (TextView)findViewById(R.id.displayUserEmail);
                t.setText("");
                buttonGetBalance.setVisibility(View.INVISIBLE);
                AboutFragment aboutFragment = new AboutFragment();
                FragmentTransaction aboutFragmentTransaction = getSupportFragmentManager().beginTransaction();
                aboutFragmentTransaction.replace(R.id.container, aboutFragment);
                aboutFragmentTransaction.commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) { return true; }

        return super.onOptionsItemSelected(item);
    }

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment {
        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";
        static TextView tv1;

        //Returns a new instance of this fragment for the given section number.
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bLogout:
                Toast.makeText(this, "Logout Successful", Toast.LENGTH_LONG).show();
                logOut();
                break;
        }
    }





    public void logOut() {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        startActivity(new Intent(this, Login.class));
    }

    private boolean authenticate() {
        return userLocalStore.isUserLoggedIn();
    }

    private void setUserDetails() {
        User user = userLocalStore.getLoggedInUser();
    }

    public static UserLocalStore getUserLocalStore() {
        return userLocalStore;
    }

}