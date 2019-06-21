package com.parrot.sdksample;

import android.Manifest;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class FragmentBluetooth extends DialogFragment {


    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private Switch buttonBluetooth;
    private GestionAffichageBT gestion = GestionAffichageBT.get();


    public static final String TAG = "fragment2";


    private Handler maj = new Handler();
    private BlutetoothSingleton serviceBT = BlutetoothSingleton.get();

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

   static  Bundle save;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        mBluetoothStatus = (TextView) view.findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) view.findViewById(R.id.readBuffer);
        mDiscoverBtn = (Button) view.findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button) view.findViewById(R.id.PairedBtn);
        buttonBluetooth = (Switch) view.findViewById(R.id.switch1);
        if (save == null)
        {
            save = new Bundle();
        }
        else
        {
            savedInstanceState = save;
        }

        if (savedInstanceState==null) {
            Log.d(TAG, "Bundle null");
            mBTArrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
        }
        else
        {
            ArrayList<String> itemsList = new ArrayList<String>( ) ;
            itemsList = savedInstanceState.getStringArrayList("list");
            mBTArrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, itemsList);
        }
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mDevicesListView = (ListView) view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(this.getActivity(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });
        }

        if(mBTAdapter.isEnabled()){

            buttonBluetooth.setChecked(true);

        }
        buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (buttonBluetooth.isChecked())
                {
                    bluetoothOn(v);
                }
                else
                {
                    bluetoothOff(v);
                }

            }
        });


        maj();
        return view;
    }


    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            gestion.statutBT="Bluetooth activé";
            Toast.makeText(this.getActivity(),"Bluetooth activé", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this.getActivity(),"Bluetooth déjà activé", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Activé");
            }
            else
                mBluetoothStatus.setText("Désactivé");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        gestion.statutBT="Bluetooth désactivé";
        Toast.makeText(this.getActivity(),"Bluetooth désactivé", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
        }

        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(this.getActivity(),"Recherche arreté", Toast.LENGTH_SHORT).show();
        }
        else{

            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(this.getActivity(), "Recherche...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "recherche");
                this.getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }

            else{
                Toast.makeText(this.getActivity(), "Bluetooth pas activé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "je cherche");
            Log.d(TAG, BluetoothDevice.ACTION_FOUND);
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                boolean verificationSiPresentDansLaListe = false;
                for(int i = 0; i < mDevicesListView.getCount(); i++){

                    if (((device.getName() + "\n" + device.getAddress()).equals(mDevicesListView.getAdapter().getItem(i)))== true)
                    {
                        verificationSiPresentDansLaListe = true;
                    }
                }
                if (verificationSiPresentDansLaListe== false)
                {
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                }

            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            mBTArrayAdapter.clear(); // clear items
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            Log.d(TAG, "Je montre");
            Toast.makeText(this.getActivity(), "Affichage des appareils", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this.getActivity(), "Bluetooth pas activé", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if (!mBTAdapter.isEnabled()) {
               //Toast.makeText(getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }
            gestion.statutBT="Connexion...";
            //mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String mName = info.substring(0, info.length() - 17);

            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            serviceBT.connect(device, mName);
        }

    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        // Sauvegarde des données du contexte utilisateur
        outState.putString("statut",mBluetoothStatus.getText().toString());
        //outState.putParcelableArrayList(LIST, messages);
        ArrayList<String> itemsList = new ArrayList<String>( ) ;
        for(int i = 0; i < mBTArrayAdapter.getCount(); i++){
            itemsList.add(mBTArrayAdapter.getItem(i));
        }
        outState.putStringArrayList("list", itemsList );
        super.onSaveInstanceState(outState);

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
        onSaveInstanceState(save);
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

        mBTAdapter.cancelDiscovery();
    }


    public void maj () {
        maj.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                mBluetoothStatus.setText(gestion.statutBT);
                mReadBuffer.setText(gestion.reception);
                maj();
            }
        }, 1000);
    }
}

