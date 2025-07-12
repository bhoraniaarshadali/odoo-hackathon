package com.example.skillswap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;
    private Button registerButton;
    private TextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePassword = findViewById(R.id.togglePassword);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.loginRedirect);

        // Toggle password visibility
        togglePassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Register click logic
        registerButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Call API to register
                new RegisterTask().execute(name, email, password);
            }
        });

        // Login redirect
        loginRedirect.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // AsyncTask for registration API call
    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String email = params[1];
            String password = params[2];
            String apiUrl = "http://10.0.2.2:8000/api/auth/register/"; // Use 10.0.2.2 for Android emulator

            try {
                java.net.URL url = new java.net.URL(apiUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Prepare JSON
                String jsonInputString = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\",\"first_name\":\"%s\",\"last_name\":\"\"}",
                    name, password, email, name
                );

                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                if (code == 201) {
                    return "success";
                } else {
                    java.io.InputStream errorStream = conn.getErrorStream();
                    java.util.Scanner s = new java.util.Scanner(errorStream).useDelimiter("\\A");
                    String error = s.hasNext() ? s.next() : "";
                    return "error: " + error;
                }
            } catch (Exception e) {
                return "error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}