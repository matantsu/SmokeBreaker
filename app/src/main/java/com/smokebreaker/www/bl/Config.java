package com.smokebreaker.www.bl;

import java.util.List;

public interface Config {
    long tipsInterval();
    double timeRegainedFromAvoidingCigarette();
    long smokeBreakDuration();
    int numberOfDaysToAverage();
    double interstitialFrequency();

    List<String> productIds();

    List<String> subscriptionIds();
}
