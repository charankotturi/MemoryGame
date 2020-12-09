package com.example.memorygameclone.models;

import java.util.HashMap;
import java.util.Map;

public enum BoardSize {

    EASY(8),
    MEDIUM(18),
    HARD(24);

    private int boardSize;
    private static Map map = new HashMap<>();

    static {
        for (BoardSize b: BoardSize.values()){
            map.put(b.getBoardSize(), b);
        }
    }

    BoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public static BoardSize getByValue(int i) {
        return (BoardSize) map.get(i);
    }

    public int getWidth() {

        switch(this) {
            case EASY:
                return 2;
            case MEDIUM:
                return 3;
            case HARD:
                return 4;
            default:
                break;
        };

        return 0;
    }

    public int getHeight() {
        return getBoardSize() / getWidth();
    }

    public int getNumPairs() {
        return getBoardSize() / 2;
    }

}
