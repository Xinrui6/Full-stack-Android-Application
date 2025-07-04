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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private TextView usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button regButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String username, email, password, confirmPassword, userID;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initializeUI();

        regButton.setOnClickListener(v -> registerUser());
    }

    private void initializeUI() {
        usernameInput = findViewById(R.id.editTextUsername);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmPasswordInput = findViewById(R.id.editTextConfirmPassword);
        regButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        username = usernameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        confirmPassword = confirmPasswordInput.getText().toString();

        if (validateInput()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this::Registration);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(username)) {
            showErrorMsg(R.string.error_username_empty);
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            showErrorMsg(R.string.error_email_empty);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showErrorMsg(R.string.error_password_empty);
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            showErrorMsg(R.string.error_confirm_password_empty);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            passwordInput.setText("");
            confirmPasswordInput.setText("");
            showErrorMsg(R.string.error_password_mismatch);
            return false;
        }
        return true;
    }

    private void Registration(@NonNull Task<AuthResult> task) {
        progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            createNewUser();
            Toast.makeText(Register.this, R.string.account_created, Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(getApplicationContext(), login.class);
            startActivity(loginIntent);
            finish();
        } else {
            Log.e("Registration failed", "createUserWithEmail:failure", task.getException());
            Toast.makeText(Register.this, R.string.account_created_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewUser() {
        userID = mAuth.getCurrentUser().getUid();
        DocumentReference dr = firestore.collection("users").document(userID);
        Map<String, Object> profile = new HashMap<>();
        profile.put("userID", userID);
        profile.put("username", username);
        profile.put("email", email);
        profile.put("password", password);
        profile.put("biography","");
        profile.put("followers", new ArrayList<String>());
        profile.put("following", new ArrayList<String>());
        profile.put("homeTown", "");
        profile.put("cityLists", new HashMap<String,ArrayList<String>>());
        profile.put("checkIns", new ArrayList<String>());
        profile.put("flavor", new ArrayList<String>());
        profile.put("timeline", new ArrayList<String>());
        profile.put("profileImage", "gs://clear-arbor-426817-u4.appspot.com/profile_images/profile.png");
        dr.set(profile).addOnSuccessListener(dataAdded -> Log.d("data", "Success: use Profile is created for " + userID));

    }

    public void login(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), login.class);
        startActivity(loginIntent);
        finish();
    }

    private void showErrorMsg(int messageId) {
        Toast.makeText(Register.this, messageId, Toast.LENGTH_SHORT).show();
    }
}