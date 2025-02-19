package com.example.haphaestussboxoftrinkets;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.haphaestussboxoftrinkets.databinding.FragmentWheelBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WheelFragment extends Fragment {

    public WheelFragment() {
        // Required empty public constructor
    }

    String TAG = "tag";
    PieChart chartView;
    private FragmentWheelBinding binding;

    ArrayList<Game> gameList = new ArrayList<>();
    int tempDegrees;
    float previousFinalDegree = 0;

    ArrayList<Integer> color = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWheelBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (gameList.isEmpty()) {
            binding.spinFAB.setVisibility(View.INVISIBLE);
            binding.selector.setVisibility(View.INVISIBLE);
        } else {
            for (Game game : gameList) {
                Random r = new Random();
                color.add(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
            }
        }

        chartView = (PieChart) binding.pieChart;
        FloatingActionButton button = binding.spinFAB;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                int spinDegrees = 1080 + r.nextInt(2521);
                tempDegrees = spinDegrees;

                long duration = 2000;

                chartView.animate().rotationBy(-spinDegrees).setDuration(duration);

                chartView.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        AlertDialog gameWinner = new AlertDialog.Builder(getContext()).create();
                        gameWinner.setMessage(findGame());
                        gameWinner.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                gameWinner.show();
                            }
                        }, 200);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
            }
        });
        setupChart();

        binding.addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeItem();
            }
        });

        binding.editFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameList.isEmpty()) {
                    Toast.makeText(getContext(), "Please add an game first!", Toast.LENGTH_LONG).show();
                } else {
                    viewItem();
                }
            }
        });
    }

    public void setupChart(){

        List<PieEntry> entries = new ArrayList<>();

        for (Game game : gameList) {
            entries.add(new PieEntry(game.getScore()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2);
        dataSet.setDrawValues(false);
        dataSet.setColors(color);

        PieData pieData = new PieData(dataSet);
        chartView.setData(pieData);
        chartView.getDescription().setEnabled(false);
        chartView.getLegend().setEnabled(false);
        chartView.invalidate();
    }

    private void makeItem() {
        Random r = new Random();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_game_input, null);
        builder.setView(dialogView);

        EditText gameNameInput = dialogView.findViewById(R.id.editGameName);
        EditText gameScoreInput = dialogView.findViewById(R.id.editGameScore);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String gameName = gameNameInput.getText().toString();
            int gameScore = Integer.parseInt(gameScoreInput.getText().toString());

            gameList.add(new Game(gameName, gameScore));
            Toast.makeText(getContext(), "Added: " + gameName + " - Score: " + gameScore, Toast.LENGTH_LONG).show();

            color.add(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
            setupChart();

            if (binding.spinFAB.getVisibility() == View.INVISIBLE) {
                binding.spinFAB.setVisibility(View.VISIBLE);
                binding.selector.setVisibility(View.VISIBLE);
            }
        });

        builder.show();
    }

    private void viewItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GameAdapter adapter = new GameAdapter(gameList, color);
        recyclerView.setAdapter(adapter);

        builder.setView(recyclerView);

        AlertDialog dialog = builder.create();
        dialog.show();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                gameList.remove(position);
                adapter.notifyItemRemoved(position);
                setupChart();

                if (gameList.isEmpty()) {
                    binding.spinFAB.setVisibility(View.INVISIBLE);
                    binding.selector.setVisibility(View.INVISIBLE);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private String findGame() {
        float degreePerScore;
        int totalScore = 0;
        float finalDegree;
        float buffer = 0;
        String gameName = "";

        for (Game game : gameList) {
            totalScore += game.score;
        }

        degreePerScore = totalScore / 360f;

        finalDegree = (this.tempDegrees % 360) + previousFinalDegree;

        if (finalDegree >= 360)
            finalDegree -= 360;

        previousFinalDegree = finalDegree;

        for (Game game : gameList) {
            buffer += game.getScore() / degreePerScore;

            if (finalDegree < buffer) {
                gameName = game.getName();
                break;
            }
        }

        Log.d(TAG, "previousFinalDegree: " +  previousFinalDegree);
        Log.d(TAG, "tempDegrees: " + tempDegrees);
        Log.d(TAG, "finalDegree: " + finalDegree);
        Log.d(TAG, "buffer: " + buffer);

        return gameName;
    }
}