package com.smokebreaker.www.bl;

import com.smokebreaker.www.bl.models.Tip;

import java.util.List;

import rx.Observable;

public interface TipsManager {
    void addTip(String input);
    Observable<List<Tip>> getTips();
    void deleteTip(Tip tip);
    void vote(Tip tip, boolean b);

    Tip randomTip();

    Observable<Tip> randomTipAsync();
}
