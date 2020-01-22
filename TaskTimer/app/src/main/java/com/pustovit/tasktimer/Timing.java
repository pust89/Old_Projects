package com.pustovit.tasktimer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

/**
 * Simple timing object.
 * Sets it start time when created and calculated how long since creation,
 * when SetDurations is called.
 * <p>
 * Created by Pustovit Vladimir on 22.10.2019.
 * vovapust1989@gmail.com
 */

class Timing implements Parcelable {
    private static final String TAG = Timing.class.getSimpleName();

    private long m_Id;
    private Task mTask;
    private long mStartTime;
    private long mDuration;


    public Timing(Task task) {
        mTask = task;
        //Initialize the start time for a new object.
        Date currrentTime = new Date();
        mStartTime = currrentTime.getTime()/1000;
        mDuration = 0;

    }

      Timing(Parcel in) {
        m_Id = in.readLong();
          mTask = in.readParcelable(Task.class.getClassLoader());
        mStartTime = in.readLong();
        mDuration = in.readLong();
    }

    public static final Creator<Timing> CREATOR = new Creator<Timing>() {
        @Override
        public Timing createFromParcel(Parcel in) {
            return new Timing(in);
        }

        @Override
        public Timing[] newArray(int size) {
            return new Timing[size];
        }
    };

    long getId() {
        return m_Id;
    }

    void setId(long id) {
        m_Id = id;
    }

    Task getTask() {
        return mTask;
    }

    void setTask(Task task) {
        mTask = task;
    }

    long getStartTime() {
        return mStartTime;
    }

    void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    long getDuration() {
        return mDuration;
    }

    void setDuration() {
        long curentTime = new Date().getTime();
        mDuration = (curentTime/1000) - mStartTime;
        Log.d(TAG, mTask.getId()+" - Start time: "+mStartTime +" | Durations: "+mDuration);
    }

    @Override
    public String toString() {
        return "Timing{" +
                "m_Id=" + m_Id +
                ", mTask=" + mTask +
                ", mStartTime=" + mStartTime +
                ", mDuration=" + mDuration +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_Id);
        dest.writeParcelable(mTask,0);
        dest.writeLong(mStartTime);
        dest.writeLong(mDuration);

    }
}
