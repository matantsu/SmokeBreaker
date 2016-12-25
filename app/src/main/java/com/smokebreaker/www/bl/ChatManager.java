package com.smokebreaker.www.bl;

import com.smokebreaker.www.bl.models.ChatMessage;

import rx.Observable;

public interface ChatManager {
    void send(String message);

    Observable<ChatMessage> chatStream();
    Observable<ChatMessage> lastMessageAsync();
}
