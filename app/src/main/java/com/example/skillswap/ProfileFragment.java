package com.example.skillswap;

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

public class ProfileFragment extends Fragment {

    private ImageView profileImage, editIcon;
    private EditText nameField, locationField, skillsField, availabilityField;
    private Switch publicSwitch;

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

        // Optional: Add dummy values
        nameField.setText("Arshad ali B.");
        locationField.setText("Vadodara");
        skillsField.setText("OOPS, Figma, Photoshop");
        availabilityField.setText("Saturday, Sunday");

        // Public/private toggle listener
        publicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String type = isChecked ? "Public" : "Private";
            Toast.makeText(getActivity(), "Profile set to: " + type, Toast.LENGTH_SHORT).show();
        });

        // Edit profile click
        editIcon.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Edit profile clicked!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
