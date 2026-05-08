package com.example.batterysafety;

import static android.app.PendingIntent.getActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.batterysafety.databinding.FragmentChargingBinding;


import java.text.DecimalFormat;

//public class ChargingService extends Service {
//
//
//    MyTask myTask;
//    TextView tvAmperage,tvAmpAverage,tvChargingSpeed,tvMaxAmperage,tvMinAmperage;
//    boolean isCharging;
//    int voltage;
//    public ChargingService() {
//    }
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_charging, null);
//        tvAmperage = view.findViewById(R.id.tv_amperage);
//        tvAmpAverage = view.findViewById(R.id.tv_amp_average);
//        tvChargingSpeed = view.findViewById(R.id.tv_charging_speed);
//        tvMaxAmperage = view.findViewById(R.id.tv_max_amperage);
//        tvMinAmperage = view.findViewById(R.id.tv_min_amperage);
//
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        // Create and display the notification
//
//        startServiceChargingBattery();
//        myTask = new MyTask();
//        myTask.execute();
//
//
//
//
//        return START_STICKY;
//    }
//    private void startServiceChargingBattery() {
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        registerReceiver(chargingReceiver, filter);
//    }
//    private final BroadcastReceiver chargingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
//            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
//
//        }
//    };
//
//            @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//
//    class MyTask extends AsyncTask<String ,Integer ,Void> {
//        int currentFlow,minAmp = Integer.MAX_VALUE,maxAmp= Integer.MIN_VALUE,amp;
//        double volt,watts;
//        private boolean isCancelled = false;
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            tvAmperage.setText(currentFlow +"");
//            tvMinAmperage.setText(updateMinAmp(currentFlow) +"mA");
//            tvMaxAmperage.setText(updateMaxAmp(currentFlow) +"mA");
//            tvAmpAverage.setText((updateMaxAmp(currentFlow)+updateMinAmp(currentFlow))/2 +"mA");
//            tvChargingSpeed.setText(updateWatts(currentFlow) +" w");
//        }
//
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            boolean run = true;
//            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
//
//            while (run){
//                currentFlow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
////                    volt = (voltage / 1000.0);
//                watts = (volt * voltage) ;
//                publishProgress();
//            }
//
//            return null;
//        }
//
//        // Override the onCancelled() method to handle cancellation completion
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//
//            // Clean up or perform any necessary actions upon cancellation
//        }
//        private int updateMinAmp(int currentFlow) {
//            if (isCharging){
//                return minAmp = Math.min(minAmp, currentFlow);
//            }else{
//                return minAmp = Math.min(minAmp, currentFlow);
//            }
//
//        }
//        private int updateMaxAmp(int currentFlow) {
//            return maxAmp = Math.max(maxAmp, currentFlow);
//        }
//        private double updateWatts(int currentFlow){
//            return currentFlow * voltage;
//        }
//    }
//}