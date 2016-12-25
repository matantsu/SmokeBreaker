package com.smokebreaker.www.bl;

import android.content.Context;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.smokebreaker.www.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class PaymentManagerImpl implements PaymentManager, BillingProcessor.IBillingHandler {

    private final Config config;
    BillingProcessor billingProcessor;

    Subject<Boolean, Boolean> isProSubject = PublishSubject.create();
    private List<SkuDetails> skus = new LinkedList<>();
    Subject<List<SkuDetails>,List<SkuDetails>> skusSubject = ReplaySubject.create();

    @Inject
    public PaymentManagerImpl(Context context, Config config) {
        this.config = config;
        billingProcessor = new BillingProcessor(context, context.getString(R.string.licence_key), this);
    }

    @Override
    public BillingProcessor getBillingProcessor() {
        return billingProcessor;
    }

    @Override
    public boolean isPro(){
        for(String id : config.productIds())
            if(billingProcessor.isPurchased(id))
                return true;
        for(String id : config.subscriptionIds())
            if(billingProcessor.isSubscribed(id))
                return true;
        return false;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        isProSubject.onNext(isPro());
    }

    @Override
    public void onPurchaseHistoryRestored() {
        isProSubject.onNext(isPro());
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        boolean isSuccessful = billingProcessor.loadOwnedPurchasesFromGoogle();
        if(isSuccessful){
            List<SkuDetails> skus = new LinkedList<>();
            for(String id : config.productIds())
                skus.add(billingProcessor.getPurchaseListingDetails(id));
            for(String id : config.subscriptionIds())
                skus.add(billingProcessor.getSubscriptionListingDetails(id));

            Collections.sort(skus,(a, b)->{
                if(a.priceValue < b.priceValue)
                    return -1;
                else if(a.priceValue > b.priceValue)
                    return 1;
                else
                    return 0;
            });

            this.skus = skus;
            skusSubject.onNext(skus);
            skusSubject.onCompleted();
        }
    }

    @Override
    public List<SkuDetails> getSkus() {
        return skus;
    }

    @Override
    public Observable<List<SkuDetails>> getSkusStream() {
        return skusSubject;
    }

    @Override
    public Observable<Boolean> getIsProStream(){
        return isProSubject;
    }
}
