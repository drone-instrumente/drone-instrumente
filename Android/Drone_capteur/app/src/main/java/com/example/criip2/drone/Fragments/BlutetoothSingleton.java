package com.example.criip2.drone.Fragments;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BlutetoothSingleton {

    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private static final String TAG = "piscine";
    private GestionAffichageBT gest = GestionAffichageBT.get();


    private BlutetoothSingleton(){

       mHandler = gest.getmHandler();
    }

    public static BlutetoothSingleton get(){

        if (serviceBt == null){

            serviceBt= new BlutetoothSingleton();
        }

        return serviceBt;
    }

    private static BlutetoothSingleton serviceBt;


    private class ConnectThread extends Thread {
        private BluetoothSocket mBTSocket = null;
        private final BluetoothDevice mmDevice;
        private String mSocketType;
        private String name;

        public ConnectThread(BluetoothDevice device, String mName) {

            mmDevice = device;
            name = mName;
        }

        public void run () {
            boolean fail = false;

            try {
                mBTSocket = createBluetoothSocket(mmDevice);
            } catch (IOException e) {
                fail = true;
                //Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                mBTSocket.connect();
            } catch (IOException e) {
                try {
                    fail = true;
                    mBTSocket.close();
                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                            .sendToTarget();
                } catch (IOException e2) {
                    //insert code to deal with this
                    //Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
            }
            if (fail == false) {
                mConnectedThread = new ConnectedThread(mBTSocket);
                mConnectedThread.start();

                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }


    public void connect (BluetoothDevice device, String mName){
        mConnectThread = new ConnectThread(device, mName);
        mConnectThread.start();
    }



    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            int i = 0;
            // Keep listening to the InputStream until an exception occurs
            Log.d(TAG, "Reception1");
            while (true) {
                try {
                    // Read from the InputStream
                    buffer = new byte[1024];
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        i++;
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void envoieData(String messag){
        if (mConnectedThread != null) //First check to make sure thread created
            mConnectedThread.write(messag);
        //mConnectedThread.write(message.getText().toString());
    }


}
