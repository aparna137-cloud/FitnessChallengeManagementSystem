package com.example.fitnesschallenge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.ParticipantChallengeAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Participant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private BarChart barChart;
    private RecyclerView rvProgressBreakdown;
    private ParticipantChallengeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        initViews(view);
        setupChart();
        setupRecyclerView();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshProgress);
        barChart = view.findViewById(R.id.barChartProgress);
        rvProgressBreakdown = view.findViewById(R.id.rvProgressBreakdown);
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        leftAxis.setGridColor(ContextCompat.getColor(requireContext(), R.color.divider));

        barChart.getAxisRight().setEnabled(false);
        barChart.setNoDataText("No challenge progress data yet. Join a challenge to view chart!");
        barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
    }

    private void setupRecyclerView() {
        adapter = new ParticipantChallengeAdapter(requireContext(), new ParticipantChallengeAdapter.OnParticipantActionListener() {
            @Override
            public void onChallengeClick(Participant participant) {
            }

            @Override
            public void onLogActivityClick(Participant participant) {
            }
        });
        rvProgressBreakdown.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProgressBreakdown.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadProgressData);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProgressData();
    }

    private void loadProgressData() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);

        FirestoreManager.getInstance().getMyChallenges(uid, new FirestoreManager.FirestoreCallback<List<Participant>>() {
            @Override
            public void onSuccess(List<Participant> participants) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);

                if (participants == null || participants.isEmpty()) {
                    barChart.clear();
                    adapter.setParticipants(new ArrayList<>());
                    return;
                }

                adapter.setParticipants(participants);
                updateChartWithParticipants(participants);
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void updateChartWithParticipants(List<Participant> participants) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);
        int completedColor = ContextCompat.getColor(requireContext(), R.color.secondary);

        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            int pct = p.getProgressPercentage();
            entries.add(new BarEntry(i, (float) pct));

            String title = p.getChallengeTitle();
            if (title.length() > 8) {
                labels.add(title.substring(0, 8) + "..");
            } else {
                labels.add(title);
            }

            if (pct >= 100) {
                colors.add(completedColor);
            } else {
                colors.add(primaryColor);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Progress (% Completed)");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        dataSet.setValueTextSize(11f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());

        barChart.setData(data);
        barChart.animateY(800);
        barChart.invalidate();
    }
}
