package com.smokebreaker.www.pl.statistics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Statistics;

import butterknife.ButterKnife;

public class StatisticFooterItem extends AbstractItem<StatisticHeaderItem,StatisticFooterItem.ViewHolder>{
    private final Statistics statistics;

    public StatisticFooterItem(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public int getType() {
        return R.id.statistic_footer;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.statistic_footer;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }
    }
}
