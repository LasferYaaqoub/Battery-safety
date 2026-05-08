package com.example.batterysafety.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.batterysafety.BatteryService;
import com.example.batterysafety.databinding.FragmentFunctionsBinding;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class FunctionsFragment extends Fragment {
    FragmentFunctionsBinding binding;
    SharedPreferences sp;





    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] permissionsToRequest = {

            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.SET_ALARM,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY

    };
    boolean allPermissionsGranted = true;

    public FunctionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FunctionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FunctionsFragment newInstance(String param1, String param2) {
        FunctionsFragment fragment = new FunctionsFragment();
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
        binding = FragmentFunctionsBinding.inflate(inflater, container, false);

        // request permission
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        // Check if all permissions are granted
                        boolean allGranted = true;
                        for (Boolean isGranted : result.values()) {
                            if (!isGranted) {
                                allGranted = false;
                                break;
                            }
                        }
                        if (allGranted) {
                            // All permissions granted, proceed with your logic
                        } else {
                            // Permissions denied, handle accordingly
                        }
                    }
                });

        // SharedPreferences for save status of switch and value of full|low
                sp = getActivity().getSharedPreferences("Switch", Context.MODE_PRIVATE);;
        boolean switchCheckedFull = sp.getBoolean("isCheckedFull", false); // false is the default value if the key is not found
        boolean switchCheckedLow = sp.getBoolean("isCheckedLow", false); // false is the default value if the key is not found

        int full = sp.getInt("valueFull",85); // 85 is the default value if the value is not changed
        int low = sp.getInt("valueLow",45); // 45 is the default value if the value is not changed

        // set the value of full and low in textView
        binding.tvFullAlarm.setText("You will get an alarm at "+full+"%");
        binding.tvLowAlarm.setText("You will get an alarm at "+low+"%");

        // set progress of seekbar
        binding.sbFullAlarm.setProgress(full);
        binding.sbLowAlarm.setProgress(low);


        // Set the switch state
        binding.switchFullAlarm.setChecked(switchCheckedFull);
        binding.switchLowAlarm.setChecked(switchCheckedLow);



        Intent intent = new Intent(getActivity(), BatteryService.class);

        binding.switchFullAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked){
                    editor.putBoolean("isCheckedFull", true);
                    editor.apply();
                    // view visible
                    binding.tvFullAlarm.setVisibility(View.VISIBLE);
                    binding.sbFullAlarm.setVisibility(View.VISIBLE);
                    //                    check permission is granted of notification
                    for (String permission : permissionsToRequest) {
                        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (!allPermissionsGranted) {
                        // Request permissions
                        requestPermissionLauncher.launch(permissionsToRequest);
                    }
                    // check permission of appear on top
                    if (!Settings.canDrawOverlays(getActivity())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getActivity().getPackageName()));
                        overlayPermissionLauncher.launch(intent);
                    } else {
                        // Permission already granted, handle accordingly
                    }
                    //                        start service
                    if (!binding.switchLowAlarm.isChecked()) {
                        getActivity().startForegroundService(intent);
                    }
                    // read value changed for seekbar full
                    readValueFull();
                }else {
                    editor.putBoolean("isCheckedFull", false);
                    editor.apply();
                    // view visible
                    binding.tvFullAlarm.setVisibility(View.GONE);
                    binding.sbFullAlarm.setVisibility(View.GONE);
                    // stop service
                    if (!binding.switchLowAlarm.isChecked()){
                        getActivity().stopService(intent);
                     }
                }
            }
        });
        if (binding.switchFullAlarm.isChecked()){
            binding.tvFullAlarm.setVisibility(View.VISIBLE);
            binding.sbFullAlarm.setVisibility(View.VISIBLE);
            // read value changed for seekbar full
            readValueFull();

        }
        binding.switchLowAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked){
                    editor.putBoolean("isCheckedLow", true);
                    editor.apply();
                    // view visible
                    binding.tvLowAlarm.setVisibility(View.VISIBLE);
                    binding.sbLowAlarm.setVisibility(View.VISIBLE);
                    binding.tvPowerSavingMode.setVisibility(View.VISIBLE);

//                    check permission is granted of notification
                    for (String permission : permissionsToRequest) {
                        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (!allPermissionsGranted) {
                        // Request permissions
                        requestPermissionLauncher.launch(permissionsToRequest);
                    }

                    // check permission of appear on top
                    if (!Settings.canDrawOverlays(getActivity())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getActivity().getPackageName()));
                        overlayPermissionLauncher.launch(intent);
                    } else {
                        // Permission already granted, handle accordingly
                    }


                    //                        start service
                    if (!binding.switchFullAlarm.isChecked()){
                        getActivity().startForegroundService(intent);
                    }
                    // read value changed for seekbar full
                    readValueLow();
                }else {
                    editor.putBoolean("isCheckedLow", false);
                    editor.apply();
                    // view visible
                    binding.tvLowAlarm.setVisibility(View.GONE);
                    binding.sbLowAlarm.setVisibility(View.GONE);
                    binding.tvPowerSavingMode.setVisibility(View.GONE);

                    //                    stop service
                    if (!binding.switchFullAlarm.isChecked()) {
                        getActivity().stopService(intent);
                    }
                }
            }
        });
        if (binding.switchLowAlarm.isChecked()){
            binding.tvLowAlarm.setVisibility(View.VISIBLE);
            binding.sbLowAlarm.setVisibility(View.VISIBLE);
            binding.tvPowerSavingMode.setVisibility(View.VISIBLE);
            // read value changed for seekbar full
            readValueLow();
        }
        binding.tvPowerSavingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPowerSaving = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
                intentPowerSaving.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentPowerSaving);
            }
        });



        return binding.getRoot();
    }
    private void readValueFull(){
        binding.sbFullAlarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvFullAlarm.setText("You will get an alarm at "+progress+"%");
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("valueFull", progress);
                editor.apply();

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    private void  readValueLow() {
        binding.sbLowAlarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                binding.tvLowAlarm.setText("You will get an alarm at " + progress + "%");
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("valueLow", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private final ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Settings.canDrawOverlays(getActivity())) {
                    // Permission granted, handle accordingly
                } else {
                    // Permission denied or not granted, handle accordingly
                }
            }
    );

}