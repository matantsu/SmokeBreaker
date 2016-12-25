package com.smokebreaker.www.pl.statistics;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;
import com.smokebreaker.www.bl.models.Statistic;
import com.smokebreaker.www.pl.achievements.AchievementItem;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends RxFragment {

    @Inject
    Statistics statistics;
    private FastItemAdapter adapter;
    private HeaderAdapter<StatisticHeaderItem> headerAdapter;
    private FooterAdapter<StatisticFooterItem> footerAdapter;

    @BindView(R.id.rv)
    RecyclerView rv;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.statistics, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        adapter = new FastItemAdapter<>();
        headerAdapter = new HeaderAdapter<>();
        footerAdapter = new FooterAdapter<>();

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(footerAdapter.wrap(headerAdapter.wrap(adapter)));

        if(statistics.isReady()){
            headerAdapter.add(new StatisticHeaderItem(statistics));
            for(Statistic statistic : statistics.getStatistics())
                adapter.add(new StatisticItem(statistic));
        }
        else
            footerAdapter.add(new StatisticFooterItem(statistics));
    }
}
