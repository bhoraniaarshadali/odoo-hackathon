package com.example.skillswap;

import android.os.AsyncTask;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserModel> userList;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String SWAP_REQUEST_ENDPOINT = "swap-requests/";

    public UserAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userSkills, userRating;
        Button actionButton;
        ImageView profileImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userSkills = itemView.findViewById(R.id.userSkills);
            userRating = itemView.findViewById(R.id.userRating);
            actionButton = itemView.findViewById(R.id.actionButton);
            profileImage = itemView.findViewById(R.id.profileImage); // optional if you add this
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.userName.setText(user.getName());
        holder.userSkills.setText("Offered: " + user.getOfferedSkills() + "\nWanted: " + user.getWantedSkills());
        holder.userRating.setText("Ratings: " + "⭐".repeat(user.getRating())); // requires API 24+

        holder.actionButton.setText("Request");
        holder.actionButton.setOnClickListener(v -> {
            // Send swap request via API
            new SwapRequestTask().execute(user.getId(), user.getName());
            Toast.makeText(v.getContext(), "Sending request to " + user.getName() + "...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    private class SwapRequestTask extends AsyncTask<String, Void, SwapRequestResult> {
        
        @Override
        protected SwapRequestResult doInBackground(String... params) {
            String targetUserId = params[0];
            String targetUserName = params[1];
            
            try {
                URL url = new URL(API_BASE_URL + SWAP_REQUEST_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                // TODO: Add Authorization header with user token
                // connection.setRequestProperty("Authorization", "Bearer " + userToken);
                connection.setDoOutput(true);
                
                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("target_user_id", targetUserId);
                jsonPayload.put("message", "I'd like to swap skills with you!");
                jsonPayload.put("status", "pending");
                
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
                
                if (responseCode == 201 || responseCode == 200) {
                    // Success
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String message = jsonResponse.optString("message", "Swap request sent successfully");
                    return new SwapRequestResult(true, message);
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to send swap request");
                    return new SwapRequestResult(false, errorMessage);
                }
                
            } catch (Exception e) {
                return new SwapRequestResult(false, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(SwapRequestResult result) {
            if (result.success) {
                // Show success message
                Toast.makeText(null, result.message, Toast.LENGTH_SHORT).show();
            } else {
                // Show error message
                Toast.makeText(null, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private static class SwapRequestResult {
        boolean success;
        String message;
        
        SwapRequestResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
