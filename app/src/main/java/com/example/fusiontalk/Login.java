package com.example.fusiontalk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    Button button;
    TextView signBtn;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        //so that user ko cancel ka button na mile.
        progressDialog.setCancelable(false);
//        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.buttonLog);
        email = findViewById(R.id.editTextLogEmail);
        signBtn = findViewById(R.id.signUpBtn);
        password = findViewById(R.id.editTextLogPassword);
        password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emails = email.getText().toString().trim();
                String pass = password.getText().toString();

                if(emails.isEmpty()){
                    progressDialog.dismiss();
                    email.setError("Please enter email");
                    Toast.makeText(Login.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if(pass.isEmpty()){
                        progressDialog.dismiss();
                        password.setError("Please enter Password");
                        Toast.makeText(Login.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!emails.matches(emailPattern)) {
                        progressDialog.dismiss();
                        email.setError("Give valid Email Address");
                    } else if (pass.length() < 6) {
                        progressDialog.dismiss();
                        password.setError("More than Six character");
                        Toast.makeText(Login.this, "Password needs to be longer than Six Character", Toast.LENGTH_SHORT).show();
                    } else{
                        auth.signInWithEmailAndPassword(emails,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressDialog.show();
                                    Toast.makeText(Login.this, "Login is done", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Login.this, WelcomePage.class);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "Login is not done", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });
    }
}