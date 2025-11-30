package com.example.uwikaquicktypergame.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uwikaquicktypergame.R;
import com.example.uwikaquicktypergame.model.LeaderboardEntry;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<LeaderboardEntry> leaderboardList;

    public LeaderboardAdapter(List<LeaderboardEntry> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardEntry entry = leaderboardList.get(position);
        holder.bind(entry, position + 1);
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRank, textViewUsername, textViewScore;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRank = itemView.findViewById(R.id.textViewRank);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewScore = itemView.findViewById(R.id.textViewScore);
        }

        public void bind(LeaderboardEntry entry, int rank) {
            textViewRank.setText(String.format(Locale.getDefault(), "%d.", rank));
            textViewUsername.setText(entry.getUsername());
            textViewScore.setText(String.valueOf(entry.getFinalScore()));
        }
    }
}
