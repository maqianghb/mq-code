package com.example.mq.testcode.leedcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exercise {

    private char[][] board;
    private int row;
    private int column;
    private String word;
    private List<List<Integer>> usedIndex =new ArrayList<>();

    private boolean result =false;

    public boolean exist(char[][] board, String word) {
        this.board =board;
        this.row =board.length;
        if(row ==0){
            return false;
        }
        this.column =board[0].length;
        this.word =word;

        for(int i=0; i<row; i++){
            for(int j=0; j<column; j++){
                if(board[i][j] !=word.charAt(0)){
                    continue;
                }
                usedIndex.add(Arrays.asList(i, j));
                setValue(i, j, 1);
                usedIndex.remove(usedIndex.size()-1);
            }
        }
        return result;
    }

    private void setValue(int row, int column, int index){
        if(usedIndex.size() ==word.length()){
            result =true;
            return;
        }
        if(row >0){
            if(!isUsed(row-1, column) && board[row-1][column] ==word.charAt(index)){
                usedIndex.add(Arrays.asList(row-1, column));
                setValue(row-1, column, index+1);
                usedIndex.remove(usedIndex.size()-1);
            }
        }
        if(row <this.row-1){
            if(!isUsed(row+1, column) && board[row+1][column] ==word.charAt(index)){
                usedIndex.add(Arrays.asList(row+1, column));
                setValue(row+1, column, index+1);
                usedIndex.remove(usedIndex.size()-1);
            }
        }
        if(column >0){
            if(!isUsed(row, column-1) && board[row][column-1] ==word.charAt(index)){
                usedIndex.add(Arrays.asList(row, column-1));
                setValue(row, column-1, index+1);
                usedIndex.remove(usedIndex.size()-1);
            }
        }
        if(column <this.column-1){
            if(!isUsed(row, column+1) && board[row][column+1] ==word.charAt(index)){
                usedIndex.add(Arrays.asList(row, column+1));
                setValue(row, column+1, index+1);
                usedIndex.remove(usedIndex.size()-1);
            }
        }
    }

    private boolean isUsed(int rowIndex, int columnIndex){
        for(List<Integer> pair : usedIndex){
            if(pair.get(0) ==rowIndex && pair.get(1) ==columnIndex){
                return true;
            }
        }
        return false;
    }
}
