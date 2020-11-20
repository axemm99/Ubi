package com.example.ubi_interfaces.ui.settings_main;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.SettingsSound;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.SettingsAccount;

public class SettingsMain extends Fragment {

    TextView account, sound;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings_main, container, false);

        // Go to Account
        account = root.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.goToFragment(new SettingsAccount(), getFragmentManager());
            }
        });

        sound = root.findViewById(R.id.sound);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.goToFragment(new SettingsSound(), getFragmentManager());
            }
        });
        return root;
    }
}
