package com.smokebreaker.www.bl.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tip {
    User author;
    String uid;
    String key;
    String tip;
    private boolean vote;
    private int votes;

    public Tip() {
    }

    public Tip(String input) {
        this.tip = input;
    }

    //region g&s

    @Exclude
    public User getAuthor() {
        return author;
    }

    @Exclude
    public void setAuthor(User author) {
        this.author = author;
        this.uid = author.getUid();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Exclude
    public boolean getVote() {
        return vote;
    }

    @Exclude
    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    //endregion
}
