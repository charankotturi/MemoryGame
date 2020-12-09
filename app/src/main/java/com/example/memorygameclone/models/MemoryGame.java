package com.example.memorygameclone.models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.memorygameclone.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame {

    public List<MemoryCard> cards = new ArrayList<>();
    private Constants constants;
    private List<Integer> list;
    private BoardSize boardSize;
    private int numPairs = 0;
    public ArrayList<String> customImageList;
    private Integer previousCard = null;

    private static final String TAG = "MemoryGame";

    @RequiresApi(api = Build.VERSION_CODES.R)
    public MemoryGame(BoardSize boardSize, ArrayList<String> customImageList) {

        this.boardSize = boardSize;
        this.customImageList = customImageList;

        if (customImageList == null) {

            shuffleLists();
            List<Integer> randomPairs = new ArrayList<>();
            for (int i=0 ; i<list.size(); i++) {
                randomPairs.add(list.get(i));
                randomPairs.add(list.get(i));
            }

            Log.d(TAG, "onCreate: " + randomPairs.size());

            Collections.shuffle(randomPairs);

            for (int i=0; i<randomPairs.size(); i++) {
                MemoryCard memoryCard = new MemoryCard(randomPairs.get(i));
                cards.add(memoryCard);
            }

        }else {

            ArrayList<String> randomList = new ArrayList<>();

            for (String i: customImageList) {
                randomList.add(i);
                randomList.add(i);
            }

            Collections.shuffle(randomList);

            for (String s: randomList) {
                MemoryCard memoryCard = new MemoryCard(s.hashCode(), s);
                cards.add(memoryCard);
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void shuffleLists() {
        constants = new Constants();

        List<Integer> FullList = constants.pics;

        List<Integer> fullList = new ArrayList<>(FullList);

        Collections.shuffle(fullList);

        List<Integer> originalList = fullList.subList(0 , boardSize.getNumPairs());

        list = new ArrayList<>(originalList);

        Collections.shuffle(list);

        Log.d(TAG, "onCreate: " + originalList.size());
    }

    public boolean flipcard(int position) {

        boolean match = false;
        MemoryCard card = cards.get(position);
        // 1st flip ->
        //    restore cards + persist the card;
        //2nd flip ->
        //    check for match + persist the card;
        //3rd flip ->
        //    restore cards + persist the card;
        if (previousCard == null) {
            restoreCard();
            previousCard = position;
        }else {
            match = updateGame(previousCard, position);
            previousCard = null;
        }

        card.setFaceUp(!card.isFaceUp());

        return match;
    }

    private boolean updateGame(Integer previousCard, int position) {
        if (cards.get(previousCard).getIdentifier() == cards.get(position).getIdentifier()) {
            cards.get(previousCard).setMatched(true);
            cards.get(position).setMatched(true);
            numPairs++;
            return true;
        }

        return false;
    }

    private void restoreCard() {
        for (MemoryCard c: cards) {
            if (!c.isMatched()) {
                c.setFaceUp(false);
            }
        }
    }

    public boolean isCardFaceUp(int position) {
        return cards.get(position).isFaceUp();
    }

    public boolean hasWon() {
        return numPairs == boardSize.getNumPairs();
    }

    public int getNumPairs() {
        return numPairs;
    }
}
