package com.example.stayfit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityAddDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddDetailsActivity extends AppCompatActivity {

    ActivityAddDetailsBinding binding;
    ImageView profileImage;
    DatabaseReference userRef;
    FirebaseUser mUser;
    String username;

    int pos = 0;
    Spinner spinner;
    String[] gender = {"Male", "Female"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        profileImage = binding.profileImage;
        username = getIntent().getStringExtra("username");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
        userRef.child("username").setValue(username);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        binding.buttonSave.setOnClickListener(view1 -> verifyDataAndUpload());

        profileImage.setOnClickListener(view12 -> selectImageIntent());

        spinner = binding.genderSpinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { pos = i; }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                pos = 0;
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, gender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri imageUri = data.getData();
                        binding.profileImage.setImageURI(imageUri);
                    }
                }
            });

    private void verifyDataAndUpload() {
        if(verifyData()){
            uploadData();
        }else {
            Toast.makeText(this, "Enter the details Correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        String name = binding.edtName.getText().toString();
        int weight = Integer.parseInt(binding.edtWeight.getText().toString());
        int height = Integer.parseInt(binding.edtHeight.getText().toString());
        int DOB = Integer.parseInt(binding.edtDob.getText().toString());
        String gender = (pos == 0)?"Male":"Female";

        if (profileImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();

            // Get a reference to Firebase Storage and create a unique file name for the image
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String fileName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child("images/" + fileName + ".jpg");

            // Convert the bitmap image to a byte array and upload it to Firebase Storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);

            // After the upload is complete, get the download URL of the image and store it in Firebase Realtime Database
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Store the image URL in Firebase Realtime Database
                userRef.child("imageUrl")
                        .setValue(imageUrl);

                Toast.makeText(AddDetailsActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AddDetailsActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }));
        }
        userRef.child("name").setValue(name);
        userRef.child("weight").setValue(weight);
        userRef.child("height").setValue(height);
        userRef.child("gender").setValue(gender);
        userRef.child("dob").setValue(DOB);
        userRef.child("username").setValue(username);
    }

    private boolean verifyData() {
        String dob = binding.edtDob.getText().toString();
        String weight = binding.edtWeight.getText().toString();
        String name = binding.edtName.getText().toString();
        String height = binding.edtHeight.getText().toString();
        boolean result = true;
        if(dob.length() != 8 ){
            result = false;
            binding.edtDob.setError("Please enter correct date of birth");
        } else if (weight.length()<=0) {
            result = false;
            binding.edtWeight.setError("Please enter correct weight");
        }else if (height.length()<=0) {
            result = false;
            binding.edtHeight.setError("Please enter correct height");
        }else if (name.length()<=0) {
            result = false;
            binding.edtName.setError("Please enter correct name");
        }
        return result;
    }

}