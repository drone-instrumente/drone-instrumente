package com.example.criip2.drone.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.criip2.drone.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "station";


    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    // Pour le bt
    private BlutetoothSingleton serviceBT = null;
    private Handler mhandler;

    private Fragment fragmentgps = new FragmentGPS();
    private Fragment fragmentautre = new FragmentAutre();
    private Fragment fragmentBT = new FragmentBluetooth();


    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentgps;
    int defaut;


    //Permission

    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Configure all views

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

       while (mLocationPermissionGranted==false) {

            if (checkMapServices()) {
                if (mLocationPermissionGranted) {
                    //getChatrooms();
                    Log.d(TAG, "Ok");
                } else {
                    getLocationPermission();
                }
            }
        }
        isMapsEnabled();

        if (savedInstanceState == null) {

            Log.d(TAG, String.valueOf(fragmentgps));
            fm.beginTransaction().add(R.id.te, fragmentBT, "2").hide(fragmentBT).commit();
            fm.beginTransaction().add(R.id.te, fragmentautre, "1").hide(fragmentautre).commit();
            fm.beginTransaction().add(R.id.te, fragmentgps, "0").commit();
        } else {

            fragmentautre = getSupportFragmentManager().getFragment(savedInstanceState, "autre");
            fragmentgps = getSupportFragmentManager().getFragment(savedInstanceState, "gps");
            fragmentBT = getSupportFragmentManager().getFragment(savedInstanceState, "bt");
        }
        Log.d(TAG, "onCreat: called.");


        if (savedInstanceState!=null) {
            active = getSupportFragmentManager().getFragment(savedInstanceState, "active");
            defaut = savedInstanceState.getInt("defaut",0);
            switch (defaut) {
                case R.id.navigation_gps:
                    loadFragment(fragmentgps);
                    break;

                case R.id.navigation_bt:
                    loadFragment(fragmentBT);
                    break;

                case R.id.navigation_autre:
                    loadFragment(fragmentautre);
                    break;
            }
    }

}

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.d(TAG, "onSaveInstste");
        outState.putInt("defaut", defaut);

        getSupportFragmentManager().putFragment(outState, "active", active);
        getSupportFragmentManager().putFragment(outState, "normal", fragmentgps);
        getSupportFragmentManager().putFragment(outState, "normal", fragmentautre);
        getSupportFragmentManager().putFragment(outState, "bt", fragmentBT);

        super.onSaveInstanceState(outState);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fm.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
        //transaction.replace(R.id.te, fragment).addToBackStack(null).commit();
        //transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }


// ---------------------
    // CONFIGURATION
    // ---------------------

    // Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){

        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle Navigation Item Click
        defaut = item.getItemId();
        switch (defaut){
            case R.id.navigation_gps:
                loadFragment(fragmentgps);
                break;

            case R.id.navigation_bt:
                loadFragment(fragmentBT);
                break;

            case R.id.navigation_autre:
                loadFragment(fragmentautre);
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    //Permission

    private boolean checkMapServices(){
        if(isServicesOK()){
            //if(isMapsEnabled()){
                return true;
            //}
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cette application nécessite la localisation GPS, Voulez-vous l'activer?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
           // getChatrooms();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //getChatrooms();
                }
                else{
                    Log.d(TAG, "Attente gps");
                    getLocationPermission();
                }
            }
        }

    }

}