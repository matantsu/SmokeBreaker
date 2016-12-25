package com.smokebreaker.www.pl.achievements;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.models.Achievement;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementsFragment extends RxFragment {

    @BindView(R.id.rv)
    RecyclerView rv;
    private FastItemAdapter<AchievementItem> adapter;

    @Inject
    AchievementsManager achievementsManager;

    public AchievementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.achievements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        adapter = new FastItemAdapter<>();

        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);

        for(Achievement achievement : achievementsManager.getAchievements())
            adapter.add(new AchievementItem(achievement));
    }
}
