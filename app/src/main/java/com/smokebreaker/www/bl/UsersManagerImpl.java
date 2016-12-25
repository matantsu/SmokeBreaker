package com.smokebreaker.www.bl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import com.smokebreaker.www.bl.models.User;

import javax.inject.Inject;

import rx.Observable;

public class UsersManagerImpl implements UsersManager{

    private final DatabaseReference usersRef;
    private final DatabaseReference myRef;
    User currentUser;

    @Inject
    public UsersManagerImpl() {
        currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");
        myRef = usersRef.child(currentUser.getUid());

        myRef.setValue(currentUser);
    }

    @Override
    public User getCurrentUser(){
        return currentUser;
    }

    @Override
    public Observable<User> get(String uid){
        return RxFirebaseDatabase.observeSingleValueEvent(usersRef.child(uid),User.class);
    }
}
