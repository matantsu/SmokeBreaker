package com.smokebreaker.www.pl.statistics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Statistic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class StatisticItem extends AbstractItem<StatisticItem,StatisticItem.ViewHolder> {

    Statistic statistic;
    public StatisticItem(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.statistic;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.text.setText(statistic.getStatus());
        holder.progress.setProgress((int) (statistic.getProgress()*100));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final Context context;

        @BindView(R.id.text)
        TextView text;

        @BindView(R.id.progress)
        RingProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }
    }
}
