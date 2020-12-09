package com.example.memorygameclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.memorygameclone.databinding.ActivityCreationBinding;
import com.example.memorygameclone.models.BoardSize;
import com.example.memorygameclone.utils.BitmapScalar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreationActivity extends AppCompatActivity {

    private static final String TAG = "CreationActivity";
    public static final String EXTRA_GAME_NAME = "game is passed to main";
    private ActivityCreationBinding binding;
    private List<Uri> images = new ArrayList<>();
    private creationAdaptor adaptor;
    int numPairs;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int MIN_EDIT_TEXT = 3;
    private static final int MAX_EDIT_TEXT = 14;
    private static final String REQUEST_TEXT = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 49408;
    private static final int IMAGE_GET_CODE = 6996;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.customCreationToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(MAX_EDIT_TEXT);

        binding.editTxtGameName.getText().setFilters(filters);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveImageToFireBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.editTxtGameName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.btnSave.setEnabled(shouldEnable());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            BoardSize boardSize = (BoardSize) intent.getSerializableExtra("customBoardSize");
            if (boardSize != null) {
                numPairs = boardSize.getNumPairs();
                getSupportActionBar().setTitle("Chosen pairs: ( 0 / " + boardSize.getNumPairs() + " )");

                adaptor = new creationAdaptor(images, this, boardSize, new creationAdaptor.ImageClickListener() {
                    @Override
                    public void onClickListener() {
                        if (ContextCompat.checkSelfPermission(CreationActivity.this, REQUEST_TEXT) == PackageManager.PERMISSION_GRANTED) {
                            getPhotos();
                        }else {
                            ActivityCompat.requestPermissions(CreationActivity.this, new String[] {REQUEST_TEXT}, REQUEST_CODE);
                        }
                    }
                });
                binding.recViewCustomPics.setAdapter(adaptor);
                binding.recViewCustomPics.setLayoutManager(new GridLayoutManager(this, boardSize.getWidth()));

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (!grantResults.equals(null) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhotos();
            }else {
                Snackbar.make(null, "grant permission to the application.", Snackbar.LENGTH_SHORT);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != IMAGE_GET_CODE || resultCode != Activity.RESULT_OK || data == null) {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri singleImg = data.getData();
        ClipData multipleImg = data.getClipData();

        if (multipleImg != null) {
            for (int i=0 ;i<multipleImg.getItemCount(); i++) {
                ClipData.Item item = multipleImg.getItemAt(i);
                if (images.size() < numPairs) {
                    images.add(item.getUri());
                }
            }
        }else{
            images.add(singleImg);
        }

        adaptor.notifyDataSetChanged();
        getSupportActionBar().setTitle("Chosen pairs: ( "+images.size() + " / " +numPairs+" )");
        binding.btnSave.setEnabled(shouldEnable());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldEnable() {
        if (images.size() != numPairs) {
            return false;
        }
        if (binding.editTxtGameName.getText().toString().equals("") || binding.editTxtGameName.getText().length() < MIN_EDIT_TEXT){
            return false;
        }

        return true;
    }

    private void saveImageToFireBase() throws IOException {

        binding.btnSave.setEnabled(false);

        String customGameName = binding.editTxtGameName.getText().toString();
        //Checking if the name is already taken by someone;
        db.collection("game").document(customGameName)
                .get().addOnSuccessListener( document -> {
                    if (document != null && document.getData() != null) {
                        new AlertDialog.Builder(this)
                                .setTitle("Invalid Name:")
                                .setMessage(customGameName + "- this is already taken. Please choose another name!")
                                .setPositiveButton("Ok", null)
                                .create().show();
                        binding.btnSave.setEnabled(true);
                    }else {
                        try {
                            handleImageUploading(customGameName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
        }).addOnFailureListener( exception ->{
            Log.d(TAG, "saveImageToFireBase: " + exception.getMessage());
            Toast.makeText(this, "Encountered an exception while saving the game!", Toast.LENGTH_SHORT).show();
            binding.btnSave.setEnabled(true);
        });
    }

    private void handleImageUploading(String customGameName) throws IOException {
        binding.progressBar.setVisibility(View.VISIBLE);
        int i = 1;
        AtomicBoolean errorExists = new AtomicBoolean(false);
        ArrayList<String> uploadedImagesUri = new ArrayList<>();
        for (Uri photoUri: images) {
            byte[] byteArray = getImageByteArray(photoUri);
            String filePath = "images/" + customGameName + "/" + System.currentTimeMillis() + "-" + i + ".jpg";
            StorageReference photoReference = storage.getReference().child(filePath);
            photoReference.putBytes(byteArray)
                    .continueWithTask( photoUploadTask -> {
                        Log.d(TAG, "saveImageToFireBase: " + photoUploadTask.getResult().getBytesTransferred());
                        return photoReference.getDownloadUrl();
                    }).addOnCompleteListener( downloadUrlTask -> {
                if (!downloadUrlTask.isSuccessful()) {
                    Toast.makeText(this, "Image is not uploaded!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saveImageToFireBase: " + downloadUrlTask.getException());
                    errorExists.set(true);
                    return;
                }
                if (errorExists.get()){
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }

                String downloadLink = downloadUrlTask.getResult().toString();
                uploadedImagesUri.add(downloadLink);
                binding.progressBar.setProgress(uploadedImagesUri.size() * 100 / images.size());
                Log.d(TAG, "saveImageToFireBase: " + downloadLink);

                if (uploadedImagesUri.size() == images.size()) {
                    handleAllImagesUploaded(customGameName, uploadedImagesUri);
                }
            });
            i++;
        }
    }

    private void handleAllImagesUploaded(String gameName, ArrayList<String> imageUrls) {
        Map<String, ArrayList<String>> map = new HashMap<>();

        map.put("images", imageUrls);

        Log.d(TAG, "handleAllImagesUploaded: " + map);

        binding.progressBar.setVisibility(View.GONE);
        db.collection("game").document(gameName)
                .set(map)
                .addOnCompleteListener( gameCreationTask -> {
                    if (!gameCreationTask.isSuccessful()) {
                        Log.d(TAG, "handleAllImagesUploaded: " + gameCreationTask.getException());
                        Toast.makeText(this, "game creation failed :/", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(this)
                            .setMessage("Upload complete! Let's play your game " + gameName)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent result = new Intent();
                                    result.putExtra(EXTRA_GAME_NAME, gameName);
                                    setResult(Activity.RESULT_OK, result);
                                    finish();
                                }
                            })
                    .create().show();
                });
    }

    private byte[] getImageByteArray(Uri photoUri) throws IOException {

        Bitmap originalImage;
        Bitmap scaledBitmap;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source s = ImageDecoder.createSource(getContentResolver(), photoUri);
            originalImage = ImageDecoder.decodeBitmap(s);

        }else {
            originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
        }
        Log.d(TAG, "original image: height and width " + originalImage.getWidth() + " " + originalImage.getHeight());
        scaledBitmap = BitmapScalar.scaleToFitHeight(originalImage, 250);
        Log.d(TAG, "scaled image: height and width : " + scaledBitmap.getWidth() + " " + scaledBitmap.getHeight());
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream);

        return byteOutputStream.toByteArray();

    }

    private void getPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Choose Images"), IMAGE_GET_CODE);
    }
}