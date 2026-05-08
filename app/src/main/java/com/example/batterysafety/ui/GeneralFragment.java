package com.example.batterysafety.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.ext.SdkExtensions;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.example.batterysafety.R;
import com.example.batterysafety.databinding.FragmentGeneralBinding;
import com.yangp.ypwaveview.YPWaveView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralFragment extends Fragment {
    private BatteryReceiver batteryReceiver ;

    FragmentGeneralBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGeneralBinding.inflate(inflater, container, false);
        batteryReceiver = new BatteryReceiver();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        requireContext().registerReceiver(batteryReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(batteryReceiver);
    }

    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                BatteryManager manager = (BatteryManager) getActivity().getSystemService(Context.BATTERY_SERVICE);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int chargingType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
                int capacityCurrentlyAmpere = ( (int) getBatteryMaxCapacityMAH(getActivity()) * level) / 100;
                String batteryType = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);




                binding.tvBatteryCapacityMax.setText((int) getBatteryMaxCapacityMAH(getActivity()) + " mAh");
                binding.tvBatteryCapacityCurrently.setText(capacityCurrentlyAmpere  + " mAh");
                binding.tvBatteryCapacityMaxOfCurrently.setText("/" + (int) getBatteryMaxCapacityMAH(getActivity()) + " mAh");
                binding.tvHealth.setText(getHealthOfBattery(health));
                binding.tvBatteryLevel.setText(level + "%");
                binding.tvStatue.setText(getStatusOfBattery(status));
                binding.tvChargingType.setText(getChargingTypeOfBattery(chargingType));
                binding.tvVoltage.setText(voltage/1000.0f +"mV");
                binding.tvTemperature.setText(temperature / 10f +" °C");
                binding.tvBatteryType.setText(batteryType);

                binding.batteryLevelProgress.setProgress(level);













            }
        }
    }




    private String getHealthOfBattery(int health){
            String healthStatus;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return healthStatus = "Good " + "(100%)";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return healthStatus = "Overheat" + " (70%)";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return healthStatus = "Dead" + " (10%)";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return healthStatus = "Over Voltage" + " (60%)";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return healthStatus = "Unspecified Failure" + " (30%)";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return healthStatus = "Cold" + " (50%)";
            default:
                return healthStatus = "Unknown" + " (0%)";
        }

    }
    private String getStatusOfBattery(int status){
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            return "Charging";
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            return "Discharging";
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            return "Not charging";
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            return "Battery is full";
        } else if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
             return "Battery status is unknown";
        } else {
            return "Handle other cases or unknown status";
        }
    }

    private String getChargingTypeOfBattery(int type){
        if (type == BatteryManager.BATTERY_PLUGGED_AC) {
            return "AC";
        } else if (type == BatteryManager.BATTERY_PLUGGED_USB) {
            return "USB";
        } else if (type == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
            return "WIRELESS";
        } else {
            return "No";
        }
    }


    public double getBatteryMaxCapacityMAH(Context context) {
            Object mPowerProfile;
            double batteryCapacity = 0;
            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

            try {
                mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                        .getConstructor(Context.class)
                        .newInstance(context);

                batteryCapacity = (double) Class
                        .forName(POWER_PROFILE_CLASS)
                        .getMethod("getBatteryCapacity")
                        .invoke(mPowerProfile);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return batteryCapacity;

        }

    public static int getCycleCount() {
        int cycleCount = -1;

        // Potential file paths for battery information (may vary across devices)
        String[] paths = {
                "/sys/class/power_supply/battery/cycle_count",
                "/sys/class/power_supply/battery/charge_counter",
                "/sys/devices/platform/battery/cycle_count",
                "/sys/devices/platform/battery/battery_cycle"
        };

        for (String path : paths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line = reader.readLine();
                try {
                    cycleCount = Integer.parseInt(line);
                    break; // Found a valid cycle count
                } catch (NumberFormatException ignored) {}
            } catch (IOException ignored) {}
        }

        return cycleCount;
    }



}



