package com.pustovit.flickrbrowser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pustovit Vladimir on 27.06.2019.
 * vovapust1989@gmail.com
 */

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickRWAdaptTag";
    private List<Photo> mPhotoList;

    public FlickrRecyclerViewAdapter(List<Photo> photoList) {
        this.mPhotoList = photoList;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "FlickrRecyclerViewAdapter-onCreateViewHolder(): create new ViewHolder ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {

        if(mPhotoList == null || mPhotoList.size()==0){
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.title_empty_card);

        }else {
            Photo photo = mPhotoList.get(position);
            holder.title.setText(photo.getTitle());
            Picasso.get().load(photo.getSmallImage()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(holder.thumbnail);
        }

    }

    @Override
    public int getItemCount() {
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.size() : 1);
    }

    void loadNewData(List<Photo> newListPhoto) {
        mPhotoList = newListPhoto;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position) {
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolderTag";
        ImageView thumbnail;
        TextView title;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder(): start");
            thumbnail = itemView.findViewById(R.id.item_thumbnail);
            title = itemView.findViewById(R.id.item_title);
        }
    }
}
