package com.example.ptvimproved24;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisruptionsListAdapter extends ArrayAdapter<Disruptions> {

    private Context mContext;
    int mResource;

    public DisruptionsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Disruptions> objects) {
        super(context, resource, objects);
        mContext =context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        String title = getItem(position).getTitle();
        String publishDatetime = getItem(position).getPublishDatetime();
        String disruption_status = getItem(position).getDisruption_status();
        String referenceLink = getItem(position).getReflink();
        int disruptionid = getItem(position).getDisruptionid();
        boolean display_status = getItem(position).isDisplay_status();
        ArrayList<Routes> affectedRoutes = getItem(position).getAffectedRoutes();
        ArrayList<Stoppings> affectedStops = getItem(position).getAffectedStops();
        int affectedRoute_type = -1;

        Disruptions disruptions = new Disruptions( disruptionid,title,referenceLink,title,disruption_status,publishDatetime,affectedRoutes,affectedStops,display_status);

        if (affectedRoutes.size()>0){
            affectedRoute_type = affectedRoutes.get(0).getRoute_type();
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTitle = convertView.findViewById(R.id.dis_title);
        TextView tvTime = convertView.findViewById(R.id.dis_time);
        TextView tvcolor1 = convertView.findViewById(R.id.dis_color1);
        TextView tvcolor2 = convertView.findViewById(R.id.dis_color2);

        tvTitle.setText(title);
        tvTime.setText(publishDatetime);
        tvcolor1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvcolor1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (affectedRoute_type == 0){ // Metro
            tvcolor1.setBackgroundColor(Color.parseColor("#3D9CE3"));
            tvcolor2.setBackgroundColor(Color.parseColor("#3D9CE3"));
        }
        if (affectedRoute_type == 1){ // Tram
            tvcolor1.setBackgroundColor(Color.parseColor("#64B46B"));
            tvcolor2.setBackgroundColor(Color.parseColor("#64B46B"));
        }
        if (affectedRoute_type == 2 || affectedRoute_type == 4){ // Bus, Regional Bus, Night Bus
            tvcolor1.setBackgroundColor(Color.parseColor("#E88A20"));
            tvcolor2.setBackgroundColor(Color.parseColor("#E88A20"));
        }
        if (affectedRoute_type == 3){ // Vline
            tvcolor1.setBackgroundColor(Color.parseColor("#B464A9"));
            tvcolor2.setBackgroundColor(Color.parseColor("#B464A9"));
        }


        return convertView;
    }
}
