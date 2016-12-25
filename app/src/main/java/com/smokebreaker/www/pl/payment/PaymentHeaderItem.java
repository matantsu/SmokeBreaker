package com.smokebreaker.www.pl.payment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;

public class PaymentHeaderItem extends AbstractItem<PaymentHeaderItem,PaymentHeaderItem.ViewHolder> {
    @Override
    public int getType() {
        return R.id.payment_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.payment_header;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
