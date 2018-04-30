package cs160.dinestination;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.location.LostLocationEngine;


import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.commons.geojson.Feature;

import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;

import com.mapbox.services.commons.models.Position;
import com.mapbox.mapboxsdk.annotations.Marker;

//import com.mapbox.services.android.

import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;



import com.mapbox.mapboxsdk.geometry.LatLng;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;

import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Code used in requesting runtime permissions.
     */
    //private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    //private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    //private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    //String MY_PERMISSIONS_ACCESS_FINE_LOCATION = "Please enable locationing!";

    // UI elements
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior previewSheetBehavior;
    LinearLayout layoutPreviewBottomSheet;
    Button testButton;
    SeekBar mSeekBar;
    Switch mSwitch;
    TextView priceRangeFromSeekBar;
    ImageView layoverRectangle;
    ImageView checkButton;
    ImageView backButton;
    ImageView marker;
    Location lastLocation;

    // Mapbox-related elements
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";

    private MapboxMap mapboxMap;

    private MapView mapView;
    private boolean getTempRouteCalled;
    private boolean getTempRoute2Called;

    //private LocationEngine locationEngine;

    //route-related
    private Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    private com.mapbox.geojson.Point originPosition;
    private com.mapbox.geojson.Point destinationPosition;
    private DirectionsRoute currentRoute;
    private DirectionsRoute analyzeRoute;
    private Double curDistance;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private  YelpFusionApi yelpFusionApi;
    private ArrayList<Business> business_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg");

        lastLocation = new Location("");
        lastLocation.setLatitude(37.866528);
        lastLocation.setLongitude(-122.258722);

        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        //mapView.setAccessToken(MAPBOX_ACCESS_TOKEN);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        //mapView.set(14);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        getApplicationContext().setTheme(R.style.AppTheme);

        // hook up UI elements
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        layoutPreviewBottomSheet = findViewById(R.id.preview_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        previewSheetBehavior = BottomSheetBehavior.from(layoutPreviewBottomSheet);
        testButton = findViewById(R.id.test_button);
        mSeekBar = findViewById(R.id.seekBar);
        mSwitch = findViewById(R.id.switch1);
        priceRangeFromSeekBar = findViewById(R.id.price_range_from_seek_bar);
        layoverRectangle = findViewById(R.id.layover_rectangle);
        checkButton = findViewById(R.id.check_button);
        backButton = findViewById(R.id.back_button);
        // marker = findViewById(R.id.marker);

        mSeekBar.setZ(999);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(30);
        mSwitch.setZ(999);

        // set price range listener and update price range from seek bar
        setPriceRangeListener();

        // set up bottom sheet
        setBottomSheetCallback();
        setPreviewBottomSheetCallback();
        setOnClickForTestButton();

        setOnClickForFilterTrigger(checkButton);
        setOnClickForFilterTrigger(backButton);

        layoverRectangle.setImageAlpha(0);
        layoverRectangle.setZ(4);

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        yelpFusionApi = null;
        try {
            yelpFusionApi = apiFactory.createAPI("dczs4nuyUTOJWGPaXth8Zqt0IwzGoD0Wr-8OZgDmdu4G0oa3M3K-GzlPVYFAh4indjgmImwbDSSaWnh2d7KQgSFly0AresZM9PGy6p4IRUgJcE3ElHJyWyXIb7jeWnYx");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d("yelp initialization", "please work");
//        Map<String, String> params = new HashMap<>();
//
//        params.put("term", "Boba");
//        params.put("latitude", "37.86701009652528");
//        params.put("longitude", "-122.2539898316383");
//
//        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
//
//
//        Log.d("yelp initialization", "please work 2");
//
//
//        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
//            @Override
//            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
//                SearchResponse searchResponse = response.body();
//                Log.d("HEREHEREHERE", String.valueOf(searchResponse.getBusinesses()));
//                // Update UI text with the searchResponse.
//                business_list = searchResponse.getBusinesses();
//                //Log.d("Business Name is", business_list.get(0).getName());
//                for (Business business : business_list) {
//                    Log.d("Business Name is", business.getName());
//                    Log.d("Business Rating is", Double.toString(business.getRating()));
//                    Log.d("Business dist is", Double.toString(business.getDistance()));
//                    //Log.d("Wait time is X minutes", Double.toString(5*(business.getRating() - business.getDistance()/10000)));
//                    //Rating - Distance (lat, long vs origin + lat, long vs destination)
//                    //Log.d("Business wait time", Double.toString(getWaitTime(37.869599999, -122.25878, 37.86442, -122.24920, business)));//sproul to clark kerr
//                }
//
//            }
//            @Override
//            public void onFailure(Call<SearchResponse> call, Throwable t) {
//                // HTTP error happened, do something to handle it.
//            }
//        };
//
//        call.enqueue(callback);
//        Log.d("yelp initialization", "please work 3");


    }
    private Double getWaitTime(Double latO, Double lonO, Double latD, Double lonD) { //Business bus) {
        com.mapbox.geojson.Point destinationP = com.mapbox.geojson.Point.fromLngLat(latD, lonD);
        com.mapbox.geojson.Point originP = com.mapbox.geojson.Point.fromLngLat(latO, lonO);
        //com.mapbox.geojson.Point busP = com.mapbox.geojson.Point.fromLngLat(bus.getCoordinates().getLatitude(), bus.getCoordinates().getLongitude());
        Log.d("About to get route ", "starting");
        getRoute(originP, destinationP);
        Log.d("About to get route ", "finished");


        double distance = curDistance;

        Double RestType = 0.3; // boba
        Double TimeOfDay = 1.2; // say its 3pm


        return distance/84;///84 + bus.getRating()*5*TimeOfDay*RestType;// + bus.getRating()) * TimeOfDay * RestType;

    }




    public static double getWaitDistance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        //double height = el1 - el2;

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }



//set the user's viewpoint as specified in the cameraPosition object




    private void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;

                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        curDistance = currentRoute.duration();
                        //Log.d("number of legs is " , currentRoute.legs().length);


                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            //MapboxNavigation navigation = new MapboxNavigation(mContext, MapboxConstants.MAP_TOKEN);
                            //navigationMapRoute = new NavigationMapRoute(navigation, mapView, mapboxMap);
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);

                        }

                        Toast.makeText(getApplicationContext(), Double.toString(currentRoute.distance()), Toast.LENGTH_SHORT).show();
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }
    private void getTempRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Doing temp route: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;

                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        analyzeRoute = response.body().routes().get(0);
                        curDistance = analyzeRoute.duration();
                        Log.d("Distance set to ", Double.toString(curDistance));
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "onTempRout error: " + throwable.getMessage());
                    }
                });
    }

    // on click for button to trigger filters
    private void setOnClickForTestButton() {
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    layoverRectangle.setImageAlpha(100);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    layoverRectangle.setImageAlpha(0);

                }
            }
        });
    }

    // temporary set on clicks for back and check button triggers
    private void setOnClickForFilterTrigger(ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    layoverRectangle.setImageAlpha(100);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    layoverRectangle.setImageAlpha(0);

                }
            }
        });
    }

    // trigger filter bottom sheet expansion
    private void setBottomSheetCallback() {
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        testButton.setText("Close");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        testButton.setText("Expand");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    // trigger preview bottom sheet expansion
    private void setPreviewBottomSheetCallback() {
        previewSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View previewBottomSheet, float slideOffset) {

            }
        });
    }

    // update price range from seek bar
    private void setPriceRangeListener() {

        Integer progUpper = 10*Math.round(mSeekBar.getProgress()/10);
        Integer progLower;
        if (progUpper < 10) {
            progLower = 1;
        } else {
            progLower = progUpper - 10;
        }
        String upperStr = progUpper.toString();
        String lowerStr = progLower.toString();
        priceRangeFromSeekBar.setText("$".concat(lowerStr).concat("-").concat(upperStr));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                Integer progUpper = 10*Math.round(mSeekBar.getProgress()/10);
                Integer progLower;
                if (progUpper < 10) {
                    progUpper = 10;
                    progLower = 1;
                } else {
                    progLower = progUpper - 10;
                }
                String upperStr = progUpper.toString();
                String lowerStr = progLower.toString();
                priceRangeFromSeekBar.setText("$".concat(lowerStr).concat("-").concat(upperStr));

            }
        });

    }
    private void getTempRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        if (getTempRouteCalled == false) {
                            getTempRouteCalled = true;
                            Log.d(TAG, "Doing temp route: " + response.code());
                            if (response.body() == null) {
                                Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                return;

                            } else if (response.body().routes().size() < 1) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            analyzeRoute = response.body().routes().get(0);
                            curDistance = analyzeRoute.distance();
                            finalDistance += curDistance;
                            Log.d("Distance set to ", Double.toString(finalDistance));
                            getTempRoute2(com.mapbox.geojson.Point.fromLngLat(-122.2702069, 37.8706731), destinationPosition);
                        }   else {
                            return;
                        }

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "onTempRout error: " + throwable.getMessage());
                    }
                });
    }
    private void getTempRoute2(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        if (getTempRoute2Called == false) {
                            getTempRoute2Called = true;
                            Log.d(TAG, "Doing temp route: " + response.code());
                            if (response.body() == null) {
                                Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                return;

                            } else if (response.body().routes().size() < 1) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            analyzeRoute = response.body().routes().get(0);
                            curDistance = analyzeRoute.distance();
                            finalDistance += curDistance;
                            Log.d("Final distance is ", Double.toString(finalDistance));
                        } else {
                            return;
                        }

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "onTempRout error: " + throwable.getMessage());
                    }
                });
    }

    // mapbox overrides

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        MainActivity.this.mapboxMap = mapboxMap;
        /* Image: An image is loaded and added to the map. */
        Bitmap icon = BitmapFactory.decodeResource(
                MainActivity.this.getResources(), R.drawable.pinpoint, options);
        mapboxMap.addImage(MARKER_IMAGE, icon);







        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng point) {
                PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, "my.layer.id");
                if (!features.isEmpty()) {
                    Feature selectedFeature = features.get(0);
                    String title = selectedFeature.getStringProperty("title");
                    Toast.makeText(getApplicationContext(), "You selected " + title, Toast.LENGTH_SHORT).show();
                }
                System.out.println(point);
                destinationPosition = com.mapbox.geojson.Point.fromLngLat(point.getLongitude(), point.getLatitude());
                originPosition = com.mapbox.geojson.Point.fromLngLat(-122.258875, 37.865593);
                Log.d("Search for me", "About to do getRoute stuff");
                //Log.d("Business wait time", Double.toString(getWaitTime(37.869599999, -122.25878, 37.86442, -122.24920)));
                getRoute(originPosition, destinationPosition);


                //do new shit
//                Log.d("Search for me", "About to do yelp stuff");
//
//
//                Map<String, String> params = new HashMap<>();
//
//                params.put("term", "Boba");
//                params.put("latitude", "37.86701009652528");
//                params.put("longitude", "-122.2539898316383");
//
//                Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
//                //SearchResponse searchResponse = call.execute().body();
//
//
//                Callback<SearchResponse> callback = new Callback<SearchResponse>() {
//                    @Override
//                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
//                        SearchResponse searchResponse = response.body();
//                        Log.d("HEREHEREHERE", String.valueOf(searchResponse.getBusinesses()));
//                        // Update UI text with the searchResponse.
//                        business_list = searchResponse.getBusinesses();
//                        //Log.d("Business Name is", business_list.get(0).getName());
//                        for (Business business : business_list) {
//                            Log.d("Business Name is", business.getName());
//                            Log.d("Business Rating is", Double.toString(business.getRating()));
//                            Log.d("Business dist is", Double.toString(business.getDistance()));
//                            //Log.d("Wait time is X minutes", Double.toString(5*(business.getRating() - business.getDistance()/10000)));
//                            //Rating - Distance (lat, long vs origin + lat, long vs destination)
//                            //Log.d("Business wait time", Double.toString(getWaitTime(37.869599999, -122.25878, 37.86442, -122.24920, business)));//sproul to clark kerr
//                        }
//
//                    }
//                    @Override
//                    public void onFailure(Call<SearchResponse> call, Throwable t) {
//                        // HTTP error happened, do something to handle it.
//                    }
//                };
//
//                call.enqueue(callback);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        mapView.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();


        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }




}
