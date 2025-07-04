package com.example.a2866777l_development_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private TextView emailInput, passwordInput;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initializeUI();

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void initializeUI() {
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar_login);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (validateInput(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this::onLogin);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showErrorMsg(1);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showErrorMsg(2);
            return false;
        }
        return true;
    }

    private void onLogin(@NonNull Task<AuthResult> task) {
        progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            Log.e("login success", "signInWithEmail:success");
            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
            loggedIn();
        } else {
            Log.e("login failed", "signInWithEmail:failure", task.getException());
            Toast.makeText(login.this, "Your email or password is not matched", Toast.LENGTH_SHORT).show();
        }
    }

    private void loggedIn() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void showErrorMsg(int errorCode) {
        int messageResId;
        switch (errorCode) {
            case 1:
                messageResId = R.string.error_email_empty;
                break;
            case 2:
                messageResId = R.string.error_password_empty;
                break;
            default:
                return;
        }
        Toast.makeText(login.this, messageResId, Toast.LENGTH_SHORT).show();
    }




    public void signUp(View view) {
        Intent signUpIntent = new Intent(login.this, Register.class);
        startActivity(signUpIntent);
    }
}