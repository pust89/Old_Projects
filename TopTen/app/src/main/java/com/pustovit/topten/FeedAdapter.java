package com.pustovit.topten;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pustovit Vladimir on 08.06.2019.
 * vovapust1989@gmail.com
 */

public class FeedAdapter<T extends FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResourse;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    public FeedAdapter(Context context, int resource, List<T> applications) {
        super(context, resource);
        layoutResourse = resource;
        layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResourse, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry entry = applications.get(position);

        viewHolder.tvName.setText(entry.getName());
        viewHolder.tvArtist.setText(entry.getArtist());
        viewHolder.tvSummary.setText(entry.getSummary());
        Picasso.get().load(entry.getImgUrl()).into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final ImageView imageView;

        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.imageView =v.findViewById(R.id.imageView);
        }
    }
}
