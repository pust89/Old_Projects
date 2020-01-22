package com.pustovit.tasktimer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Pustovit Vladimir on 02.10.2019.
 * vovapust1989@gmail.com
 */

public class Task implements Parcelable {
   // public static final long serialVersionUID = 20191002L;

    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    public Task(long id, String name, String description, int sortOrder) {
        this.m_Id = id;
        mName = name;
        mDescription = description;
        mSortOrder = sortOrder;
    }

    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return "Task{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(m_Id);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
        parcel.writeInt(mSortOrder);

    }

    public static Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel parcel) {
            return new Task(parcel);
        }

        @Override
        public Task[] newArray(int i) {
            return new Task[i];
        }
    };

    private Task(Parcel parcel) {
        m_Id = parcel.readLong();
        mName = parcel.readString();
        mDescription = parcel.readString();
        mSortOrder = parcel.readInt();
    }


}
