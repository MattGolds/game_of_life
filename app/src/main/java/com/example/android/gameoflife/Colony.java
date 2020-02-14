package com.example.android.gameoflife;

import java.io.Serializable;

public class Colony implements Serializable {


    private String mName;
    private String mColorScheme = "Default";
    private int mRows, mCols;
    private Cell[][] mCells;
    private int[][] mAges;


    public Colony(String name, int rows, int cols, String colorScheme) {
        mName = name;
        mColorScheme = colorScheme;
        mRows = rows;
        mCols = cols;
        mCells = new Cell[mRows][mCols];
        mAges = new int[mRows][mCols];
        for (int i = 0 ; i < mRows ; i++ ){
            for (int j = 0 ; j < mCols ; j++ ){
                Cell newCell = new Cell();
                mCells[i][j] = newCell;
            }
        }
    }
    public Colony(String name, int rows, int columns,String colorScheme, Cell[][] savedColony) {
        mName = name;
        mColorScheme = colorScheme;

        mRows = savedColony.length;
        mCols = savedColony[0].length;
//        mSideLength = sideLength;
        mAges = new int[mRows][mCols];
        mCells = new Cell[mRows][mCols];

        // copy old array
        for (int i = 0; i < mRows; i++) {
            System.arraycopy(savedColony[i], 0, mCells[i], 0, mCols);
        }
    }

    public boolean isAlive(int[] pos){
        return mCells[pos[0]][pos[1]].isAlive();
    }

    public void flip( int[] pos){
        mCells[pos[0]][pos[1]].flip();
    }

    public void nextGen() {
        // Logic help from David Kopec's Game of Life

        // array to count the number of living neighbors each cell has
        int[][] livingNeighbors = new int[mRows][mCols];
        int[][] ages = new int[mRows][mCols];

        for(int i = 0; i < mRows; i++){
            for(int j = 0; j < mCols; j++){

                //indices of rows and columns to the right and lef of the current cell
                int leftOfRow = i + mRows - 1;
                int rightOfRow = i + 1;
                int leftOfColumn = j + mCols - 1;
                int rightOfColumn = j + 1;

                // if cell is alive add 1 to each of its neighbors count of living neighbors
                if ( mCells[i][j].isAlive() ){
                    livingNeighbors[leftOfRow % mRows][leftOfColumn % mCols]++;
                    livingNeighbors[leftOfRow % mRows][j % mCols]++;
                    livingNeighbors[(i + mRows - 1) % mRows][rightOfColumn % mCols]++;
                    livingNeighbors[i % mRows][leftOfColumn % mCols]++;
                    livingNeighbors[i % mRows][rightOfColumn % mCols]++;
                    livingNeighbors[rightOfRow % mRows][leftOfColumn % mCols]++;
                    livingNeighbors[rightOfRow % mRows][j % mCols]++;
                    livingNeighbors[rightOfRow % mRows][rightOfColumn % mCols]++;
                }
            }
        }

        for (int i = 0; i < mRows; i++){
            for (int j = 0; j < mCols; j++) {
                // kill cell if it has 4 or more alive nighbors
                if (livingNeighbors[i][j] >= 4)
                    mCells[i][j].setAlive(false);
                // kill cell if it has 1 or no alive neighbors
                if (livingNeighbors[i][j] < 2)
                    mCells[i][j].setAlive(false);
                // bring cell to life if it has exactly 3 alive neighbors
                if (livingNeighbors[i][j] == 3)
                    mCells[i][j].setAlive(true);
                // if cell is alive age it up and get its age to add to ages array/
                if (mCells[i][j].isAlive()) {
                    mCells[i][j].age();
                    ages[i][j] = mCells[i][j].getGen();
                } // if it is not alive set its age to zero in the ages array.
                else
                    ages[i][j] = 0;
            }
        }

        mAges =  ages;
    }

    public int getSize(){
        return mCols;
    }
    public String getName(){
        return mName;
    }
    public String getColor(){
        return mColorScheme;
    }
    public Cell[][] getCells(){
        return mCells;
    }
    public int[][] getAges(){
        int[][] ages = new int[mRows][mCols];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mCols; j++) {
                ages[i][j] = mCells[i][j].getGen();
            }
        }
        return ages;
    }
    public int getAge(int row, int col){
        return mAges[row][col];
    }
}
