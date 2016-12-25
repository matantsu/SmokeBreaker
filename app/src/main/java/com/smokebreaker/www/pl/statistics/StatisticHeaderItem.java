package com.smokebreaker.www.pl.statistics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Statistics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticHeaderItem extends AbstractItem<StatisticHeaderItem,StatisticHeaderItem.ViewHolder>{
    private final Statistics statistics;

    public StatisticHeaderItem(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public int getType() {
        return R.id.statistic_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.statistic_header;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.initialCigarettesPerDay.setText(statistics.initialCigarettesPerDay()+"");
        holder.cigarettesPerDay.setText(statistics.cigarettesPerDay()+"");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final Context context;
        @BindView(R.id.initialCigarettesPerDay)
        TextView initialCigarettesPerDay;

        @BindView(R.id.cigarettesPerDay)
        TextView cigarettesPerDay;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }
    }
}
