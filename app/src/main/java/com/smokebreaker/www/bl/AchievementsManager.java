package com.smokebreaker.www.bl;

import com.smokebreaker.www.bl.models.Achievement;

import java.util.List;

public interface AchievementsManager {
    List<Achievement> getAchievements();
    List<Achievement> getUnachieved();
    Achievement getNext();
    void checkAchievements(boolean silent);

    void checkAchievements();
}
