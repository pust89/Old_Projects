package com.pustovit.tasktimer.debug;

/**
 * Created by Pustovit Vladimir on 24.10.2019.
 * vovapust1989@gmail.com
 */

public class TestTiming {
    long taskId;
    long startTime;
    long duration;

    public TestTiming(long taskId, long startTime, long duration) {
        this.taskId = taskId;
        this.startTime = startTime/1000;
        this.duration = duration;
    }
}
