package com.example.ptvimproved24;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.ptvimproved24.datastructures.DepartureHttpRequestHandler;
import com.example.ptvimproved24.datastructures.Route;
import com.example.ptvimproved24.datastructures.StopDetailAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ptvimproved24.databinding.ActivityStopsBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class stops extends AppCompatActivity {

    private ActivityStopsBinding binding;
    private ListView detail;

    int stopId;
    int routeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        //toolBarLayout.setTitle(getTitle());

        //show information in the layout
        detail = (ListView) findViewById(R.id.stops_nextservice);
        detail.setNestedScrollingEnabled(true);
        Intent intent = getIntent();
        stopId = intent.getIntExtra("index", 0);
        routeType = intent.getIntExtra("type", 0);
        String stopName = intent.getStringExtra("name");
        String stopSuburb = intent.getStringExtra("suburb");

        toolBarLayout.setTitle(stopName);
        switch (routeType){
            case 0:
                this.findViewById(R.id.toolbar_layout).setBackgroundColor(0xFF0072CE);
                toolBarLayout.setContentScrimColor(0xFF0072CE);
                break;
            case 1:
                this.findViewById(R.id.toolbar_layout).setBackgroundColor(0xFF78BE20);
                toolBarLayout.setContentScrimColor(0xFF78BE20);
                break;
            case 2:
            case 4:
                this.findViewById(R.id.toolbar_layout).setBackgroundColor(0xFFFF8200);
                toolBarLayout.setContentScrimColor(0xFFFF8200);
                break;
            case 3:
                this.findViewById(R.id.toolbar_layout).setBackgroundColor(0xFF8F1A95);
                toolBarLayout.setContentScrimColor(0xFF8F1A95);
                break;
            default:
                break;
        }

        TextView stopsuburb = findViewById(R.id.text_stopsuburb);
        stopsuburb.setText(stopSuburb);

        ArrayList<Route> stopDetail = new ArrayList<>();

        DepartureHttpRequestHandler departureHttpRequestHandler = new DepartureHttpRequestHandler(this);

        StopDetailAdapter adapter = new StopDetailAdapter(this, R.layout.stopdetail, stopDetail);

        detail.setAdapter(adapter);

        departureHttpRequestHandler.getStopNextDepartureDetailByStopId(stopId, routeType, adapter);

        detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), RouteDetails.class);
                Route route = adapter.getItem(i);

                intent.putExtra("run_ref",route.getRun_ref());
                intent.putExtra("route_type", route.getRoute_type());
                startActivity(intent);

            }
        });

        String PREFERENCE_NAME = "SavedStops";
        FloatingActionButton fab = binding.fab;

        SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (pref.contains(Integer.toString(stopId))){
            int color = ContextCompat.getColor(getApplicationContext(), R.color.ptv_network_grey);
            fab.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);}


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
                if (pref.contains(Integer.toString(stopId))){

                    View pop_view = getLayoutInflater().inflate(R.layout.popup_window_stop_detail, null, false);
                    final PopupWindow popWindow = new PopupWindow(pop_view,
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                    popWindow.setTouchable(true);
                    popWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));

                    popWindow.showAtLocation(pop_view, Gravity.CENTER, 0, 0);

                    Button btn_yes = (Button) pop_view.findViewById(R.id.btn_ok);
                    Button btn_cancel = (Button) pop_view.findViewById(R.id.btn_cancel);

                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                            editor.remove(Integer.toString(stopId));
                            editor.apply();

                            Snackbar.make(view, "This stop has been removed from you save-list", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            popWindow.dismiss();

                            int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
                            fab.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popWindow.dismiss();
                        }
                    });

                    return;
                }

                HashMap<String, Object> stop_info = new HashMap<String, Object>();
                stop_info.put("stop_name", stopName);
                stop_info.put("route_type", routeType);
                stop_info.put("suburb", stopSuburb);

                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                JSONObject jsonObject = new JSONObject(stop_info);
                String jsonString = jsonObject.toString();
                editor.putString(Integer.toString(stopId), jsonString);

                editor.apply();

                String TAG = "MyActivity";
                Log.i(TAG, "write success");
                Snackbar.make(view, "Stop saved successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                int color = ContextCompat.getColor(getApplicationContext(), R.color.ptv_network_grey);
                fab.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}