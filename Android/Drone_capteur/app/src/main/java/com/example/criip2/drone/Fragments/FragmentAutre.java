package com.example.criip2.drone.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.criip2.drone.R;

public class FragmentAutre extends Fragment {



    // Pour le bt
    private BlutetoothSingleton serviceBT = null;
    private Handler mhandler;
    private Handler maj = new Handler();
    private TextView valTemp;
    private TextView valHum;
    private TextView valSon;
    private TextView valLum;
    private ImageView temp;
    private ImageView hum;
    private ImageView lum;
    private ImageView son;
    Toast toast;
    private GestionAffichageBT gestion = GestionAffichageBT.get();


    public static final String TAG = "fragment2";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_capteurs, container, false);
        Log.d(TAG, "onCreateView: ");

        valTemp = (TextView) view.findViewById(R.id.valeurTemprature);
        valSon = (TextView) view.findViewById(R.id.valeurSon);
        valHum = (TextView) view.findViewById(R.id.valeurHumidite);
        valLum = (TextView) view.findViewById(R.id.valeurLumiere);

        lum = (ImageView) view.findViewById(R.id.ic_lum);
        son = (ImageView) view.findViewById(R.id.ic_sound);
        hum = (ImageView) view.findViewById(R.id.ic_hum);
        temp = (ImageView) view.findViewById(R.id.ic_termo);

        toast= Toast.makeText(getContext(), "Texte", Toast.LENGTH_SHORT);

        lum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast.cancel();
                toast = Toast.makeText(getContext(), "Capteur luminosité", Toast.LENGTH_SHORT);
                //toast.setText("Localisation du drone");
                toast.show();

            }
        });

        son.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast.cancel();
                toast = Toast.makeText(getContext(), "Capteur son", Toast.LENGTH_SHORT);
                //toast.setText("Localisation du drone");
                toast.show();

            }
        });

        hum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast.cancel();
                toast = Toast.makeText(getContext(), "Capteur humidité", Toast.LENGTH_SHORT);
                //toast.setText("Localisation du drone");
                toast.show();

            }
        });

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast.cancel();
                toast = Toast.makeText(getContext(), "Capteur température", Toast.LENGTH_SHORT);
                //toast.setText("Localisation du drone");
                toast.show();

            }
        });

        maj();
        return view;
    }

    public void maj () {
        maj.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                valTemp.setText(gestion.valTemperature);
                valSon.setText(gestion.valSon);
                valHum.setText(gestion.valHumidity);
                valLum.setText(gestion.valLumiere);

                maj();
            }
        }, 200);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep the fragment and all its data across screen rotation
        Log.d(TAG, "onCreate: ");

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
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



}

