package com.example.praytimeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import static java.lang.Integer.parseInt;

public class AppSettings extends AppCompatActivity {
    Button done;
    Switch silesntSwitch;
    EditText editTextMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        silesntSwitch = (Switch) findViewById(R.id.silentSwitch);
        editTextMinutes = (EditText) findViewById(R.id.editTextMinutes);
        done = (Button) findViewById(R.id.buttonSettings);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                if(silesntSwitch.isChecked()) {
                    data.putExtra("check",true);
                    data.putExtra("ms",Integer.parseInt(editTextMinutes.getText().toString()));
                    setResult(Activity.RESULT_OK,data);
                }else{
                    data.putExtra("check",false);
                    data.putExtra("ms",0);
                    setResult(Activity.RESULT_OK,data);
                }

                finish();

            }
        });
    }


}