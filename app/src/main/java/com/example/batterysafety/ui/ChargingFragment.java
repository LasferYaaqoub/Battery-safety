package com.example.batterysafety.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.batterysafety.*;
import com.example.batterysafety.databinding.FragmentChargingBinding;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChargingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargingFragment extends Fragment {
    FragmentChargingBinding binding;
    boolean isCharging;
    int voltage;
    MyTask myTask;
    BatteryReceiver batteryReceiver;
    boolean run ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChargingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChargingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChargingFragment newInstance(String param1, String param2) {
        ChargingFragment fragment = new ChargingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChargingBinding.inflate(inflater, container, false);
        batteryReceiver = new BatteryReceiver();



        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();

        run = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        requireContext().registerReceiver(batteryReceiver, filter);
        myTask = new MyTask();
        myTask.execute();

//        binding.tvAmperage.setText(myTask.currentFlow +"");
//        binding.tvMinAmperage.setText(myTask.updateMinAmp(myTask.currentFlow) +"mA");
//        binding.tvMaxAmperage.setText(myTask.updateMaxAmp(myTask.currentFlow) +"mA");
//        binding.tvAmpAverage.setText((myTask.updateMaxAmp(myTask.currentFlow)+myTask.updateMinAmp(myTask.currentFlow))/2 +"mA");

        if (myTask != null && myTask.getStatus() == AsyncTask.Status.RUNNING) {
            // Task is running, handle appropriately
        }
        else {
            myTask = new MyTask();
            myTask.execute();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        run = false;
        requireActivity().unregisterReceiver(batteryReceiver);
    }

    public class BatteryReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

                // Display strength of power source
                int chargingStrength = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (!isCharging){
                    binding.tvPowerSource.setText("Unplugged");
                } else if (isCharging && chargingStrength >= 0) {
                    binding.tvPowerSource.setText(getChargingStrength(chargingStrength));
                }
                // Display Temperature
                binding.tvTemperatureCharging.setText((temperature / 1f)/10 +" °C");






            }
        }
    }

    private String getChargingStrength(int chargingStrength){

        if (chargingStrength < 100) {
            return  "Weak";
        } else {
            return  "Strong";
        }

    }
    private String getChargingSpeed(boolean isCharging ,float watts) {
        if (isCharging) {
            if (watts >= 9.0){
                return "Fast";
            }else {
                return "Slow";
            }
        } else {
            return "Not Charging";
        }
    }


     class MyTask extends AsyncTask<String ,Integer ,Void>{

        int currentFlow,minAmp = Integer.MAX_VALUE,maxAmp= Integer.MIN_VALUE,amp;
        float watts;
        String getWatts;
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            binding.tvAmperage.setText(currentFlow +"");
            binding.tvMinAmperage.setText(updateMinAmp(currentFlow) +"mA");
            binding.tvMaxAmperage.setText(updateMaxAmp(currentFlow) +"mA");
            binding.tvAmpAverage.setText((updateMaxAmp(currentFlow)+updateMinAmp(currentFlow))/2 +"mA");
            binding.tvChargingSpeed.setText(updateWatts(currentFlow) +" w");
            binding.tvAmpStatue.setText(getAmpStatus(currentFlow));
            binding.tvStatueOfSpeed.setText(getChargingSpeed(isCharging,watts));

            try {
                // speed view of average of ampere
                binding.speedViewAmp.speedTo((float) currentFlow);
                binding.speedViewAmp.setMinMaxSpeed((float) updateMinAmp(currentFlow), (float) updateMaxAmp(currentFlow));
                // speed view of speed charge Wats
                binding.speedViewWats.speedTo((float) watts);
                binding.speedViewWats.setMinMaxSpeed( 0, (float) (10 + watts));


            }catch (Exception e){
                e.getMessage();
            }

        }


        @Override
        protected Void doInBackground(String... strings) {

            BatteryManager batteryManager = (BatteryManager) getActivity().getSystemService(Context.BATTERY_SERVICE);

                while (run){
                    currentFlow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    //watts
                    // Specify the desired format
                    DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places
                    watts = (currentFlow * (voltage/1000.f))/1000.f ;
                    // Format the number
                    getWatts = decimalFormat.format(watts);
                    publishProgress();
                }

            return null;
        }

        // Override the onCancelled() method to handle cancellation completion
        @Override
        protected void onCancelled() {
            super.onCancelled();

            // Clean up or perform any necessary actions upon cancellation
        }

        private int updateMinAmp(int currentFlow) {
            return minAmp = Math.min(minAmp, currentFlow);
        }
        private int updateMaxAmp(int currentFlow) {
            return maxAmp = Math.max(maxAmp, currentFlow);
        }
        private float updateWatts(int currentFlow){
            if (isCharging)
                return Float.parseFloat(getWatts.replace(",", "."));
            else
                return 0;
        }
        private String getAmpStatus(int currentFlow){
            if (currentFlow >= 0)
                return "Gaining";
            else
                return "Losing";
        }


    }

}