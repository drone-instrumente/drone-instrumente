package com.example.criip2.drone.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.criip2.drone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.criip2.drone.Fragments.MainActivity.PERMISSIONS_REQUEST_ENABLE_GPS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGPS extends Fragment implements OnMapReadyCallback {

    // Pour le bt
    private BlutetoothSingleton serviceBT = null;
    private Handler mhandler;
    private Handler maj = new Handler();
    private ImageView mGps;
    private GestionAffichageBT gestion = GestionAffichageBT.get();

    MapView mapView;
    GoogleMap googleMap;
    View view;
    String oldAlt;
    LatLng localisation;
    Toast toast;
    private Marker mMarker;


    private static final String TAG = "Affichage";

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public FragmentGPS() {
        // Required empty public constructor
        //return (newInstance());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gps, container, false);

        mGps = (ImageView) view.findViewById(R.id.ic_gps);

        toast= Toast.makeText(getContext(), "Texte", Toast.LENGTH_SHORT);


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localisation!= null) {
                    if (Double.valueOf(gestion.valLat) != 0 & Double.valueOf(gestion.valLonge) != 0) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localisation, 15), 1000, null);
                        mMarker.showInfoWindow();
                        toast.cancel();
                        toast = Toast.makeText(getContext(), "Localisation du drone...", Toast.LENGTH_SHORT);
                        //toast.setText("Localisation du drone");
                        toast.show();
                    }
                    else {
                        toast.cancel();
                        toast= Toast.makeText(getContext(), "Impossible de localiser le drone", Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }
                else {
                    toast.cancel();
                    toast= Toast.makeText(getContext(), "Impossible de localiser le drone", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        maj();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MapsInitializer.initialize(getContext());
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.setMinZoomPreference(15);
        // googleMap.clear();
        //LatLng localisation = new LatLng(48.7945413, 2.3340758);
        //googleMap.addMarker(new MarkerOptions().position(localisation).title("Marker in Sydney"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(localisation));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d(TAG, "GPS false ");
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if (mMarker!=null)
                {
                    mMarker.hideInfoWindow();
                }

                if (isMapsEnabled())
                {
                    toast.cancel();
                    toast= Toast.makeText(getContext(), "Localisation du téléphone...", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    toast.cancel();
                    toast= Toast.makeText(getContext(), "Impossible : Activez la localisation du télèphone", Toast.LENGTH_SHORT);

                    toast.show();
                }
                return false;
            }
        });
        if (isMapsEnabled())
        {
            getDeviceLocation();
        }

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Cette application nécessite la localisation GPS, Voulez-vous l'activer?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

                        //while(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ));
                        //getDeviceLocation();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    public void maj () {
        maj.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (googleMap!=null) {
                    if (Double.valueOf(gestion.valLat) == 0 & Double.valueOf(gestion.valLonge) == 0)
                    {
                        if (googleMap!=null)
                        {
                            googleMap.clear();
                        }
                    }
                    else {
                        if (localisation== null)
                        {
                            oldAlt = gestion.valAlt;
                            String snippet = "Altitude : "  + gestion.valAlt;
                            localisation = new LatLng(Double.valueOf(gestion.valLat), Double.valueOf(gestion.valLonge));

                            MarkerOptions options = new MarkerOptions()
                                    .position(localisation)
                                    .title("Latitude : " + gestion.valLat + ", Longitude : " + gestion.valLonge)
                                    .snippet(snippet);
                            mMarker = googleMap.addMarker(options);
                            mMarker.showInfoWindow();
                        }
                        else if ((Double.valueOf(gestion.valLat) != localisation.latitude
                                & Double.valueOf(gestion.valLonge) != localisation.longitude)
                                | ((gestion.valAlt) != oldAlt))  {

                            String snippet = "Altitude : "  + gestion.valAlt;
                            googleMap.clear();

                            localisation = new LatLng(Double.valueOf(gestion.valLat), Double.valueOf(gestion.valLonge));

                            MarkerOptions options = new MarkerOptions()
                                    .position(localisation)
                                    .title("Latitude : " + gestion.valLat + ", Longitude : " + gestion.valLonge)
                                    .snippet(snippet);
                            mMarker = googleMap.addMarker(options);
                            mMarker.showInfoWindow();

                            //googleMap.addMarker(new MarkerOptions().position(localisation).title("Lat : "
                                   // + gestion.valLat + ", Long : " + gestion.valLonge + ", Alt : " + gestion.valAlt ));
                        }
                    }
                }
                maj();
            }
        }, 200);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        FusedLocationProviderClient  mFusedLocationProviderClient;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15), 1000,null);
                            //googleMap.moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), );

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
}
