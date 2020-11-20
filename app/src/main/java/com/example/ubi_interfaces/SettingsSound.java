package com.example.ubi_interfaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.ui.performances.PerformancesActivity;

public class SettingsSound extends Fragment {

    SeekBar seekBar;
    AudioManager audioManager;
    Button button, button2;
    ImageView goBack;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings_sound_ui, container, false);

        seekBar = root.findViewById(R.id.seekBar);
        audioManager = (AudioManager) root.getContext().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));


        button = root.findViewById(R.id.button);
        button2 = root.findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                Toast.makeText(getContext(),"Volume Down", Toast.LENGTH_SHORT).show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                Toast.makeText(getContext(),"Volume Up", Toast.LENGTH_SHORT).show();
            }
        });

        goBack = root.findViewById(R.id.imageView10);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.goToFragment(new PerformancesActivity(), getFragmentManager());
            }
        });
        return root;
    }
}
