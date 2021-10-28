package com.ntok.chatmodule.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.utils.Lg;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Sonam on 22-05-2018.
 */

@SuppressLint("ValidFragment")
public class MapViewFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private Location myLocation;
    private Button btn_share;
    private shareLocationInterface shareLocationInterface;

    @SuppressLint("ValidFragment")
    public MapViewFragment(Location myLocation) {
        this.myLocation = myLocation;
    }

    public void setShareLocationInterface(MapViewFragment.shareLocationInterface shareLocationInterface) {
        this.shareLocationInterface = shareLocationInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        btn_share = rootView.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLocationInterface.shareLocation();
                getActivity().onBackPressed();
            }
        });

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(sydney));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setText("Enter Details");
            ((MainActivity)getActivity()).title.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).onlineStatus.setVisibility(View.GONE);
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }

    public interface shareLocationInterface {
        public void shareLocation();
    }
}
