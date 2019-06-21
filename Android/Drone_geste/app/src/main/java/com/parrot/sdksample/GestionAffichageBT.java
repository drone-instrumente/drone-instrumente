package com.parrot.sdksample;

import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class GestionAffichageBT {

    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private Handler mHandler;

    public String statutBT= "Pas de connexion";
    public String reception= "N/C";
    public String mode ="0";

    public boolean recep = false;
    private static final String TAG = "bluetooth station";



    private GestionAffichageBT() {

        mHandler = new Handler() {
        public void handleMessage (android.os.Message msg){
            if (msg.what == MESSAGE_READ) {
                String readMessage = null;
                try {
                    readMessage = new String((byte[]) msg.obj, "UTF-8");
                    Log.d(TAG, readMessage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, readMessage);
                if (readMessage.contains("g"))
                {
                    mode = readMessage.substring(0, readMessage.lastIndexOf('g'));
                    //mode = readMessage.substring(readMessage.lastIndexOf('g')+1);
                    //mode = readMessage.substring((readMessage.lastIndexOf('g')) + (readMessage.length()));
                }

                reception = readMessage;
            }

            if (msg.what == CONNECTING_STATUS) {
                if (msg.arg1 == 1)
                    statutBT ="Connecté à : " + (String) (msg.obj);
                else
                    statutBT ="Connexion échouée";
            }
        }
        };
    }


    private static GestionAffichageBT gest;

    public static GestionAffichageBT get(){

        if (gest == null){

            gest= new GestionAffichageBT();
        }
        return gest;
    }

    public Handler getmHandler (){

        return mHandler;
    }
}
