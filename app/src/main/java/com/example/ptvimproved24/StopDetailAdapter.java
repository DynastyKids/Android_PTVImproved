package com.example.ptvimproved24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StopDetailAdapter extends ArrayAdapter<Route> {

    private Context mContext;
    int mResource;
    public StopDetailAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Route> objects) {
        super(context, resource, objects);
        mContext =context;
        mResource = resource;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String stopRoute = getItem(position).getRoute_gtfs_id();
        String stopTime = getItem(position).getScheduleDepart();
//        String stopDirection = stopDetail[2];

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView direction = (TextView) convertView.findViewById(R.id.stop_direction);
        TextView route = (TextView) convertView.findViewById(R.id.stop_route);
        TextView time = (TextView) convertView.findViewById(R.id.stop_time);


        //direction.setText(stopDirection);
        route.setText(stopRoute);
        time.setText(stopTime);

        return convertView;
    }

}
