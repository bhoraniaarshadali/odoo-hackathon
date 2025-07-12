package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;
    private Button loginButton;
    private TextView createAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // change this if your XML name is different

        // Init views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePassword = findViewById(R.id.togglePassword);
        loginButton = findViewById(R.id.loginButton);
        createAccountText = findViewById(R.id.createAccount);

        // Toggle password visibility
        togglePassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye); // Closed eye
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                togglePassword.setImageResource(R.drawable.ic_eye_open); // Open eye (you must add this)
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Login click
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Add actual login logic here (Firebase, SQL, dummy check etc.)
                Toast.makeText(LoginActivity.this, "Login success (dummy)", Toast.LENGTH_SHORT).show();
                // Intent to HomeActivity (after login)
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        // Create Account click
        createAccountText.setOnClickListener(view -> {
            // TODO: Navigate to Registration screen
            Toast.makeText(LoginActivity.this, "Redirecting to registration...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
