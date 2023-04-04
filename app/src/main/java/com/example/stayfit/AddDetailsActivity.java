package com.example.stayfit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityAddDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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

        binding.buttonSave.setOnClickListener(view1 -> { verifyDataAndUpload(); });

        profileImage.setOnClickListener(view12 -> selectImageIntent());

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

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Enter the details Correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        String name = binding.edtName.getText().toString();
        int weight = Integer.parseInt(binding.edtWeight.getText().toString());
        int height = Integer.parseInt(binding.edtHeight.getText().toString());
        int DOB = Integer.parseInt(binding.edtDob.getText().toString());
        String gender = binding.edtGender.getText().toString().toLowerCase();

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

            // Show a progress dialog while the upload is in progress
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Uploading image...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();

            // After the upload is complete, get the download URL of the image and store it in Firebase Realtime Database
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // Store the image URL in Firebase Realtime Database
                            userRef.child("imageUrl")
                                    .setValue(imageUrl);

//                            progressDialog.dismiss();
                            Toast.makeText(AddDetailsActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
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
        String gender = binding.edtGender.getText().toString().toLowerCase();
        boolean result = true;
        if(dob.length() != 8 ){
            result = false;
            binding.edtDob.setError("Please enter correct date of birth");
        }
        if (gender.equals("male") || gender.equals("female")) {

        }else{
            result = false;
            binding.edtGender.setError("Please enter correct gender (male or female)");
        }
        return result;
    }

}