package com.example.visualpost_it.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.adapters.PlacesRecyclerAdapter;
import com.example.visualpost_it.adapters.UserRecyclerAdapter;
import com.example.visualpost_it.util.NearbyPlace;
import com.example.visualpost_it.dtos.Place;
import com.example.visualpost_it.util.PlaceComparator;
import com.example.visualpost_it.dtos.User;
import com.example.visualpost_it.dtos.UserLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;
import static com.example.visualpost_it.util.Constants.MAPVIEW_BUNDLE_KEY;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";

    private static final String ARG_PARAM1 = "nickname";
    private static final String ARG_PARAM2 = "email";
    private static final String ARG_PARAM3 = "fullname";
    private static final String ARG_PARAM4 = "password";
    private static final String ARG_PARAM5 = "userLocation";

    //widgets
    private RecyclerView mHomeFragmentRecyclerView;
    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    GoogleMap mMap;
    private PlacesClient placesClient;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback; //for updating user req if lastknownLocation is null

    private View mapView;
    private Context mContext;
    GoogleMap googleMap;

    //vars
    private ArrayList<User> mUserList = new ArrayList<>();
    private UserRecyclerAdapter mUserRecyclerAdapter;
    private ArrayList<Place> placesList = new ArrayList<>();

    public static HomeFragment newInstance(UserLocation userLocation) {
        HomeFragment fragment = new HomeFragment();
//       Bundle args = new Bundle();
//       args.putParcelable(ARG_PARAM5, userLocation);
//       args.putString(ARG_PARAM1, param1);
//       args.putString(ARG_PARAM2, param2);
//       args.putString(ARG_PARAM3, param3);
//       args.putString(ARG_PARAM4, param4);
//       Log.d(TAG, "newInstance: " +  args);
//       fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initializeMap();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: error initializing map");
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        Places.initialize(mContext, "AIzaSyCAZXOFxlgwHMn4U63Phzey1SIZzR2PVoQ");
        placesClient = Places.createClient(mContext);
    }

    private void initializeMap() {
        if (googleMap == null ) {
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //spinner
        Spinner mSpinner = view.findViewById(R.id.places_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.types_of_places, R.layout.layout_spinner);
        adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
                String selectedItem = parent.getItemAtPosition(position).toString();

                switch(selectedItem){
                    case "Museums":
                        findPlaces("museum", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        Log.d(TAG, "onItemSelected: Showing nearby Museums");
                        break;

                    case "Castles":
                        findPlaces("castle", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        Log.d(TAG, "onItemSelected: Showing nearby castles");
                        break;

                    case "Restaurants":
                        findPlaces("restaurant", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        Log.d(TAG, "onItemSelected: Showing nearby restaurants");
                        break;

                    case "Parks":
                        findPlaces("park", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        Log.d(TAG, "onItemSelected: Showing nearby parks");
                        break;

                    case "Gas stations":
                        findPlaces("gas", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        Log.d(TAG, "onItemSelected: Showing nearby gas stations");
                        break;

                    default:
                        placesList.clear();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mHomeFragmentRecyclerView = view.findViewById(R.id.nearby_places_recycler_view);
        mMapView = (MapView) view.findViewById(R.id.nearby_places_map);
        mMapView.getMapAsync(this);

        initGoogleMap(savedInstanceState);

        return view;
    }

    private void findPlaces(String type, double latitude, double longitude) {
        mMap.clear();
        placesList.clear();
        String url = getUrl(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), type);
        String lastKnownLatitude = String.valueOf(latitude);
        String lastKnownLongitude = String.valueOf(longitude);

        NearbyPlace nearbyPlace = (NearbyPlace) new NearbyPlace(new NearbyPlace.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<Place> mPlacesList) {
                placesList = mPlacesList;
                for(Place p : placesList){
                    Log.d(TAG, "processFinish: " + p.toString());
                }
                addCustomMarkers(placesList);

                Collections.sort(placesList, new PlaceComparator());
                initPlacesListRecyclerView();
            }
        }).execute(url, lastKnownLatitude, lastKnownLongitude, type);
    }

    private void addCustomMarkers(ArrayList<Place> placesList) {
        MarkerOptions markerOptions = new MarkerOptions();

        for(Place p: placesList){
            markerOptions.position(p.getLatLng());
            markerOptions.title(p.getPlaceName());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
        }

        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private String getUrl(double latitude, double longitude, String nearbyPlaceType){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googlePlaceUrl.append("&query=").append(nearbyPlaceType);
        googlePlaceUrl.append("&location=").append(latitude).append(",").append(longitude);
        googlePlaceUrl.append("&radius="+10000);
        googlePlaceUrl.append("&language="+"ro");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyCAZXOFxlgwHMn4U63Phzey1SIZzR2PVoQ");

        return googlePlaceUrl.toString();
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void initPlacesListRecyclerView() {
        for(Place p : placesList) {
            Log.d(TAG, "initPlacesListRecyclerView: " + p.toString());
        }

        PlacesRecyclerAdapter mPlacesRecyclerAdapter = new PlacesRecyclerAdapter(placesList, mMap);

        mHomeFragmentRecyclerView.setAdapter(mPlacesRecyclerAdapter);
        mHomeFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        initializeMap();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setExpirationDuration(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener((Activity) mContext, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "onSuccess: task get device location");
                getDeviceLocation();
            }
        });

        task.addOnFailureListener((Activity) mContext, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException){
                    Log.d(TAG, "onFailure: ");
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        Log.d(TAG, "onFailure: try");
                        resolvableApiException.startResolutionForResult((Activity) mContext, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 51){
            if(resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.d(TAG, "onComplete: getDeviceLocation: ");
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                            } else {
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult == null){
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(mContext, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
