package com.example.srinivas.newmaps;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String EXTRA_PLACES = "places.extra";
    private LocationHandler mLocation;
    private ArrayList<LocationHandler> mLocationHandlers;
    private MapView mMapView;
    private TextView mDistanceTraveled;
    private FloatingActionButton mFab;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Receiver", "in receiver");
            if (!LocationResult.hasResult(intent)) {
                Log.i("Receiver", "no data " + intent.toString());
                return;
            }
            LocationResult result = LocationResult.extractResult(intent);
            Location location = result.getLastLocation();
            if (location != null) {
                mLocation = new LocationHandler(location.getLatitude(), location.getLongitude());
                mLocationHandlers = null;
                mMapView.getMapAsync(LocationMapFragment.this);
                Log.i("Receiver", "true");
            } else {
                Log.i("Receiver", "false");
            }
        }
    };

    private BroadcastReceiver placesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Location map p rec", "received");
            mLocationHandlers = (ArrayList<LocationHandler>)
                    intent.getSerializableExtra(PlacesSearchService.EXTRA_PLACES);
            mMapView.getMapAsync(LocationMapFragment.this);
        }
    };


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_map, parent, false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mMapView.onCreate(savedInstanceState);
        mDistanceTraveled = (TextView) v.findViewById(R.id.text_distance_traveled);
        mDistanceTraveled.setText("Distance Traveled " + DistanceHelper.getDistanceTravled(getActivity()) + " meteres");
        setExtraPlaces();
        return v;
    }

    public void setExtraPlaces() {
        if(getArguments()==null|| getArguments().getSerializable(EXTRA_PLACES)==null) return;
        mLocationHandlers=(ArrayList<LocationHandler>) getArguments().getSerializable(EXTRA_PLACES);
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (getActivity() == null) return;
        setFab();
        if (DistanceHelper.getLocationPrevious(getActivity()).getLongitude() != 0 &&
                DistanceHelper.getLocationPrevious(getActivity()).getLatitude() != 0) {
            mLocation = DistanceHelper.getLocationPrevious(getActivity());
            mMapView.getMapAsync(LocationMapFragment.this);
        }
        getActivity().registerReceiver(receiver, new IntentFilter(FusedLocationReceiver.ACTION_FUSED_LOCATION));
        getActivity().registerReceiver(placesReceiver, new IntentFilter(PlacesSearchService.ACTION_PLACES));
    }

    public void setFab() {
        setFabDrawable(SettingsPref.isLocationStarted(getActivity()));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        manageLocationUpdates(!SettingsPref.isLocationStarted(getActivity()));
                        return null;
                    }
                }.execute();
            }
        });
    }


    public void setFabDrawable(boolean val) {
        if (val) {
            mFab.setImageResource(R.drawable.ic_pause_white);
        } else {
            mFab.setImageResource(R.drawable.ic_play_arrow_white);
        }
    }

    public void manageLocationUpdates(boolean shouldStart) {
        if (shouldStart) {
            startUpdates(getActivity());
        } else {
            stopUpdates(getActivity());
        }
    }

    public void handleLocationStarted(final boolean val) {
        SettingsPref.setLocationStarted(getActivity(), val);
        if (!val) {
            DistanceHelper.setLocationDistance(getActivity(), 0);
            DistanceHelper.setLocationPrevious(getActivity(), new LocationHandler(0.0, 0.0));
            mLocation=null;
        }
        getActivity().runOnUiThread(new Thread() {
            @Override
            public void run() {
                setFabDrawable(val);
                if(!val) {
                    mMapView.getMapAsync(LocationMapFragment.this);
                    mDistanceTraveled.setText("Distance Traveled 0 meteres");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (getActivity() == null) return;
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(placesReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mLocation == null) {
            googleMap.clear();
            return;
        }
        googleMap.clear();
        Log.i("LatLng",mLocation.getLatitude()+" "+mLocation.getLongitude());
        LatLng location = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        googleMap.addMarker(new MarkerOptions().position(location).title("Location Marker").snippet("Current location"));
        if (mLocationHandlers != null) {
            LatLng latLng;
            for (int i = 0; i < mLocationHandlers.size(); i++) {
                latLng = new LatLng(mLocationHandlers.get(i).getLatitude(), mLocationHandlers.get(i).getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Hotel")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .snippet(mLocationHandlers.get(i).getName()));
            }
        }
        mDistanceTraveled.setText("Distance Traveled " + DistanceHelper.getDistanceTravled(getActivity()) + " meteres");
    }


    public synchronized void startUpdates(Context context) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect();
        if (connectionResult.isSuccess()) startLocationUpdates(mGoogleApiClient, context);
        else {
            getActivity().runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"failed to start!try again",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private synchronized void startLocationUpdates(GoogleApiClient mGoogleApiClient, final Context context) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(20 * 1000);
        request.setSmallestDisplacement(100);
        Intent i = new Intent(FusedLocationReceiver.ACTION_FUSED_LOCATION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LocationStart", "No permission");
            return;
        }
        Log.i("LocationStart", "before pending result");
        final com.google.android.gms.common.api.PendingResult<Status> result = LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, request, pi);
        com.google.android.gms.common.api.Status status = result.await();
        if (status.isSuccess()) {
            handleLocationStarted(true);
            Log.i("LocationStart", "Start success");
        } else {
            getActivity().runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"failed to start!try again",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public synchronized void stopUpdates(final Context context) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect();
        if(!connectionResult.isSuccess()) {
            getActivity().runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"failed to stop!try again",Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        Intent i = new Intent(context, FusedLocationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        final com.google.android.gms.common.api.PendingResult<Status> result =
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, pi);
        com.google.android.gms.common.api.Status status = result.await();
        if (status.isSuccess()) {
            handleLocationStarted(false);
        } else {
            getActivity().runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"failed to stop!try again",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static LocationMapFragment newInstance(ArrayList<LocationHandler> handlers) {
        LocationMapFragment fragment=new LocationMapFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable(EXTRA_PLACES,handlers);
        fragment.setArguments(bundle);
        return fragment;
    }
}
