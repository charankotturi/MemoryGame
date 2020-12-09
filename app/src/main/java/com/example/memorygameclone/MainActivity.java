package com.example.memorygameclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.memorygameclone.databinding.ActivityMainBinding;
import com.example.memorygameclone.models.BoardSize;
import com.example.memorygameclone.models.MemoryGame;
import com.example.memorygameclone.models.UserImageList;
import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.memorygameclone.CreationActivity.EXTRA_GAME_NAME;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MemoryBoardAdaptor adaptor;
    private BoardSize boardSize = BoardSize.EASY;
    private MemoryGame memoryGame;
    private int Count = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String gameName = null;

    private RadioGroup rgOption;
    private EditText editTxtDownload;

    private ArrayList<String> customImageList = null;

    private static final String TAG = "shit";
    private static final int CREATION_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);

//        Intent intent = new Intent(MainActivity.this, CreationActivity.class);
//        intent.putExtra("customBoardSize", boardSize);
//        startActivityForResult(intent, CREATION_CODE);

        startGame();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.mi_refresh:
                alertDialogRefresh();
                break;

            case R.id.menu_choose_size:
                optionDialog();
//                Toast.makeText(this, "dialog to choose", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_creation_flow:
                customDialog();
                break;

            case R.id.menu_download_game:
                downloadDialog();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: >> started working step one!!" );
        if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: >> started step two!!" );
            String customGameName = data.getStringExtra(EXTRA_GAME_NAME);
            if (customGameName != null) {
                Log.d(TAG, "onActivityResult: >> started step three!!" );
                downloadGame(customGameName);
                getSupportActionBar().setTitle(customGameName);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void downloadGame(String customGameName){
        db.collection("game").document(customGameName).get()
                .addOnSuccessListener( document-> {
                    UserImageList userImageList = document.toObject(UserImageList.class);

                    if (document.getData() != null && document != null) {
                        getSupportActionBar().setTitle(customGameName);
                        if (userImageList == null) {
                            Log.d(TAG, "onActivityResult: user image list is empty");
                        }
                        int numCards = userImageList.getImages().size() * 2;
                        boardSize = BoardSize.getByValue(numCards);
                        Log.d(TAG, "onActivityResult: >>>>>>>>>>>>>>>" + boardSize.getBoardSize() + " " +
                                BoardSize.getByValue(numCards) + " ${numCards}" + numCards);
                        gameName = customGameName;
                        customImageList = userImageList.getImages();
                        Log.d(TAG, "onActivityResult: >>>>>>>>>>>>>>>");
                        Log.d(TAG, "onActivityResult: Images from FireStore:" + customImageList);
                        Log.d(TAG, "onActivityResult: customGameName " + customGameName);
                        Log.d(TAG, "onActivityResult: >>>>>>>>>>>>>>>");
                        Snackbar.make(binding.constrainLayout, "Ready to play "+ customGameName, Snackbar.LENGTH_SHORT).show();
                        startGame();
                    }
                    else {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("Invalid Name:")
                                .setMessage(customGameName + "- this game does not exist. Please use a valid code!")
                                .setPositiveButton("Ok", null)
                                .create().show();
                        return;
                    }
                }).addOnFailureListener( exception -> {
            Log.d(TAG, "onActivityResult: " + exception.getMessage());
        });
    }

    private void downloadDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.download_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Existing Game:");
        builder.setView(view);
        editTxtDownload = view.findViewById(R.id.editTxtDownload);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 downloadGame(editTxtDownload.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void customDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.board_size_options_dialog,  null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom board size:");
        builder.setView(view);
        RadioGroup rgButton = view.findViewById(R.id.rgBoardSizes);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                BoardSize customBoard = BoardSize.EASY;

                switch(rgButton.getCheckedRadioButtonId()) {
                    case R.id.rbEasy:
                        customBoard = BoardSize.EASY;
                        break;
                    case R.id.rbHard:
                        customBoard = BoardSize.HARD;
                        break;
                    case R.id.rbMedium:
                        customBoard = BoardSize.MEDIUM;
                        break;
                }

                Intent intent = new Intent(MainActivity.this, CreationActivity.class);
                intent.putExtra("customBoardSize", customBoard);
                startActivityForResult(intent, CREATION_CODE);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    @SuppressLint("NonConstantResourceId")
    private void optionDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.board_size_options_dialog,  null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Choose the board size:");
        initOptionDialog(view);

        switch (boardSize) {
            case EASY:
                rgOption.check(R.id.rbEasy);
                break;
            case MEDIUM:
                rgOption.check(R.id.rbMedium);
                break;
            case HARD:
                rgOption.check(R.id.rbHard);
                break;
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch(rgOption.getCheckedRadioButtonId()) {
                    case R.id.rbEasy:
                        boardSize = BoardSize.EASY;
                        customImageList = null;
                        getSupportActionBar().setTitle("My Memory");
                        break;
                    case R.id.rbHard:
                        boardSize = BoardSize.HARD;
                        customImageList = null;
                        getSupportActionBar().setTitle("My Memory");
                        break;
                    case R.id.rbMedium:
                        boardSize = BoardSize.MEDIUM;
                        customImageList = null;
                        getSupportActionBar().setTitle("My Memory");
                        break;
                }

                startGame();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    private void initOptionDialog(View view) {
        rgOption = view.findViewById(R.id.rgBoardSizes);
    }


    private void alertDialogRefresh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure about this?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    private void startGame() {

        binding.txtPairs.setTextColor(ContextCompat.getColor(this, R.color.color_started));
        Count = 0;

        switch (boardSize){
            case EASY:
                binding.txtMoves.setText("EASY 2 X 4");
                binding.txtPairs.setText("Pairs: 0 / 4");
                break;
            case MEDIUM:
                binding.txtMoves.setText("MEDIUM 3 X 6");
                binding.txtPairs.setText("Pairs: 0 / 9");
                break;
            case HARD:
                binding.txtMoves.setText("HARD 4 X 8");
                binding.txtPairs.setText("Pairs: 0 / 12");
                break;
        }

        memoryGame = new MemoryGame(boardSize, customImageList);

        Log.d(TAG, "startGame: cards for the adaptor>>>>" + memoryGame.cards);

        adaptor = new MemoryBoardAdaptor(this, boardSize, memoryGame.cards, new MemoryBoardAdaptor.CardClicked(){

            @Override
            public void onClickListener(int position) {
                Log.d(TAG, "onClickListener: " + position);
                Count++;
                updateOnFlip(position);
                String moves = "Moves: " + Count;
                binding.txtMoves.setText(moves);
            }
        });

        binding.recView.setAdapter(adaptor);
        binding.recView.setLayoutManager(new GridLayoutManager(this, boardSize.getWidth()));
        binding.recView.setHasFixedSize(true);

    }

    private void updateOnFlip(int position) {

        if (memoryGame.hasWon()){
            Toast.makeText(MainActivity.this, "You have already won!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (memoryGame.isCardFaceUp(position)) {
            Toast.makeText(MainActivity.this, "Invalid move!", Toast.LENGTH_SHORT).show();
            Count--;
            return;
        }

        if (memoryGame.flipcard(position)) {
            Log.d(TAG, "Found a match: " + memoryGame.getNumPairs());
            String Paris = "Pairs: " + memoryGame.getNumPairs() + " / " + boardSize.getNumPairs();

            @SuppressLint("RestrictedApi") Integer color =(Integer) ArgbEvaluator.getInstance().evaluate(
                    Float.valueOf(memoryGame.getNumPairs()) / boardSize.getNumPairs(),
                    ContextCompat.getColor(this, R.color.color_started),
                    ContextCompat.getColor(this, R.color.color_finished)
            );

            binding.txtPairs.setTextColor(color);
            binding.txtPairs.setText(Paris);

            if (memoryGame.hasWon()) {
                Snackbar.make(binding.constrainLayout, "You have won!", Snackbar.LENGTH_SHORT).show();
                CommonConfetti.rainingConfetti(binding.constrainLayout, new int[] {Color.MAGENTA, Color.GREEN, Color.CYAN, Color.BLUE}).oneShot();
            }
        }

        adaptor.notifyDataSetChanged();

    }

}