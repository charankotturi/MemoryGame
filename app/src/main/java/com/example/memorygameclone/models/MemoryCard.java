package com.example.memorygameclone.models;

public class MemoryCard {

    private int Identifier;
    private String imageString;
    private boolean isFaceUp;
    private boolean isMatched;

    public MemoryCard(int identifier, boolean isFaceUp, boolean isMatched) {
        Identifier = identifier;
        this.isFaceUp = isFaceUp;
        this.isMatched = isMatched;
    }

    public MemoryCard(int identifier, String imageString) {
        Identifier = identifier;
        this.imageString = imageString;
        this.isFaceUp = false;
        this.isMatched = false;
    }

    public MemoryCard(int identifier) {
        Identifier = identifier;
        imageString = null;
        this.isFaceUp = false;
        this.isMatched = false;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    @Override
    public String toString() {
        return "MemoryCard{" +
                "Identifier=" + Identifier +
                ", imageString='" + imageString + '\'' +
                ", isFaceUp=" + isFaceUp +
                ", isMatched=" + isMatched +
                '}';
    }

    public int getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(int identifier) {
        Identifier = identifier;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
