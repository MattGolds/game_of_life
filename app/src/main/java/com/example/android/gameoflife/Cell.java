package com.example.android.gameoflife;

import android.graphics.Color;

import java.io.Serializable;

public class Cell implements Serializable {
//    public static final COLORS =
    private boolean mAlive;
    private int mGen;
    // Extra Credit
    public static int mLifespan = 9;

    public Cell(){

        mAlive = false;
        mGen = 0;
    }

    public Cell(int pos){
        mAlive = false;
        mGen = 0;
    }

    public Cell(int pos, boolean alive, int generation){
        mAlive = alive;
        mGen = generation;
    }

    public boolean isAlive(){
        return mAlive;
    }

    public int getGen(){
        return mGen;
    }

    public void setAlive(boolean status){
        mAlive = status;
        if (!mAlive)
            mGen = 0;
        else
            this.age();
    }

    public void flip(){
        setAlive(!mAlive);
    }

    public void age(){
        mGen++;
        if (mGen > mLifespan){
            this.setAlive(false);
        }

    }


}
