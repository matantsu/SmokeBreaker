package com.smokebreaker.www.bl;

public interface SmokeBreaksManager {
    long getNextBreakTime();
    long getPreviousBreakTime();
    int getNextIndex();

    int getPreviousIndex();

    void onNextBreak();
    int getNumberOfBreaks();
    boolean isReady();
    boolean isActive();
    boolean isOnBreak();
    long getBreakDuration();
    void setNumberOfBreaks(int q);
    Integer getStart();
    Integer getEnd();
    void setStart(Integer minutes);
    void setEnd(Integer minutes);
    void reset();
}
