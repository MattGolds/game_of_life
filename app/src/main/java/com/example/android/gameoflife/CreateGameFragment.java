package com.example.android.gameoflife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.DialogFragment;

public class CreateGameFragment extends DialogFragment {

    private static final String ARG_NAME = "name";
    private static final String NAMES_KEY = "savedGameNames";
    private static final String ARG_SIZE = "size";
    EditText mNameEdit;
    Spinner mSizeSpinner;
    String[] mSavedNames;
    String mName;
    int mSize;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_settings,null);

        // get saved game names. They aren't used here, but they need to be passed to LifeFragment.
        Bundle args = getArguments();
        mSavedNames = args.getStringArray(NAMES_KEY);

        mNameEdit = v.findViewById(R.id.name_text);
        mSizeSpinner = v.findViewById(R.id.size_spinner);

        setSpinners(getActivity());

        // EXTRA CREDIT
        // Drop down menu to select grid size.
        mSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str = parentView.getItemAtPosition(position).toString();
                mSize = Integer.parseInt(str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // EditText to name the game, this name is displayed in the saved game slots.
        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mName = charSequence.toString();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Create the Dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.game_settings)
                .setPositiveButton(R.string.start_game,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Start game
                            startGame();
                        }
                    })
                .create();
    }

    public static CreateGameFragment newInstance(String[] gameNames){
        Bundle args = new Bundle();
        CreateGameFragment fragment = new CreateGameFragment();
        args.putStringArray(NAMES_KEY,gameNames);
        fragment.setArguments(args);
        return fragment;
    }

    public void startGame(){
        // create intent and stuff with selected settings
        Intent intent = new Intent(getActivity(), LifeActivity.class);
        intent.putExtra(ARG_NAME,mName);
        intent.putExtra(ARG_SIZE,mSize);
        intent.putExtra(NAMES_KEY,mSavedNames);
        //Start game
        startActivity(intent);
    }

    public void setSpinners(Context context){
        // Help from Android Developers documentation on Spinners

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.size_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSizeSpinner.setAdapter(adapter);
    }

}
