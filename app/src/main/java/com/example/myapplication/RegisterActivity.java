package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate(binding.editName.getText().toString().trim(), binding.editPhone.getText().toString().trim(), binding.editEmail.getText().toString().trim(), binding.editPassword.getText().toString());
            }
        });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGallery();
            }
        });
    }

    private void goToGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void uploadImage(String userId, String name, String phone, String email, Uri imageUri) {

       StorageReference reference =  storageReference.child("userImages/")
               .child(System.currentTimeMillis()+userId);

       reference.putFile(imageUri)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       reference.getDownloadUrl()
                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {

                                       saveUserInDataBase(userId,name,phone,email,uri.toString());
                                   }
                               });
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                   }
               });

    }


    private void validate(String name, String phone, String email, String password) {
        if (name.isEmpty()) {
            binding.editName.setError("Required");
        } else if (phone.isEmpty()) {
            binding.editPhone.setError("Required");
        } else if (email.isEmpty()) {
            binding.editEmail.setError("Required");
        } else if (password.isEmpty()) {
            binding.editPassword.setError("Required");
        } else if (password.length() < 6) {
            binding.editPassword.setError("password must not be less than 6 characters");
        } else if(imagePath == null){
            Toast.makeText(this, "please select image", Toast.LENGTH_SHORT).show();
        }else {
            startAuth(name, phone, email, password);
        }

    }

    private void startAuth(String name, String phone, String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                uploadImage(authResult.getUser().getUid(), name, phone, email, imagePath);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveUserInDataBase(String id, String name, String phone, String email, String imageUrl) {

        ref.child(id).setValue(new ModelUserModel(id, name, email,phone,imageUrl)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.progressBar.setVisibility(View.GONE);
                startActivity(new Intent(RegisterActivity.this, UsersActivity.class));
                finishAffinity();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (data != null){
                imagePath = data.getData();
                binding.userImage.setImageURI(data.getData());
            }
        }

    }

}