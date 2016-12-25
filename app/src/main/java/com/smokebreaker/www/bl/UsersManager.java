package com.smokebreaker.www.bl;

import com.smokebreaker.www.bl.models.User;

import rx.Observable;

public interface UsersManager {
    User getCurrentUser();

    Observable<User> get(String uid);
}
