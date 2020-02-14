package com.example.android.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


public class SaveDialog extends DialogFragment {
    private static final String KEY = "savefile";
    public static final String EXTRA_SLOT = "com.example.android.gameoflife.slot";
    private static final String ARG_SAVES = "saves";

    private String[] mSaveFiles = {"save1", "save2", "save3"};
    private Button mNewButton;
    private Button[] mSaveButtons = new Button[3];
    private String[] mSavedGameNames;
    AlertDialog mDialog;
    private int mSlot;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // if names of saved games exist set button text to those names
        mSavedGameNames = getArguments().getStringArray(ARG_SAVES);
        if (mSavedGameNames == null){
            mSavedGameNames = new String[3];
            for (int i = 0 ; i<mSaveFiles.length;i++)
                mSavedGameNames[i] = getString(R.string.empty_save);
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_menu, null);

        mNewButton = v.findViewById(R.id.new_button);
        mSaveButtons[0] = v.findViewById(R.id.save1_button);
        mSaveButtons[1] = v.findViewById(R.id.save2_button);
        mSaveButtons[2] = v.findViewById(R.id.save3_button);

        mSaveButtons[0].setText(mSavedGameNames[0]);
        mSaveButtons[1].setText(mSavedGameNames[1]);
        mSaveButtons[2].setText(mSavedGameNames[2]);

        mNewButton.setVisibility(View.INVISIBLE);

        // set dialog settings
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.game_settings)
                .setPositiveButton(R.string.save_state,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        });// send result code back based of button selection.

        mDialog = builder.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // disable positive (save/overwrite) button until save slot is selected.
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        mSaveButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSlot(0);
            }
        });
        mSaveButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSlot(1);
            }
        });
        mSaveButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSlot(2);
            }
        });

        return mDialog;
    }

    private void sendResult(int resultCode){
        if (getTargetFragment() == null){
            return;
        }
        // send selected save slot back to parent activity.
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SLOT, mSlot);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode,intent);
    }

    private void selectSlot(int slot){

        mSlot = slot;
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        // disable the selected slot button
        mSaveButtons[slot].setEnabled(false);
        mSaveButtons[(slot+1)%3].setEnabled(true);
        mSaveButtons[(slot+2)%3].setEnabled(true);

        // set text to "Overwrite" or "Save Game" based on if slot contains a saved game already
        if ((mSavedGameNames[slot] ==getString(R.string.empty_save))){
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.save_state);
        }else{
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.overwrite);
        }
    }

    // create save dialog fragment.
    public static SaveDialog newInstance(String[] saves){
        Bundle args = new Bundle();

        args.putStringArray(ARG_SAVES, saves);
        SaveDialog fragment = new SaveDialog();
        fragment.setArguments(args);
        return fragment;
    }

}

