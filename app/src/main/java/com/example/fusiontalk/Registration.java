package com.example.fusiontalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {
    TextView logInBtn;
    EditText rg_UserName, rg_Email, rg_Password, rg_RePassword;
    Button rg_signup;
    CircleImageView rg_profileImg;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imgURI;
    String imgUri;
    String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing the account");
        progressDialog.setCancelable(false);

        logInBtn = findViewById(R.id.logButton);
        rg_UserName = findViewById(R.id.rgUserName);
        rg_Email = findViewById(R.id.rgEmail);
        rg_Password = findViewById(R.id.rgPassword);
        rg_RePassword = findViewById(R.id.rgRePassword);
        rg_profileImg = findViewById(R.id.profilerg);
        rg_signup = findViewById(R.id.buttonRg);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        rg_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = rg_UserName.getText().toString();
                String email1 = rg_Email.getText().toString().trim();
                String pass1 = rg_Password.getText().toString();
                String rePass1 = rg_RePassword.getText().toString();
                String status = "Hey I'm using this Application";

                if (TextUtils.isEmpty(name1) || TextUtils.isEmpty(email1) || TextUtils.isEmpty(pass1) || TextUtils.isEmpty(rePass1)) {
                    Toast.makeText(Registration.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return; // Exit to avoid further execution
                }

                if (!email1.matches(emailPattern)) {
                    rg_Email.setError("Type a Valid Email");
                    return;
                }

                if (pass1.length() < 6) {
                    rg_Password.setError("Password Must Be Greater Than 6 Character");
                    return;
                }

                if (!pass1.equals(rePass1)) {
                    rg_RePassword.setError("Password Doesn't Match");
                    return;
                }

                // Show the ProgressDialog only if validations pass
                progressDialog.show();

                auth.createUserWithEmailAndPassword(email1, pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();
                            DatabaseReference reference = database.getReference().child("user").child(id);
                            StorageReference storageReference = storage.getReference().child("Upload").child(id);

                            if (imgURI != null) {
                                // Handle image upload
                                storageReference.putFile(imgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // Retrieve the download URL of the uploaded image
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imgUri = uri.toString();
                                                    createUserInDatabase(reference, id, name1, email1, pass1, imgUri, status);
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Registration.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Use default profile image URL
                                imgUri = "https://firebasestorage.googleapis.com/v0/b/fusiontalk-fc449.appspot.com/o/profile.png?alt=media&token=c42a2fe8-c1f3-4fe8-bd00-ecaf707108b7";
                                createUserInDatabase(reference, id, name1, email1, pass1, imgUri, status);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //This is used to open the gallery and for setting the image to the profile img.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data != null){
                imgURI = data.getData();
                //Now we are setting the image;
                rg_profileImg.setImageURI(imgURI);
            }
        }
    }

    private void createUserInDatabase(DatabaseReference reference, String id, String name, String email, String password, String imageUrl, String status) {
        Users users = new Users(id, name, email, password, imageUrl, status);
        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registration.this, WelcomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Registration.this, "Error In Creating The User", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}