package com.example.android.gameoflife;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Math;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LifeFragment extends Fragment {
    private static final String TAG = "LifeFragment";
    private static final String DIALOG_SAVE = "DialogSave";
    private static final String ARG_NAME = "name";
    private static final String ARG_SIZE = "size";
    private static final String ARG_FILE = "saveFile";
    private static final String ARG_COLONY = "colony";
    private static final String NAMES_KEY = "savedGameNames";
    private static final int REQUEST_SLOT = 0;

    private int mCols = 5;
    private int mRows = 5;
    private Colony mColony;
    private RecyclerView mLifeRecycler;
    private RecyclerView.Adapter<CellHolder> mAdapter = new CellAdapter();
    private String mColorScheme;
    private String[] mSchemeNames;
    private String mName;
    private int[] mColors = {Color.BLACK, Color.GREEN, Color.YELLOW, Color.RED};
    Map<String, int[]> mSchemes = new HashMap<>();

    private Button mSaveButton;
    Button mPlayButton;
    Button mCloneButton;
    Spinner mSchemeSpinner;
    Spinner mDelaySpinner;

    private int mDelay = 3;
    private String[] mSavedNames;
    private String[] mSaveFiles = {"save1","save2","save3"};
    private String mFile;
    private boolean mPlay = false;
    Handler mHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadGameData();
        loadColors();



        mSavedNames = getActivity().getIntent().getStringArrayExtra(NAMES_KEY);
        View v = inflater.inflate(R.layout.fragment_life,container,false);
        mSaveButton = v.findViewById(R.id.save_button);


        mLifeRecycler = v.findViewById(R.id.life_recycler_view);
        mLifeRecycler.setLayoutManager(new GridLayoutManager(getActivity(),mCols));
        mLifeRecycler.setAdapter(mAdapter);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pause game
                mPlay = false;
                mPlayButton.setText(R.string.resume);
                // create dialog fragment to select a save slot
                FragmentManager fm = getFragmentManager();
                SaveDialog dialog = SaveDialog.newInstance(mSavedNames);
                dialog.setTargetFragment(LifeFragment.this,REQUEST_SLOT);
                // run dialog
                dialog.show(fm,DIALOG_SAVE);
            }
        });

        mPlayButton = v.findViewById(R.id.play_pause);
        mSchemeSpinner = v.findViewById(R.id.change_color);
        mDelaySpinner = v.findViewById(R.id.delay);
        mCloneButton = v.findViewById(R.id.clone_button);
        setSpinners(getActivity());

        // start and stop life cycle
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlay = !mPlay;
                if (mPlay)
                    mPlayButton.setText(R.string.pause);
                else
                    mPlayButton.setText(R.string.resume);
            }
            });

        // creates new activity with same grid
        mCloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               cloneActivity();
            }
        });
        // changes color scheme of grid by drop down menu
        mSchemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mColorScheme = parentView.getItemAtPosition(position).toString();
                mColors = mSchemes.get(mColorScheme);
                // colors will be updated next generation
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // EXTRA CREDIT
        // Changes the timing for each generation
        mDelaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str = parentView.getItemAtPosition(position).toString();
                mDelay = Integer.parseInt(str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //run game loop
        mHandler.post(runnableCode);
        return v;
    }

    public void setSpinners(Context context){
        // Help from Android Developers documentation on Spinners

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.color_scheme_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSchemeSpinner.setAdapter(adapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(context,
                R.array.delay_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mDelaySpinner.setAdapter(adapter);
        // set delay selection to 2 seconds (second entry)
        mDelaySpinner.setSelection(1);
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // will run every [mDelay] seconds if mPlay is true.
            if (mPlay) {
                nextGen();
                Log.d(TAG, "Next Gen");
            }
            mHandler.postDelayed(this, mDelay*1000);
        }
    };

    private class CellHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        int[] mPos = new int[2];

        public CellHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.life_cell, container, false));
            // get dimensions of each cell from size of screen then divide by number of columns. -4 is for padding.
            int width = container.getMeasuredWidth() / mCols -4;
            GridLayoutManager.LayoutParams lp = new GridLayoutManager.LayoutParams(width,width);

            //set padding for each cell
            lp.setMargins( 2,2,2,2);
            itemView.setLayoutParams(lp);
            mImageView = (ImageView) itemView;
            mImageView.setBackgroundColor(mColors[0]);
            mImageView.setColorFilter(mColors[1]);
            itemView.setOnClickListener(this);

            mImageView = itemView.findViewById(R.id.cell_button);
        }

        @Override
        public void onClick(View view){
            // if clicked change the alive/dead state of the cell
            mColony.flip(mPos);
            //set background color based of the state of cell.
            mImageView.setBackgroundColor(mColors[mColony.isAlive(mPos)? 1 : 0]);
            Log.d(TAG,"Cell ["+mPos[0]+"]["+mPos[1]+"] was clicked");
        }

        public void bindPosition(int p) {
            Log.d(TAG, "Bind "+p);
            // if all cell holders have been created, run game loop.
            if (p == mRows*mCols - 1){
                mPlay = true;
            }
            mPos[0] = p/mCols; // get row of cell (floor)
            mPos[1] = p%mCols; // get col of cell (remainder)

            Log.d(TAG,"Button ["+mPos[0]+"]["+mPos[1]+"] ("+p+") was added");

            //Extra Credit
            // get color index of cell. based on age
            int color = (int) Math.ceil((mColony.getAge(mPos[0],mPos[1])+1)/3);
            mImageView.setBackgroundColor(mColors[color]);

            // if cell is alive, run alpha animation.
            if (color > 0 ) {
                AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
                animation1.setDuration(mDelay*1000);
                mImageView.setAlpha(1f);
                mImageView.startAnimation(animation1);
            }
        }
    }

    private class CellAdapter extends RecyclerView.Adapter<CellHolder> {
        @Override
        public void onBindViewHolder(CellHolder holder, int position) {
            holder.bindPosition(position);
        }

        @Override
        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CellHolder(inflater, parent);
        }

        @Override
        public int getItemCount() {
            return mRows*mCols;
        }
    }

    private void nextGen(){
        // update grid ages.
        mColony.nextGen();
        // update grid colors
        mLifeRecycler.getAdapter().notifyDataSetChanged();
    }


    public void loadGameData(){
        Intent intent = getActivity().getIntent();
        // if game is being created from save file, then a file string would have been put into the intent.
        mFile = intent.getStringExtra(ARG_FILE);
        // otherwise the game is being created from selected settings. pull them from the intent.
        if (mFile == null){
            mRows = intent.getIntExtra(ARG_SIZE, 5);
            mCols = mRows;
            mColorScheme = "Default";
            mName = intent.getStringExtra(ARG_NAME);
            // mColony will be in the intent if game is a clone.
            mColony = (Colony) intent.getSerializableExtra(ARG_COLONY);
            // if not a clone, create new colony.
            if (mColony == null)
               mColony = new Colony(mName, mRows,mCols,mColorScheme);
        }
        else {
            // If creating game from save file.
            readFile(mFile);
        }
    }

    public void loadColors() {
        // load color schemes from resource arrays,
        Resources res = getResources();
        mSchemeNames = res.getStringArray(R.array.color_scheme_array);

        loadColorArray(mSchemeNames[0],R.array.default_scheme);
        loadColorArray(mSchemeNames[1],R.array.pastel_scheme);
        loadColorArray(mSchemeNames[2],R.array.monogreen_scheme);
        loadColorArray(mSchemeNames[3],R.array.monoblue_scheme);
        loadColorArray(mSchemeNames[4],R.array.monored_scheme);

        mColors = mSchemes.get(mColorScheme);
    }

    public void loadColorArray(String key, int array){
        TypedArray ta = getActivity().getResources().obtainTypedArray(array);
        int[] colors = new int[ta.length()];
        for (int j = 0; j < ta.length(); j++){
            colors[j] = ta.getColor(j, 0);
        }
        mSchemes.put(key,colors);
    }

    public void readFile(String file){
        ArrayList<Object> gameInfo = new ArrayList<>();
        Cell[][] cells;
        try{
            // open file and create object stream
            FileInputStream fis = new FileInputStream(new File(getActivity().getFilesDir(),file));
            ObjectInputStream ois = new ObjectInputStream(fis);

            mName = (String) ois.readObject();
            // get saved objects
            String size = (String) ois.readObject();
            mRows = Integer.valueOf(size);
            mCols = mRows;
            mColorScheme = (String) ois.readObject();
            mColony = (Colony) ois.readObject();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // if activity canceled, do nothing.
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // if save slot selected, save game data to file.
        if ( requestCode == REQUEST_SLOT ){
            int slot = data.getIntExtra(SaveDialog.EXTRA_SLOT,0);
            saveGame(slot);
        }


    }

    private void saveGame(int slot){
        Cell[][] cells = mColony.getCells();
        File file = new File(getActivity().getFilesDir(),mSaveFiles[slot]);
        try {
            if(file.createNewFile()){
                System.out.println("file.txt File Created in Project root directory");
            }else System.out.println("File file.txt already exists in the project root directory");
        }catch (Exception E){

        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //write name of game
            oos.writeObject(mName);
            // write the size
            oos.writeObject(Integer.toString(mRows) );
            //write the color scheme
            oos.writeObject(mColorScheme);
            // write colony object
            oos.writeObject(mColony);
            oos.close();
        } catch (Exception E) {

            Log.d(TAG,E.toString());
            Log.d(TAG,"IO Exception Raised");
        }
    }

    private void cloneActivity(){
        // pause game
        mPlay = false;
        mPlayButton.setText(R.string.resume);
        // create new activity based on current game data.
        Intent intent = new Intent(getActivity(), LifeActivity.class);
        intent.putExtra(ARG_NAME,mName);
        intent.putExtra(ARG_SIZE,mRows);
        intent.putExtra(ARG_COLONY,mColony);
        startActivity(intent);
    }
}
