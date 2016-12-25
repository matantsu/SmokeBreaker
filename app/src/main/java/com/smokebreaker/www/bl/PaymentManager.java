package com.smokebreaker.www.bl;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;

import java.util.List;

import rx.Observable;

public interface PaymentManager {
    BillingProcessor getBillingProcessor();
    boolean isPro();
    List<SkuDetails> getSkus();

    Observable<List<SkuDetails>> getSkusStream();

    Observable<Boolean> getIsProStream();
}
