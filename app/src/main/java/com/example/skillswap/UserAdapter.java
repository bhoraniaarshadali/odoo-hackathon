package com.example.skillswap;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserModel> userList;

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
            Toast.makeText(v.getContext(), "Swap requested with " + user.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Send request logic here
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
