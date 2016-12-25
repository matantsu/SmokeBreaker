package com.smokebreaker.www.pl.tips;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.view.IconicsImageView;
import com.smokebreaker.www.pl.tips.OnDelete;
import com.smokebreaker.www.pl.tips.OnThumbsUp;
import com.smokebreaker.www.R;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.models.Tip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TipItem extends AbstractItem<TipItem,TipItem.ViewHolder> implements OnDelete, OnThumbsUp {

    private final boolean isMine;
    private final TipsFragment fragment;
    Tip tip;
    public TipItem(TipsFragment fragment, Tip tip, boolean isMine) {
        this.fragment = fragment;
        this.isMine = isMine;
        this.tip = tip;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tip;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.tip.setText(tip.getTip());
        Utils.setUser(holder.user,tip.getAuthor());

        Utils.show(holder.user,!isMine);
        holder.card.setCardBackgroundColor(holder.context.getResources().getColor(isMine ? R.color.colorPrimaryLight : R.color.md_white_1000));

        Utils.show(holder.deleteButton, isMine);
        holder.thumbsUpButton.setColor(holder.context.getResources().getColor(tip.getVote() ? R.color.colorPrimary : R.color.ampm_text_color));
        Utils.show(holder.votes,tip.getVotes() != 0);
        holder.votes.setText(holder.context.getString(R.string.tip_number_of_votes,tip.getVotes()));

        holder.setOnDelete(this);
        holder.setOnThumbsUp(this);
    }

    @Override
    public void onDelete(int adapterPosition) {
        fragment.onDelete(tip,adapterPosition);
    }

    @Override
    public void onThumbsUp(int adapterPosition) {
        fragment.onVote(tip,adapterPosition);
    }

    @Override
    public long getIdentifier() {
        return tip.getKey().hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        Context context;

        @BindView(R.id.tip)
        TextView tip;

        @BindView(R.id.card)
        CardView card;

        @BindView(R.id.user)
        TextView user;

        @BindView(R.id.votes)
        TextView votes;

        @BindView(R.id.thumbsUpButton)
        IconicsImageView thumbsUpButton;

        @BindView(R.id.deleteButton)
        View deleteButton;

        OnDelete onDelete;
        OnThumbsUp onThumbsUp;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.deleteButton)
        public void onDeleteButtonClick(View v){
            if(onDelete != null){
                new MaterialDialog.Builder(context)
                        .title(R.string.delete_tip_dialog_title)
                        .positiveText(R.string.ok)
                        .onPositive((dialog, which) -> onDelete.onDelete(getAdapterPosition()))
                        .negativeText(R.string.cancel)
                        .show();
            }

        }

        @OnClick(R.id.thumbsUpButton)
        public void onThumbsUpButtonClick(View v){
            if(onThumbsUp != null)
                onThumbsUp.onThumbsUp(getAdapterPosition());
        }

        public void setOnDelete(OnDelete onDelete) {
            this.onDelete = onDelete;
        }

        public void setOnThumbsUp(OnThumbsUp onThumbsUp) {
            this.onThumbsUp = onThumbsUp;
        }
    }
}
