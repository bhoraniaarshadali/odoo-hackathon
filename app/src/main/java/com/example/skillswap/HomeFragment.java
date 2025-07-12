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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HomeFragment extends Fragment {
    
    private TextView welcomeText, statsText, recentActivityText;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String DASHBOARD_ENDPOINT = "dashboard/";

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        welcomeText = view.findViewById(R.id.welcomeText);
        statsText = view.findViewById(R.id.statsText);
        recentActivityText = view.findViewById(R.id.recentActivityText);
        
        // Load dashboard data from API
        new LoadDashboardTask().execute();
        
        return view;
    }
    
    private class LoadDashboardTask extends AsyncTask<Void, Void, LoadDashboardResult> {
        
        @Override
        protected LoadDashboardResult doInBackground(Void... voids) {
            try {
                URL url = new URL(API_BASE_URL + DASHBOARD_ENDPOINT);
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
                    return new LoadDashboardResult(true, jsonResponse, "Dashboard loaded successfully");
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to load dashboard");
                    return new LoadDashboardResult(false, null, errorMessage);
                }
                
            } catch (Exception e) {
                return new LoadDashboardResult(false, null, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(LoadDashboardResult result) {
            if (result.success) {
                try {
                    JSONObject dashboard = result.dashboardData;
                    
                    // Update welcome message
                    String userName = dashboard.optString("user_name", "User");
                    welcomeText.setText("Welcome back, " + userName + "!");
                    
                    // Update statistics
                    int totalSwaps = dashboard.optInt("total_swaps", 0);
                    int pendingRequests = dashboard.optInt("pending_requests", 0);
                    int completedSwaps = dashboard.optInt("completed_swaps", 0);
                    
                    String stats = String.format("📊 Your Stats:\n" +
                            "• Total Swaps: %d\n" +
                            "• Pending Requests: %d\n" +
                            "• Completed Swaps: %d", 
                            totalSwaps, pendingRequests, completedSwaps);
                    statsText.setText(stats);
                    
                    // Update recent activity
                    String recentActivity = dashboard.optString("recent_activity", "No recent activity");
                    recentActivityText.setText("🕒 Recent Activity:\n" + recentActivity);
                    
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error parsing dashboard data", Toast.LENGTH_SHORT).show();
                    loadDummyData();
                }
            } else {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_LONG).show();
                loadDummyData();
            }
        }
    }
    
    private void loadDummyData() {
        welcomeText.setText("Welcome back, User!");
        statsText.setText("📊 Your Stats:\n• Total Swaps: 5\n• Pending Requests: 2\n• Completed Swaps: 3");
        recentActivityText.setText("🕒 Recent Activity:\n• Swap request sent to John\n• Completed swap with Sarah\n• New skill added: Python");
    }
    
    private static class LoadDashboardResult {
        boolean success;
        JSONObject dashboardData;
        String message;
        
        LoadDashboardResult(boolean success, JSONObject dashboardData, String message) {
            this.success = success;
            this.dashboardData = dashboardData;
            this.message = message;
        }
    }
}
