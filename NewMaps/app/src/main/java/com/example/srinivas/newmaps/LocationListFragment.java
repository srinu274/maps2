package com.example.srinivas.newmaps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Srinivas on 03-04-2016.
 */
public class LocationListFragment extends Fragment {

    private static final int UI_UPDATE = 5;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private static final int PLACES_REQUEST=14;

    private Handler mHandler=new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message input) {
            if (input.what == UI_UPDATE) {
                ArrayList<LocationHandler> data = (ArrayList<LocationHandler>) input.obj;
                ((ListAdapter)mRecyclerView.getAdapter()).setData(data);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_location_list,parent,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ListAdapter());
        mFab=(FloatingActionButton)getActivity().findViewById(R.id.fab);
        return view;
    }



    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        ArrayList<LocationHandler> data;

        ListAdapter() {
            data = null;
        }

        ListAdapter(ArrayList<LocationHandler> data) {
            this.data = data;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_location, parent, false);
            return new ListHolder(view);
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            LocationHandler handler = data.get(position);
            holder.setData(handler);
        }

        public void setData(ArrayList<LocationHandler> data) {
            this.data=data;
        }

        public ArrayList<LocationHandler> getData() {
            return data;
        }

        public void addLocation(LocationHandler handler) {
            if(data==null) data=new ArrayList<>();
            data.add(handler);
            notifyItemInserted(data.size()-1);
        }

    }

    private class ListHolder extends RecyclerView.ViewHolder {
        TextView locationText;
        TextView timeText;

        public ListHolder(View itemView) {
            super(itemView);
            locationText = (TextView) itemView.findViewById(R.id.location_text);
            timeText= (TextView) itemView.findViewById(R.id.location_time_text);
        }

        public void setData(LocationHandler location) {
            locationText.setText(location.getName() + " : [" + location.getLatitude() + "," + location.getLongitude() + "]");
            Date date=new Date(location.getTime());
            String formatDate= DateFormat.format("HH:mm:ss a on yyyy/MM/dd",date).toString();
            timeText.setText(formatDate);
        }
    }

    private class LocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<LocationHandler> list=new LocationDatabaseHelper(getActivity()).getLocations();
            Message message=mHandler.obtainMessage(UI_UPDATE);
            message.obj=list;
            message.sendToTarget();
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()==null) return;
        if(mFab!=null) {
            mFab.setOnClickListener(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity()==null) return;
        new LocationTask().execute();
        if(mFab!=null) {
            mFab.setImageResource(R.drawable.ic_add);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(getActivity()), PLACES_REQUEST);
                    } catch (GooglePlayServicesNotAvailableException|GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(),data);
                LocationHandler handler=new LocationHandler(place.getName().toString(),place.getLatLng().latitude
                        ,place.getLatLng().longitude,System.currentTimeMillis());
                new LocationDatabaseHelper(getActivity()).insertLocation(handler);
                ((ListAdapter)mRecyclerView.getAdapter()).addLocation(handler);
            }
        }
    }
}
