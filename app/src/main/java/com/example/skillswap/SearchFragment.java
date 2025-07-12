package com.example.skillswap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import java.util.*;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView searchRecyclerView;
    private UserAdapter adapter;
    private List<UserModel> userList, filteredList;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);

        // Dummy data
        userList = new ArrayList<>();
        userList.add(new UserModel("Arshad B.", "Java, Photoshop", "Python, Figma", 4));
        userList.add(new UserModel("Arvind P.", "HTML, Excel", "Adobe XD", 3));
        filteredList = new ArrayList<>(userList);

        // Adapter setup
        adapter = new UserAdapter(filteredList);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(adapter);

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
        for (UserModel user : userList) {
            if (user.getOfferedSkills().toLowerCase().contains(query.toLowerCase()) ||
                    user.getWantedSkills().toLowerCase().contains(query.toLowerCase()) ||
                    user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
