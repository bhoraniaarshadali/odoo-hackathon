package com.example.skillswap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView searchRecyclerView;
    private UserAdapter adapter;
    private List<UserModel> userList, filteredList;
    
    // API Configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000/api/";
    private static final String USERS_ENDPOINT = "users/";

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);

        // Initialize lists
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Adapter setup
        adapter = new UserAdapter(filteredList);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(adapter);

        // Load users from API
        new LoadUsersTask().execute();

        // Filter logic
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
        });

        return view;
    }

    private void filterUsers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (UserModel user : userList) {
                if (user.getOfferedSkills().toLowerCase().contains(query.toLowerCase()) ||
                        user.getWantedSkills().toLowerCase().contains(query.toLowerCase()) ||
                        user.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (user.getLocation() != null && user.getLocation().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    private class LoadUsersTask extends AsyncTask<Void, Void, LoadUsersResult> {
        
        @Override
        protected LoadUsersResult doInBackground(Void... voids) {
            try {
                URL url = new URL(API_BASE_URL + USERS_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                
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
                    JSONArray usersArray = jsonResponse.getJSONArray("results");
                    
                    List<UserModel> users = new ArrayList<>();
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        UserModel user = UserModel.fromJson(userJson);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    
                    return new LoadUsersResult(true, users, "Users loaded successfully");
                } else {
                    // Error
                    JSONObject errorResponse = new JSONObject(response.toString());
                    String errorMessage = errorResponse.optString("error", "Failed to load users");
                    return new LoadUsersResult(false, null, errorMessage);
                }
                
            } catch (Exception e) {
                return new LoadUsersResult(false, null, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(LoadUsersResult result) {
            if (result.success) {
                userList.clear();
                userList.addAll(result.users);
                filteredList.clear();
                filteredList.addAll(userList);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_LONG).show();
                // Fallback to dummy data if API fails
                loadDummyData();
            }
        }
    }
    
    private void loadDummyData() {
        userList.clear();
        userList.add(new UserModel("Arshad B.", "Java, Photoshop", "Python, Figma", 4));
        userList.add(new UserModel("Arvind P.", "HTML, Excel", "Adobe XD", 3));
        filteredList.clear();
        filteredList.addAll(userList);
        adapter.notifyDataSetChanged();
    }
    
    private static class LoadUsersResult {
        boolean success;
        List<UserModel> users;
        String message;
        
        LoadUsersResult(boolean success, List<UserModel> users, String message) {
            this.success = success;
            this.users = users;
            this.message = message;
        }
    }
}
