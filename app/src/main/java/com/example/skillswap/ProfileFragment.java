package com.example.skillswap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProfileFragment extends Fragment {

    private ImageView profileImage, editIcon;
    private EditText nameField, locationField, skillsField, availabilityField;
    private Switch publicSwitch;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String PROFILE_ENDPOINT = "profile/";
    private static final String UPDATE_PROFILE_ENDPOINT = "profile/update/";

    public ProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage);
        editIcon = view.findViewById(R.id.editProfileIcon);
        nameField = view.findViewById(R.id.nameField);
        locationField = view.findViewById(R.id.locationField);
        skillsField = view.findViewById(R.id.skillsField);
        availabilityField = view.findViewById(R.id.availabilityField);
        publicSwitch = view.findViewById(R.id.publicSwitch);

        // Load profile data from API
        new LoadProfileTask().execute();

        // Public/private toggle listener
        publicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String type = isChecked ? "Public" : "Private";
            Toast.makeText(getActivity(), "Profile set to: " + type, Toast.LENGTH_SHORT).show();
            // Save profile visibility setting
            new UpdateProfileTask().execute();
        });

        // Edit profile click
        editIcon.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Saving profile changes...", Toast.LENGTH_SHORT).show();
            new UpdateProfileTask().execute();
        });

        return view;
    }
    
    private class LoadProfileTask extends AsyncTask<Void, Void, LoadProfileResult> {
        
        @Override
        protected LoadProfileResult doInBackground(Void... voids) {
            try {
                URL url = new URL(API_BASE_URL + PROFILE_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                // TODO: Add Authorization header with user token
                // connection.setRequestProperty("Authorization", "Bearer " + userToken);
                
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
                    return new LoadProfileResult(true, jsonResponse, "Profile loaded successfully");
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to load profile");
                    return new LoadProfileResult(false, null, errorMessage);
                }
                
            } catch (Exception e) {
                return new LoadProfileResult(false, null, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(LoadProfileResult result) {
            if (result.success) {
                try {
                    JSONObject profile = result.profileData;
                    nameField.setText(profile.optString("name", ""));
                    locationField.setText(profile.optString("location", ""));
                    skillsField.setText(profile.optString("offered_skills", ""));
                    availabilityField.setText(profile.optString("availability", ""));
                    publicSwitch.setChecked(profile.optBoolean("is_public", true));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error parsing profile data", Toast.LENGTH_SHORT).show();
                    loadDummyData();
                }
            } else {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_LONG).show();
                loadDummyData();
            }
        }
    }
    
    private class UpdateProfileTask extends AsyncTask<Void, Void, UpdateProfileResult> {
        
        @Override
        protected UpdateProfileResult doInBackground(Void... voids) {
            try {
                URL url = new URL(API_BASE_URL + UPDATE_PROFILE_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                // TODO: Add Authorization header with user token
                // connection.setRequestProperty("Authorization", "Bearer " + userToken);
                connection.setDoOutput(true);
                
                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("name", nameField.getText().toString().trim());
                jsonPayload.put("location", locationField.getText().toString().trim());
                jsonPayload.put("offered_skills", skillsField.getText().toString().trim());
                jsonPayload.put("availability", availabilityField.getText().toString().trim());
                jsonPayload.put("is_public", publicSwitch.isChecked());
                
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
                    String message = jsonResponse.optString("message", "Profile updated successfully");
                    return new UpdateProfileResult(true, message);
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to update profile");
                    return new UpdateProfileResult(false, errorMessage);
                }
                
            } catch (Exception e) {
                return new UpdateProfileResult(false, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(UpdateProfileResult result) {
            if (result.success) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void loadDummyData() {
        nameField.setText("Arshad ali B.");
        locationField.setText("Vadodara");
        skillsField.setText("OOPS, Figma, Photoshop");
        availabilityField.setText("Saturday, Sunday");
        publicSwitch.setChecked(true);
    }
    
    private static class LoadProfileResult {
        boolean success;
        JSONObject profileData;
        String message;
        
        LoadProfileResult(boolean success, JSONObject profileData, String message) {
            this.success = success;
            this.profileData = profileData;
            this.message = message;
        }
    }
    
    private static class UpdateProfileResult {
        boolean success;
        String message;
        
        UpdateProfileResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
