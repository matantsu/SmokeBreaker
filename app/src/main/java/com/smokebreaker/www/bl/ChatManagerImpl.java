package com.smokebreaker.www.bl;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseChildEvent;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import com.smokebreaker.www.bl.models.ChatMessage;

import java.util.Date;

import javax.inject.Inject;

import rx.Observable;

public class ChatManagerImpl implements ChatManager {

    private final DatabaseReference chatRef;
    UsersManager usersManager;

    @Inject
    public ChatManagerImpl(Context context, UsersManager usersManager) {
        this.usersManager = usersManager;
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        chatRef = db.getReference("chat").child("eng");
    }

    @Override
    public void send(String message){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(usersManager.getCurrentUser());
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(new Date().getTime());

        chatRef.push().runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(chatMessage);
                mutableData.child("timestamp").setValue(ServerValue.TIMESTAMP);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public Observable<ChatMessage> chatStream(){
        return chatStream(50);
    }

    @Override
    public Observable<ChatMessage> lastMessageAsync() {
        return chatStream(1);
    }

    private Observable<ChatMessage> chatStream(int limit){
        return RxFirebaseDatabase.observeChildEvent(chatRef.limitToLast(limit).orderByChild("timestamp"),ChatMessage.class)
                .filter(e->e.getEventType() == RxFirebaseChildEvent.EventType.ADDED)
                .map(RxFirebaseChildEvent::getValue)
                .flatMap(c->usersManager.get(c.getUid()).doOnNext(c::setFrom).map(u->c));
    }
}
