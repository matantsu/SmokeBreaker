package com.smokebreaker.www.pl.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.anjlab.android.iab.v3.SkuDetails;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.PaymentManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentItem extends AbstractItem<PaymentItem,PaymentItem.ViewHolder> {
    private final SkuDetails sku;
    private final PurchaseActivity purchaseActivity;

    public PaymentItem(SkuDetails sku, PurchaseActivity purchaseActivity) {
        this.sku = sku;
        this.purchaseActivity = purchaseActivity;
    }

    @Override
    public int getType() {
        return R.id.payment_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.payment_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.title.setText(sku.title);
        holder.text.setText(Html.fromHtml(sku.description));
        holder.button.setText(holder.context.getString(R.string.purchase_button_text,sku.priceText));
        holder.setOnPurchase(() -> purchaseActivity.purchase(sku.productId));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;

        OnPurchase onPurchase;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.text)
        TextView text;

        @BindView(R.id.button)
        TextView button;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.button)
        public void onPurchaseClick(){
            if(onPurchase != null)
                onPurchase.onPurchase();
        }

        @OnClick(R.id.root)
        public void onRootClick(){
            onPurchaseClick();
        }

        public void setOnPurchase(OnPurchase onPurchase) {
            this.onPurchase = onPurchase;
        }

        public interface OnPurchase{
            void onPurchase();
        }
    }
}
