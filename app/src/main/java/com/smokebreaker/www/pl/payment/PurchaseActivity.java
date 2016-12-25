package com.smokebreaker.www.pl.payment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.PaymentManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseActivity extends RxAppCompatActivity {

    @Inject
    PaymentManager paymentManager;

    @BindView(R.id.rv)
    RecyclerView rv;

    private FastItemAdapter<PaymentItem> adapter;
    private HeaderAdapter<PaymentHeaderItem> headerAdapter;
    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        App.getAppComponent(this).inject(this);
        billingProcessor = paymentManager.getBillingProcessor();
        ButterKnife.bind(this);

        adapter = new FastItemAdapter<>();
        headerAdapter = new HeaderAdapter<>();

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(headerAdapter.wrap(adapter));

        headerAdapter.add(new PaymentHeaderItem());

        paymentManager.getSkusStream().compose(bindToLifecycle()).subscribe(skus->{
            for(SkuDetails sku : skus)
                if(sku != null)
                    adapter.add(new PaymentItem(sku,this));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void purchase(String productId) {
        paymentManager.getBillingProcessor().purchase(this,productId);
    }
}
