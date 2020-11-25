package com.example.praytimeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

public class AppSettings extends AppCompatActivity {
    Button done;
    Switch silesntSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_app_settings);
        silesntSwitch = (Switch) findViewById(R.id.silentSwitch);
        SharedPreferences sharedPreferences = getSharedPreferences("save",MODE_PRIVATE);
        silesntSwitch.setChecked(sharedPreferences.getBoolean("value", false));
        silesntSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (silesntSwitch.isChecked()) {
                    // The toggle is enabled
                    MainActivity.setSilentpref(true);
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    silesntSwitch.setChecked(true);
                } else {
                    // The toggle is disabled
                    MainActivity.setSilentpref(false);
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    silesntSwitch.setChecked(false);

                }
            }
        });
        done = (Button) findViewById(R.id.buttonSettings);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Settings Saved";
                Intent mess = new Intent();
                mess.putExtra("Settings Saved", message);

                setResult(Activity.RESULT_OK,mess);
                finish();
              }
          });
    }


}