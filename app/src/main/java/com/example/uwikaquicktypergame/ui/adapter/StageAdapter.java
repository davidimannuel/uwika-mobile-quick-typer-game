package com.example.uwikaquicktypergame.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uwikaquicktypergame.R;
import com.example.uwikaquicktypergame.model.Stage;
import java.util.List;

public class StageAdapter extends RecyclerView.Adapter<StageAdapter.StageViewHolder> {

    private List<Stage> stageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Stage stage);
    }

    public StageAdapter(List<Stage> stageList, OnItemClickListener listener) {
        this.stageList = stageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stage, parent, false);
        return new StageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StageViewHolder holder, int position) {
        Stage stage = stageList.get(position);
        holder.bind(stage, listener);
    }

    @Override
    public int getItemCount() {
        return stageList.size();
    }

    static class StageViewHolder extends RecyclerView.ViewHolder {
        TextView stageName, stageDifficulty;

        public StageViewHolder(@NonNull View itemView) {
            super(itemView);
            stageName = itemView.findViewById(R.id.textViewStageName);
            stageDifficulty = itemView.findViewById(R.id.textViewStageDifficulty);
        }

        public void bind(final Stage stage, final OnItemClickListener listener) {
            stageName.setText(stage.getName());
            stageDifficulty.setText(stage.getDifficulty());
            itemView.setOnClickListener(v -> listener.onItemClick(stage));
        }
    }
}
