package com.example.android.gameoflife;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.view.View;
import android.widget.Button;

import java.util.UUID;

public class MenuActivity extends SingleFragmentActivity {

//    private static final String EXTRA_PLANET_ID = "com.example.android.planets.planet_id";


    @Override
    protected Fragment createFragment() {
        return MenuFragment.newInstance();
    }

}
