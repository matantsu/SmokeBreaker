package com.smokebreaker.www.pl.achievements;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.models.Achievement;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AchievementItem extends AbstractItem<AchievementItem,AchievementItem.ViewHolder> {

    Achievement achievement;

    public AchievementItem(Achievement achievement) {
        this.achievement = achievement;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.achievement;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        Utils.show(holder.unlocked,achievement.isAchieved());
        holder.card.setCardBackgroundColor(holder.context.getResources().getColor(achievement.isAchieved() ? R.color.colorPrimary : R.color.md_white_1000));
        holder.title.setTextColor(holder.context.getResources().getColor(achievement.isAchieved() ? R.color.md_white_1000 : R.color.ampm_text_color));
        holder.subtitle.setTextColor(holder.context.getResources().getColor(achievement.isAchieved() ? R.color.md_white_1000 : R.color.ampm_text_color));

        holder.title.setText(achievement.getName());
        holder.subtitle.setText(achievement.getDescription());

        Utils.show(holder.trophy1,achievement.getRank() >= 0);
        Utils.show(holder.trophy2,achievement.getRank() >= 1);
        Utils.show(holder.trophy3,achievement.getRank() >= 2);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;

        @BindView(R.id.card)
        CardView card;

        @BindView(R.id.unlocked)
        View unlocked;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.subtitle)
        TextView subtitle;

        @BindView(R.id.trophy1)
        View trophy1;

        @BindView(R.id.trophy2)
        View trophy2;

        @BindView(R.id.trophy3)
        View trophy3;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }
    }
}
