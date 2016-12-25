package com.smokebreaker.www.bl;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import com.smokebreaker.www.bl.models.Tip;
import com.smokebreaker.www.pl.notifications.TipNotification;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class TipsManagerImpl implements TipsManager {

    private final DatabaseReference tipsRef;
    private final UsersManager usersManager;
    private final DatabaseReference tipsRatingsRef;
    private final FirebaseAnalytics analytics;
    private final Settings settings;
    DatabaseReference db;

    List<Tip> tips = new LinkedList<>();

    @Inject
    public TipsManagerImpl(Context context, UsersManager usersManager, Application application,Config config,Settings settings) {
        analytics = FirebaseAnalytics.getInstance(context);
        this.usersManager = usersManager;
        this.settings = settings;

        db = FirebaseDatabase.getInstance().getReference();
        tipsRef = db.child("tips").child("eng");
        tipsRatingsRef = db.child("tipsRatings");

        getTips()
                .subscribe(t->tips = t);

        BroadcastReceiver randomTipReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Tip tip = randomTip();
                if(tip != null && settings.isTipNotificationsSwitchEnabled())
                    TipNotification.notify(context,tip);
            }
        };

        application.registerReceiver(randomTipReceiver,new IntentFilter(application.getPackageName()+"randomTip"));
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                0,
                config.tipsInterval(),
                PendingIntent.getBroadcast(application,
                        345,
                        new Intent(application.getPackageName()+"randomTip"),
                        PendingIntent.FLAG_CANCEL_CURRENT));
    }

    @Override
    public void addTip(String input) {
        DatabaseReference fresh = tipsRef.push();

        Tip tip = new Tip(input);
        tip.setAuthor(usersManager.getCurrentUser());
        tip.setKey(fresh.getKey());

        fresh.setValue(tip);

        Bundle bundle = new Bundle();
        bundle.putString("tipKey",tip.getKey());
        analytics.logEvent("tipAdd",bundle);
    }

    @Override
    public Observable<List<Tip>> getTips() {
        return RxFirebaseDatabase.observeValueEvent(tipsRef.orderByChild("votes").limitToLast(50))
                .map(DataSnapshot::getChildren)
                .flatMap(data->Observable.from(data)
                        .map(d->d.getValue(Tip.class))
                        .flatMap(this::attachAuthor)
                        .flatMap(this::attachVote)
                        .toList());
    }

    private Observable<Tip> attachAuthor(Tip tip){
        return usersManager.get(tip.getUid())
                .doOnNext(tip::setAuthor)
                .map(r->tip);
    }

    private Observable<Tip> attachVote(Tip tip){
        return RxFirebaseDatabase.observeSingleValueEvent(tipsRatingsRef.child(tip.getKey()).child(usersManager.getCurrentUser().getUid()))
                .map(d->d.exists() ? d.getValue(Boolean.class) : false)
                .doOnNext(tip::setVote)
                .map(r->tip);
    }

    @Override
    public void deleteTip(Tip tip) {
        tipsRef.child(tip.getKey()).setValue(null);
        tipsRatingsRef.child(tip.getKey()).setValue(null);

        Bundle bundle = new Bundle();
        bundle.putString("tipKey",tip.getKey());
        analytics.logEvent("tipDelete",bundle);
    }

    @Override
    public void vote(Tip tip, boolean b){
        tip.setVote(b);
        DatabaseReference ref = tipsRatingsRef.child(tip.getKey())
                .child(usersManager.getCurrentUser().getUid());
        if(b){
            ref.setValue(true);
            tip.setVotes(tip.getVotes()+1);
        }
        else{
            ref.removeValue();
            tip.setVotes(tip.getVotes()-1);
        }

        tipsRef.child(tip.getKey()).child("votes").setValue(tip.getVotes());

        Bundle bundle = new Bundle();
        bundle.putString("tipKey",tip.getKey());
        bundle.putString("vote",b+"");
        analytics.logEvent("tipVote",bundle);
    }

    @Override
    public Tip randomTip(){
        if(tips.size() > 0)
            return tips.get((int) (Math.random()*tips.size()));
        else
            return null;
    }

    @Override
    public Observable<Tip> randomTipAsync(){
        return getTips().map(tips->{
            this.tips = tips;
            return randomTip();
        });
    }
}
