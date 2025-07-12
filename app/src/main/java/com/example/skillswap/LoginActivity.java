package com.example.skillswap;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;
    private Button loginButton;
    private TextView createAccountText;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String LOGIN_ENDPOINT = "auth/login/";

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
                // Call API for login
                new LoginTask().execute(email, password);
            }
        });

        // Create Account click
        createAccountText.setOnClickListener(view -> {
            // TODO: Navigate to Registration screen
            Toast.makeText(LoginActivity.this, "Redirecting to registration...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
    
    private class LoginTask extends AsyncTask<String, Void, LoginResult> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");
        }
        
        @Override
        protected LoginResult doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            
            try {
                URL url = new URL(API_BASE_URL + LOGIN_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                
                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("email", email);
                jsonPayload.put("password", password);
                
                String jsonString = jsonPayload.toString();
                
                // Write JSON to connection
                try (OutputStream os = connection.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(jsonString);
                    writer.flush();
                }
                
                int responseCode = connection.getResponseCode();
                
                // Read response
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 200 && responseCode < 300 
                                    ? connection.getInputStream() 
                                    : connection.getErrorStream(), 
                                StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                
                connection.disconnect();
                
                if (responseCode == 200) {
                    // Success
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String token = jsonResponse.optString("token", "");
                    String message = jsonResponse.optString("message", "Login successful");
                    return new LoginResult(true, message, token);
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Login failed");
                    return new LoginResult(false, errorMessage, "");
                }
                
            } catch (Exception e) {
                return new LoginResult(false, "Network error: " + e.getMessage(), "");
            }
        }
        
        @Override
        protected void onPostExecute(LoginResult result) {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
            
            if (result.success) {
                Toast.makeText(LoginActivity.this, result.message, Toast.LENGTH_SHORT).show();
                // TODO: Store token securely (SharedPreferences or encrypted storage)
                // For now, just navigate to main activity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private static class LoginResult {
        boolean success;
        String message;
        String token;
        
        LoginResult(boolean success, String message, String token) {
            this.success = success;
            this.message = message;
            this.token = token;
        }
    }
}
