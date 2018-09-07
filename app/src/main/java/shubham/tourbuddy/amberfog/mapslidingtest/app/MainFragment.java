/**
 * Copyright 2015-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shubham.tourbuddy.amberfog.mapslidingtest.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import shubham.tourbuddy.GPSTracker;
import shubham.tourbuddy.Place;
import shubham.tourbuddy.PlacesService;
import shubham.tourbuddy.R;
import shubham.tourbuddy.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener, HeaderAdapter.ItemClickListener {

    private static final String ARG_LOCATION = "arg.location";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;

    // private LockableListView mListView;
    public static LockableRecyclerView mListView;
    public static SlidingUpPanelLayout mSlidingUpPanelLayout;

    // ListView stuff
    //private View mTransparentHeaderView;
    //private View mSpaceView;
    public static View mTransparentView;
    public static View mWhiteSpaceView;
    public static ArrayList<Place> ans = new ArrayList<Place>(100);
    public static HeaderAdapter mHeaderAdapter;
    public static LatLng mLocation;
    public static Marker mLocationMarker;
    public static LocationRequest mLocationRequest;
    public static SupportMapFragment mMapFragment;
    public static GPSTracker gps;
    public static GoogleMap mMap;
    public Boolean mIsNeedLocationUpdate = true;
    public static LatLng update;
    public static CameraPosition cp;
    public static GoogleApiClient mGoogleApiClient;
    private final String TAG = getClass().getSimpleName();
    public static Double lat, lon;


    public MainFragment() {

    }

    public static MainFragment newInstance(LatLng location) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mListView = (LockableRecyclerView) rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = rootView.findViewById(R.id.transparentView);
        mWhiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);

        // init header view for ListView
        // mTransparentHeaderView = inflater.inflate(R.layout.transparent_header_view, mListView, false);
        // mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        collapseMap();

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (Maps.mode==2) {

            lat = Maps.lat;
            lon = Maps.lon;

            }
            if(Maps.mode==1)
            {Log.e("mode",""+Maps.mode);
                gps=new GPSTracker(getContext());
                if(gps.canGetLocation())
                {
                    lat=gps.getLatitude();
                    lon=gps.getLongitude();
                    Log.e("Latlon",""+lat+" "+lon);
                }
                else
                {
                    gps.showSettingsAlert();
                }


            }



//System.out.println(gps.getLatitude()+"lat"+gps.getLongitude()+"lon");
        mMapFragment = SupportMapFragment.newInstance();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.addToBackStack(null).commit();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();

        mHeaderAdapter = new HeaderAdapter(getActivity(), ans, this);
        mListView.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mHeaderAdapter);
ans.clear();
        // show white bg if there are not too many items
        // mWhiteSpaceView.setVisibility(View.VISIBLE);

        // ListView approach-
        /*mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();
            }
        });*/


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    mMap = googleMap;
                    if (mMap != null) {

                        mMap.getUiSettings().setCompassEnabled(false);
                        mMap.getUiSettings().setZoomControlsEnabled(false);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
                        LatLng update = new LatLng(lat,lon);
                        //   mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_48)).position(update).anchor(.5f,.5f));
                        if (update != null) {
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 14.0f)));
                        }

                        new GetPlaces(getActivity(), Maps.string).execute();

                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {

                                mIsNeedLocationUpdate = false;
                                moveToLocation(latLng, false);
                            }
                        });
                    }
                }
            });
            // Check if we were successful in obtaining the map.

        }
    }

    public class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;


        public GetPlaces(Context context, String string) {
            this.context = context;
            places = string;

        }

        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(
                                new LatLng(result.get(i).getLatitude(), result
                                        .get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_12))
                );
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                    // Mountain View
                    .zoom(15) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    "AIzaSyAMbgCVJPkcQ4Mq896jFJhdKaxarGIM7CE");
            ArrayList<Place> findPlaces = service.findPlaces(lat,lon, places); // 77.218276
            System.out.println(places + "places");
            ans.clear();
            for (int i = 0; i < findPlaces.size(); i++) {

                Place placeDetail = findPlaces.get(i);

                ans.add(placeDetail);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // In case Google Play services has since become available.
mMapFragment.onResume();
      //  setUpMapIfNeeded();
    //    if(cp!=null)
      //      mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
      cp=null;

    }


    @Override
    public void onPause() {
        super.onPause();
        mMapFragment.onPause();
        cp=mMap.getCameraPosition();
        mMap=null;

    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
mGoogleApiClient.disconnect();
        super.onStop();


    }

 /*  private LatLng getLastKnownLocation()
    {
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            Log.e("loc",""+loc.getLatitude()+" "+loc.getLongitude());
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }*/

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        //   mLocationMarker = mMap.addMarker(new MarkerOptions()
        //         .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_48))
        //       .position(latLng).anchor(0.5f, 0.5f));
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && moveCamera) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 14.0f)));
                }
            }
        });
    }

    private void collapseMap() {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.showSpace();
        }
        mTransparentView.setVisibility(View.GONE);
        if (mMap != null && mLocation != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 14f), 1000, null);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.hideSpace();
        }
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //   ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
              // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                      int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= 23) { // Marshmallow

                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }

            } else { // no need to ask for permission

                // start to find location...
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start to find location...

            } else { // if permission is not granted
                Toast.makeText(getActivity(),"Permission denied", Toast.LENGTH_SHORT).show();
                // decide what you want to do if you don't get permissions
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClicked(int position) {
        mSlidingUpPanelLayout.collapsePane();
    }
}
