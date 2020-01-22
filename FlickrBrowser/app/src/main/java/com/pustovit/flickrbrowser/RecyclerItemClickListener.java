package com.pustovit.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Pustovit Vladimir on 10.07.2019.
 * vovapust1989@gmail.com
 */

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickLisTag";

    private final RecyclerItemClickListener.OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;


    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, final RecyclerItemClickListener.OnRecyclerClickListener mListener) {
        this.mListener = mListener;

        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (view != null) {
                    mListener.onItemLongClick(view, recyclerView.getChildAdapterPosition(view));
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (view != null) {
                    mListener.onItemClick(view, recyclerView.getChildAdapterPosition(view));
                }
                return true;
            }
        });
    }

    interface OnRecyclerClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: start");
        if(mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned "+ result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent: returned FALSE");
            return false;
        }
    }
}
