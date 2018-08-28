package com.tkctechnologies.codename_tkc.amazingacc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
 TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      /*  try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaceEnumeration.hasMoreElements()){
               NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                networkInterface.getInterfaceAddresses().get(0);
                        InetAddress.class.isInstance(Inet6Address.class);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }*/
      /* Using the server class i created in the same package*/
      textView= (TextView) findViewById(R.id.values);
        Server  server = new Server(MainActivity.this,8090);
        server.startServer();
        SensorManager sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] floats= sensorEvent.values;
        Server.setValues(floats);
        int ax = (int)(Math.atan2(floats[0],floats[1])/(Math.PI/180));
        int ay = (int)(Math.atan2(floats[1],floats[2])/(Math.PI/180));
        int az = (int)(Math.atan2(floats[0],floats[2])/(Math.PI/180));
        textView.setText("X: "+ax+" Y: "+ay+" Z: "+az);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
