package com.smokebreaker.www.pl.community;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.models.ChatMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatItem extends AbstractItem<ChatItem,ChatItem.ViewHolder> {
    private final ChatMessage chatMessage;
    private final boolean isMine;

    public ChatItem(ChatMessage c, boolean isMine) {
        this.isMine = isMine;
        this.chatMessage = c;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        Utils.show(holder.user,!isMine);
        MarginLayoutParamsCompat.setMarginStart((ViewGroup.MarginLayoutParams) holder.card.getLayoutParams(),holder.dp(isMine ? 8 : 40));
        MarginLayoutParamsCompat.setMarginEnd((ViewGroup.MarginLayoutParams) holder.card.getLayoutParams(),holder.dp(!isMine ? 8 : 40));
        holder.root.setGravity(isMine ? GravityCompat.START : GravityCompat.END);
        holder.card.setCardBackgroundColor(holder.context.getResources().getColor(isMine ? R.color.colorPrimaryLight : R.color.md_white_1000));

        holder.message.setText(chatMessage.getMessage());
        Utils.setUser(holder.user,chatMessage.getFrom());
        holder.time.setReferenceTime(chatMessage.getTimestamp());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        Context context;
        @BindView(R.id.root)
        LinearLayout root;

        @BindView(R.id.card)
        CardView card;

        @BindView(R.id.user)
        TextView user;

        @BindView(R.id.message)
        TextView message;

        @BindView(R.id.time)
        RelativeTimeTextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }

        public int dp(int dp){
            float d = context.getResources().getDisplayMetrics().density;
            return (int)(dp * d);
        }
    }
}
