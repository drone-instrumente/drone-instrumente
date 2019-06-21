package com.example.criip2.drone.Fragments;

import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class GestionAffichageBT {

    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private Handler mHandler;

    public String valSon = "N/C";
    public String valTemperature = "N/C";
    public String valHumidity = "N/C";
    public String valLumiere = "N/C";
    public String valLat = "48.7945";
    public String valLonge = "2.3340";
    public String valAlt = "2";
    public String statutBT= "Pas de connexion";
    public String reception= "N/C";

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
                if (readMessage.contains("t"))
                {
                    valTemperature = readMessage.substring(readMessage.lastIndexOf('t')+1,readMessage.length())+ " °C";
                    //valTemperature = readMessage.replace('t', ' ');
                }
                else if (readMessage.contains("h")){
                    valHumidity = readMessage.substring(readMessage.lastIndexOf('h')+1,readMessage.length())+ " %";
                    //valHumidity = readMessage.replace('h', ' ');
                }
                else if (readMessage.contains("s")){
                    valSon = readMessage.substring(readMessage.lastIndexOf('s')+1,readMessage.length());
                    //valSon = readMessage.replace('s', ' ');
                }
                else if (readMessage.contains("l")){
                    valLumiere = readMessage.substring(readMessage.lastIndexOf('l')+1,readMessage.length()) + " Lux";
                    //valLumiere = readMessage.replace('l', ' ');
                }
                else if (readMessage.contains("a")){
                    String message = readMessage.substring(readMessage.lastIndexOf('a')+1,readMessage.length());
                    valAlt = (String.format("%.2f", Double.parseDouble(message)));
                    valAlt= valAlt.replace(',', '.');
                    Log.d(TAG, "Alt " + valAlt);
                    //valAlt = readMessage.replace('a', ' ');
                }

                else if (readMessage.contains("b")){
                    String message = readMessage.substring(readMessage.lastIndexOf('b')+1,readMessage.length());
                    String message1 = message;
                    if (Double.parseDouble(message) != 0){

                        double mm = (Double.valueOf(message.substring(message.lastIndexOf('.') - 2, message.length()))) / 60;
                        double dd = Double.valueOf(message1.substring(0, message1.lastIndexOf('.') - 2));
                        Log.d(TAG, "mm " +Double.toString(dd));
                        Log.d(TAG, "dd " + Double.toString(mm));
                        double total = dd + mm;
                        valLat = (String.format("%.4f", total)) ;
                        valLat = valLat.replace(',', '.');
                        Log.d(TAG, "lat " + valLat);
                        //valLat = readMessage.substring(readMessage.lastIndexOf('b')+1,readMessage.length());
                        //valLat = readMessage.replace('b', ' ');
                    }
                    else {
                        valLat = "0";
                    }
                }
                else if (readMessage.contains("c")){
                    String message = readMessage.substring(readMessage.lastIndexOf('c')+1,readMessage.length());
                    String message1 = message;
                    if (Double.parseDouble(message) != 0) {

                        double  mm = (Double.valueOf(message.substring(message.lastIndexOf('.') - 2, message.length()))) / 60;
                        double dd = Double.valueOf(message1.substring(0, message1.lastIndexOf('.') - 2));
                        Log.d(TAG, "mm " +Double.toString(dd));
                        Log.d(TAG, "dd " + Double.toString(mm));
                        double total = dd + mm;
                        valLonge = (String.format("%.4f", total)) ;
                        valLonge = valLonge.replace(',', '.');
                        Log.d(TAG, "long " + valLonge);
                        //valLonge = readMessage.substring(readMessage.lastIndexOf('c')+1,readMessage.length());
                        //valLonge = readMessage.replace('c', ' ');
                    }
                    else {
                        valLonge = "0";
                    }

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
