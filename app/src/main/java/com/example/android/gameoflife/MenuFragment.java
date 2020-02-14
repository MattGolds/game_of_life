package com.example.android.gameoflife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.UUID;


public class MenuFragment extends Fragment {

    private static final String TAG = "MENU";
    private static final String SAVE_KEY = "saveFile";
    private static final String NAMES_KEY = "savedGameNames";
    private static final String DIALOG_SET = "DialogSetting";

    private String[] mSaveFiles = {"save1","save2","save3"};
    private String[] mSavedGameNames = new String[3];
    private Button mNewButton;
    private Button[] mSaveButtons = new Button[3];

    @Override
    public void onResume(){
        super.onResume();
        loadSavedNames();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu,container,false);

        mNewButton = v.findViewById(R.id.new_button);
        mNewButton.setText(R.string.new_game);
        mSaveButtons[0] = v.findViewById(R.id.save1_button);
        mSaveButtons[1] = v.findViewById(R.id.save2_button);
        mSaveButtons[2] = v.findViewById(R.id.save3_button);

        loadSavedNames();

        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If new game button is clicked a CreateGame Dialog will be created, game will be started there.
                FragmentManager fm = getFragmentManager();
                CreateGameFragment dialog = CreateGameFragment.newInstance(mSavedGameNames);
                dialog.show(fm,DIALOG_SET);
            }
        });

        mSaveButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(mSaveFiles[0]);
            }
        });
        mSaveButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(mSaveFiles[1]);
            }
        });
        mSaveButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(mSaveFiles[2]);
            }
        });

        return v;
    }

    // Start game will be called only if an existing game save is selected
    private void startGame(String saveFile){
        Intent intent = new Intent(getActivity(),LifeActivity.class);
        // stuff selected save file and the names of the saved games.
        intent.putExtra(SAVE_KEY,saveFile);
        intent.putExtra(NAMES_KEY,mSavedGameNames);
        // start game
        startActivity(intent);

    }
    // Create new Menu Fragment
    public static MenuFragment newInstance(){
        Bundle args = new Bundle();
        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void loadSavedNames() {

        FileInputStream fis;
        ObjectInputStream ois;

        for (int i = 0; i < mSaveFiles.length; i++) {
            try {
                // open save files
                fis = new FileInputStream(new File(getActivity().getFilesDir(),mSaveFiles[i]));
                ois = new ObjectInputStream(fis);
                // read Names of saved games and set text for saved game slots
                mSavedGameNames[i] = (String) ois.readObject();
                mSaveButtons[i].setText(mSavedGameNames[i]);
                mSaveButtons[i].setEnabled(true);
                ois.close();
            } catch (Exception e) {
                //If a file doesn't exist, set saved game buttons to {Empty Slot}
                Log.d(TAG,e.toString());
                mSavedGameNames[i] = getString(R.string.empty_save);
                mSaveButtons[i].setText(R.string.empty_save);
                // Disable save slot button because the file doesn't exist.
                mSaveButtons[i].setEnabled(false);
            }
        }

    }
}