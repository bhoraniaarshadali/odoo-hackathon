package com.example.skillswap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {

    private RecyclerView statusRecyclerView;
    private TextView noRequestsText;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String SWAP_REQUESTS_ENDPOINT = "swap-requests/";

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        
        // Initialize views
        statusRecyclerView = view.findViewById(R.id.statusRecyclerView);
        noRequestsText = view.findViewById(R.id.noRequestsText);
        
        // Load swap requests from API
        new LoadSwapRequestsTask().execute();
        
        return view;
    }
    
    private class LoadSwapRequestsTask extends AsyncTask<Void, Void, LoadSwapRequestsResult> {
        
        @Override
        protected LoadSwapRequestsResult doInBackground(Void... voids) {
            try {
                URL url = new URL(API_BASE_URL + SWAP_REQUESTS_ENDPOINT);
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
                    JSONArray requestsArray = jsonResponse.getJSONArray("results");
                    
                    List<SwapRequestModel> requests = new ArrayList<>();
                    for (int i = 0; i < requestsArray.length(); i++) {
                        JSONObject requestJson = requestsArray.getJSONObject(i);
                        SwapRequestModel request = SwapRequestModel.fromJson(requestJson);
                        if (request != null) {
                            requests.add(request);
                        }
                    }
                    
                    return new LoadSwapRequestsResult(true, requests, "Swap requests loaded successfully");
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to load swap requests");
                    return new LoadSwapRequestsResult(false, null, errorMessage);
                }
                
            } catch (Exception e) {
                return new LoadSwapRequestsResult(false, null, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(LoadSwapRequestsResult result) {
            if (result.success) {
                if (result.requests.isEmpty()) {
                    noRequestsText.setVisibility(View.VISIBLE);
                    statusRecyclerView.setVisibility(View.GONE);
                } else {
                    noRequestsText.setVisibility(View.GONE);
                    statusRecyclerView.setVisibility(View.VISIBLE);
                    
                    // TODO: Create SwapRequestAdapter and set it to RecyclerView
                    // SwapRequestAdapter adapter = new SwapRequestAdapter(result.requests);
                    // statusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    // statusRecyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_LONG).show();
                noRequestsText.setVisibility(View.VISIBLE);
                statusRecyclerView.setVisibility(View.GONE);
            }
        }
    }
    
    private static class LoadSwapRequestsResult {
        boolean success;
        List<SwapRequestModel> requests;
        String message;
        
        LoadSwapRequestsResult(boolean success, List<SwapRequestModel> requests, String message) {
            this.success = success;
            this.requests = requests;
            this.message = message;
        }
    }
    
    // Model class for swap requests
    private static class SwapRequestModel {
        private String id, requesterName, targetName, message, status, createdAt;
        
        public SwapRequestModel(String id, String requesterName, String targetName, 
                              String message, String status, String createdAt) {
            this.id = id;
            this.requesterName = requesterName;
            this.targetName = targetName;
            this.message = message;
            this.status = status;
            this.createdAt = createdAt;
        }
        
        public static SwapRequestModel fromJson(JSONObject json) {
            try {
                return new SwapRequestModel(
                    json.optString("id", ""),
                    json.optString("requester_name", ""),
                    json.optString("target_name", ""),
                    json.optString("message", ""),
                    json.optString("status", ""),
                    json.optString("created_at", "")
                );
            } catch (Exception e) {
                return null;
            }
        }
        
        // Getters
        public String getId() { return id; }
        public String getRequesterName() { return requesterName; }
        public String getTargetName() { return targetName; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
    }
}
