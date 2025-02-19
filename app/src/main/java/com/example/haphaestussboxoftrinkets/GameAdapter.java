package com.example.haphaestussboxoftrinkets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private List<Game> gameList;
    private List<Integer> color;

    public GameAdapter(List<Game> gameList, List<Integer> color) {
        this.gameList = gameList;
        this.color = color;
    }

    @NonNull
    @Override
    public GameAdapter.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_game_list, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.GameViewHolder holder, int position) {
        holder.gameName.setText(gameList.get(position).getName());
        holder.gameScore.setText(gameList.get(position).getScore() + "");
        holder.gameListLayout.setBackgroundColor(color.get(position));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder{
        TextView gameName;
        TextView gameScore;
        ConstraintLayout gameListLayout;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameScore = itemView.findViewById(R.id.score);
            gameListLayout = itemView.findViewById(R.id.gameListLayout);
        }
    }
}
