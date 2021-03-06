package ihatereboot.tablettrpg;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.logging.Handler;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public final static int REQUEST_ENABLE_BT = 747;
    public final static int REQUEST_ENABLE_DSCV = 1337;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
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
        /**
        if (id == R.id.action_settings) {
            return true;
        }
         **/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
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

    public void onBootSwitch(View v)
    {
        final Switch s = ((Switch) v);
        boolean isOn = s.isChecked();
        Thread callback = new Thread(new Runnable() {
            private Switch param = s;
            @Override
            public void run() {
                try
                {
                    Thread.currentThread().sleep(2500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            param.setEnabled(true);
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        if (isOn)
        {
            int ret = -1;
            Toast.makeText(getApplicationContext(), "Server enabled, bluetooth ON", Toast.LENGTH_SHORT).show();
            s.setEnabled(false);
            callback.start();
            if ((ret = BluetoothManager.BluetoothActivate()) == -1)
            {
                Toast.makeText(getApplicationContext(), "App can't start", Toast.LENGTH_SHORT).show();
                System.out.println("app quit");
                finish();
            }
            else if (ret == 1)
               onBootActivationYes();
        }
        else
        {
            BluetoothManager.BluetoothDeactivate();
            Toast.makeText(getApplicationContext(), "Server disabled, bluetooth OFF", Toast.LENGTH_SHORT).show();
            s.setEnabled(false);
            callback.start();
        }
    }

    /*
    * User agreed bluetooth activation
     */
    public static void onBootActivationYes()
    {
        if (BluetoothManager.isBluetoothEnabled())
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.setName("SERVER pre-anticipated-early-access-alpha");
            BluetoothManager.BluetoothDiscoverableOn();

        }
        else
            System.out.println("Bluetooth Activation Failure");
    }

    /*
    * Answer disagreed/canceled bluetooth activation
     */
    public static void onBootActivationNo()
    {

        System.out.println("User disagreed bluetooth activation");
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case 747:
                System.out.println("request OK 747"); // debug
                ActivityManager.ResultCodeActivity(resultCode);
                break;
            case 1337:
                System.out.println("request OK 1337"); // debug
                ActivityManager.ResultCodeActivity(resultCode);
                break;
        }
    }
}
