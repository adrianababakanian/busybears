package cs160.dinestination;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.api.directions.v5.models.StepIntersection;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.widgets.MyLocationViewSettings;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.location.LocationEngineProvider;
import com.mapbox.services.android.telemetry.location.LostLocationEngine;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

// import com.google.android.gms.maps.model.LatLngBounds;

// import com.google.android.gms.maps.model.LatLngBounds;
// import com.mapbox.mapboxsdk.geometry.LatLng;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    String priceRange;

    // UI elements.
    BottomSheetBehavior previewSheetBehavior;
    LinearLayout layoutPreviewBottomSheet;
    BottomSheetBehavior filtersSheetBehavior;
    LinearLayout filtersBottomSheet;
    RangeBar mSeekBar;
    boolean priceSliderUsedFlag;
    Switch mSwitch1;
    Switch mSwitch2;
    TextView priceRangeFromSeekBar;
    ImageView layoverRectangle;
    ImageView filtersCheckButton;
    ImageView filtersBackButton;

    BottomSheetBehavior timeSpinnerSheetBehavior;
    LinearLayout timeSpinnerBottomSheet;
    TimePicker timePicker;
    ImageView whereToRectangle;
    TextView whereToPlace;
    TextView whereToTime;
    ConstraintLayout topInputElement;
    ImageView closeTopInputElement;
    ImageView addTopInputElement;
    CoordinatorLayout rootLayout;
    ViewFlipper whereToInputViewFlipper;
    ConstraintLayout appliedFiltersLayout;
    Button addFiltersButton;
    AutoCompleteTextView whereToEditText;
    ImageButton addMoreFiltersButton;
    Button exitInputButton;
    LinearLayout appliedFiltersWrapper;
    ConstraintLayout mainTopInputElement;
    Button findRestaurantsButton;
    LinearLayout filtersRowTopBar;

    SeekBar toleranceSlider;

    // Map-related.
    RelativeLayout navigationRowWrapper;
    ImageButton navigationWalkButton;
    ImageButton navigationCarButton;
    ImageButton navigationBikeButton;
    ImageButton navigationTaxiButton;
    RelativeLayout whereToElement;
    RelativeLayout destinationInformation;

    // Mapbox items.
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private MapboxMap mapboxMap;
    private MapView mapView;
    String mapboxAccessToken = "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg";

    // Route-related.
    private com.mapbox.geojson.Point originPosition = com.mapbox.geojson.Point.fromLngLat(-122.257290, 37.867460);
    private com.mapbox.geojson.Point destinationPosition = com.mapbox.geojson.Point.fromLngLat(-122.257290, 37.867460);

    private double southPoint = 0.0;
    private double westPoint = 0.0;
    private double northPoint = 0.0;
    private double eastPoint = 0.0;

    private double originLon = 0.0;
    private double originLat = 0.0;
    private double destLat = 0.0;
    private double destLon = 0.0;

    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private Boolean ADDED_MARKERS = Boolean.FALSE;
    private String PROFILE_TYPE;

    private GoogleApiClient mGoogleApiClient;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private static final com.google.android.gms.maps.model.LatLngBounds BOUNDS_GREATER_BAY_AREA = new com.google.android.gms.maps.model.LatLngBounds(
            new com.google.android.gms.maps.model.LatLng(37.7749, -122.4194), new com.google.android.gms.maps.model.LatLng(37.9101, -122.0652));

    // Location layer-related.
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation = new Location("");
    // private Location originLocation = new Location(-122.257290, 37.867460);

    // Yelp API v3
    YelpFusionApiFactory yelpApiFactory;
    YelpFusionApi yelpFusionApi;
    ArrayList<String> currentCuisineFilters;
    Icon pinpointIcon;
    IconFactory iconFactory;

    //Details Activity
    Double selectedPlaceLat;
    Double selectedPlaceLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, mapboxAccessToken);

        setContentView(R.layout.activity_main);

        // hook up UI elements
        layoutPreviewBottomSheet = findViewById(R.id.preview_bottom_sheet);
        previewSheetBehavior = BottomSheetBehavior.from(layoutPreviewBottomSheet);

        filtersBottomSheet = findViewById(R.id.filters_bottom_sheet);
        filtersSheetBehavior = BottomSheetBehavior.from(filtersBottomSheet);

        mSeekBar = findViewById(R.id.seekBar);
        mSwitch1 = findViewById(R.id.switch1);
        mSwitch2 = findViewById(R.id.switch2);
        priceRangeFromSeekBar = findViewById(R.id.price_range_from_seek_bar);
        priceSliderUsedFlag = false;
        layoverRectangle = findViewById(R.id.layover_rectangle);
        filtersCheckButton = findViewById(R.id.filters_check_button);
        filtersBackButton = findViewById(R.id.filters_back_button);

        timeSpinnerBottomSheet = findViewById(R.id.time_spinner_bottom_sheet);
        timeSpinnerSheetBehavior = BottomSheetBehavior.from(timeSpinnerBottomSheet);
        timePicker = findViewById(R.id.time_picker);
        whereToRectangle = findViewById(R.id.where_to_rectangle);
        whereToElement = findViewById(R.id.where_to_element);
        destinationInformation = findViewById(R.id.destination_information);
        whereToPlace = findViewById(R.id.where_to_place_sub);

        whereToPlace.setSelectAllOnFocus(Boolean.TRUE);

        whereToTime = findViewById(R.id.where_to_time_sub);
        topInputElement = findViewById(R.id.top_input_element);
        whereToEditText = (AutoCompleteTextView) findViewById(R.id.destination_top_input_elem);
        closeTopInputElement = findViewById(R.id.close_top_input_elem);
        addTopInputElement = findViewById(R.id.add_top_input_elem);
        rootLayout = findViewById(R.id.mainRootView);
        whereToInputViewFlipper = findViewById(R.id.viewFlipper1);
        whereToInputViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        whereToInputViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        appliedFiltersLayout = findViewById(R.id.applied_filters_top_input_elem);
        addFiltersButton = findViewById(R.id.add_filters_top_input_elem);
        exitInputButton = findViewById(R.id.exit_input_button);
        appliedFiltersWrapper = findViewById(R.id.applied_filters_wrapper);
        mainTopInputElement = findViewById(R.id.main_top_input_element);
        findRestaurantsButton = findViewById(R.id.find_restaurants_button);
        filtersRowTopBar = findViewById(R.id.filter_row_top_bar);
        toleranceSlider = findViewById(R.id.tolerance_slider);

        navigationRowWrapper = findViewById(R.id.navigation_row_wrapper);
        navigationWalkButton = findViewById(R.id.navigation_walk_button);
        navigationCarButton = findViewById(R.id.navigation_car_button);
        navigationBikeButton = findViewById(R.id.navigation_bike_button);
        navigationTaxiButton = findViewById(R.id.navigation_taxi_button);

        // Make icons for pinpoint
        iconFactory = IconFactory.getInstance(MainActivity.this);
        pinpointIcon = iconFactory.fromResource(R.drawable.smaller_pinpoint);

        ImageView empty = findViewById(R.id.empty);
        // empty.setZ(1000);

        empty.setClickable(Boolean.FALSE);

        empty.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }

        });

        // Set z-indices.
        navigationBikeButton.setZ(999);
        topInputElement.setZ(1000);
        whereToInputViewFlipper.setZ(999);
        timeSpinnerBottomSheet.setZ(999);
        filtersBottomSheet.setZ(999);
        timeSpinnerBottomSheet.setZ(2);
        layoutPreviewBottomSheet.setZ(999);
        exitInputButton.setZ(1000);
        findRestaurantsButton.setZ(999);

        setTimeSpinnerSheetBehavior();
        setFiltersSheetBehavior();

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

        whereToRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeSpinnerSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    findRestaurantsButton.setVisibility(View.GONE);
                    navigationRowWrapper.setVisibility(View.GONE);
                    // whereToElementReposition(false);
                    whereToInputViewFlipper.showNext();
                } else {
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        destinationInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeSpinnerSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    findRestaurantsButton.setVisibility(View.GONE);
                    navigationRowWrapper.setVisibility(View.GONE);
                    whereToElement.setVisibility(View.GONE);
                    whereToInputViewFlipper.showNext();
                } else {
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        closeTopInputElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // force close softkeyboard, else pushes layout up
                timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                whereToInputViewFlipper.showPrevious();
                if (!whereToPlace.getText().equals(""))
                whereToElement.setVisibility(View.GONE);
                // addMarkers();
            }
        });
        layoutPreviewBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                goToDetailsIntent.putExtra("placeLat", selectedPlaceLat);
                goToDetailsIntent.putExtra("placeLong", selectedPlaceLong);
                startActivity(goToDetailsIntent);
            }
        });

        addFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap map) {
                mapboxMap = map;
                enableLocationPlugin();
            }

            ;
        });

        getApplicationContext().setTheme(R.style.AppTheme);

        mSeekBar.setZ(999);
        mSeekBar.setRangePinsByValue(10, 50);
        mSwitch1.setZ(999);

        // set price range listener and update price range from seek bar
        setPriceRangeListener();

        // set up bottom sheet
        setPreviewBottomSheetCallback();

        setOnClickForExitInputButton();
        setOnClickForFilterTrigger(filtersCheckButton);
        setOnClickForFilterBack(filtersBackButton);
        setOnClickForFindRestaurants(findRestaurantsButton);

        layoverRectangle.setImageAlpha(0);
        layoverRectangle.setZ(4);

        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.activate();
        addLocationEngineListener();

        setupUI(findViewById(R.id.mainRootView));

        // Google autocomplete API client.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_BAY_AREA,null);

        whereToEditText.setAdapter(mPlaceAutocompleteAdapter);

        exitInputButton.setEnabled(false);
        exitInputButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
        whereToEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.toString().trim().length()==0) {
                    exitInputButton.setEnabled(false);
                    exitInputButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
                } else {
                    exitInputButton.setEnabled(true);
                    exitInputButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set up navigation buttons.
        setOnClickForNavigationButtons(navigationWalkButton, navigationCarButton, navigationBikeButton, navigationTaxiButton);
//        navigationCarButton.callOnClick(); // to set walk as the default routing option.


        //-----------------------Yelp-----------------------------------------------
        yelpApiFactory = new YelpFusionApiFactory();
        yelpFusionApi = null;
        try {
            yelpFusionApi = yelpApiFactory.createAPI("x4HzIK9Yg9t9HzBDOVrmPwydPeNPqV3fTJL6pLVj4XBSQ7cEVNuP9G9qqhMOxM_wxlxdq7JQfz-ZJQ6Q8DzbeCDUdA5F7I1uGRrTyFItQQmariY0BYlx7dxPKpXnWnYx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentCuisineFilters = new ArrayList<>();
//        yelpQueryMaker(destinationPosition.latitude(), destinationPosition.longitude());
        //--------------------------------------------------------------------------



        filtersRowGenerator();

        setOnKeyListenerForWhereToPlace();

    } // END THE ON CREATE METHOD

    private void setTimeSpinnerSheetBehavior() {
        timeSpinnerSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: { mapView.setVisibility(View.INVISIBLE); }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: { }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
//                        whereToInputViewFlipper.showNext(); // allows dragging instead, but if they drag down and then up again, top input disappears but this remains.
                        break;
                    case BottomSheetBehavior.STATE_SETTLING: { mapView.setVisibility(View.VISIBLE); }
                    // the movement phase between expanded and collapsed.
                    break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void setFiltersSheetBehavior() {
        filtersSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: { mapView.setVisibility(View.INVISIBLE); }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: { }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
//                        whereToInputViewFlipper.showNext(); // allows dragging instead, but if they drag down and then up again, top input disappears but this remains.
                        break;
                    case BottomSheetBehavior.STATE_SETTLING: { mapView.setVisibility(View.VISIBLE); }
                    // the movement phase between expanded and collapsed.
                    break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }


    // Gecode and address from the places API.
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = whereToEditText.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<android.location.Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            destLat = address.getLatitude();
            destLon = address.getLongitude();
            destinationPosition = com.mapbox.geojson.Point.fromLngLat(address.getLongitude(), address.getLatitude());
            System.out.println(destinationPosition);

        }

    }


    // Get and draw the route given an origin, destination position, and a profile.
    private void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        System.out.println("GET ROUTE CALLED");
        NavigationRoute.builder()
                .accessToken(mapboxAccessToken)
                .origin(origin)
                .destination(destination)
                .profile(PROFILE_TYPE)
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

                        System.out.println(currentRoute.legs());

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            //MapboxNavigation navigation = new MapboxNavigation(mContext, MapboxConstants.MAP_TOKEN);
                            //navigationMapRoute = new NavigationMapRoute(navigation, mapView, mapboxMap);
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }

//                        Toast.makeText(getApplicationContext(), Double.toString(currentRoute.distance()), Toast.LENGTH_SHORT).show();
                        navigationMapRoute.addRoute(currentRoute);

//                        for (RouteLeg rl : currentRoute.legs()) {
//                            Log.d("the other route", rl.summary().toString()); // shows same thing as currentRoute
//                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    /**
     * Generates buttons to be displayed in the filters scroll row.
     */
    private void filtersRowGenerator() {
        ArrayList<String> stringsForFilterButtons = new ArrayList<>();
        int[] cuisine_ids = new int[] {R.id.thai_check, R.id.italian_check, R.id.chinese_check, R.id.mexican_check,
                R.id.indian_check, R.id.american_check, R.id.japanese_check, R.id.burmese_check};
        int[] attire_ids = new int[] {R.id.casual_check, R.id.relaxed_check, R.id.dressy_check, R.id.formal_check};

        currentCuisineFilters.clear();
        for (int i = 0; i < cuisine_ids.length; i++) {
            CheckBox cBox = findViewById(cuisine_ids[i]);
            if (cBox.isChecked()) {
                stringsForFilterButtons.add(cBox.getText().toString());
                switch (cuisine_ids[i]) {
                    case R.id.indian_check:
                        currentCuisineFilters.add("indpak");
                        break;
                    case R.id.american_check:
                        currentCuisineFilters.add("newamerican,tradamerican");
                        break;
                    default:
                        currentCuisineFilters.add(cBox.getText().toString().toLowerCase());
                        break;
                }
            }
        }
        if (priceSliderUsedFlag) stringsForFilterButtons.add(priceRange);
        for (int i = 0; i < attire_ids.length; i++) {
            CheckBox cBox = findViewById(attire_ids[i]);
            if (cBox.isChecked()) stringsForFilterButtons.add(cBox.getText().toString());
        }

        if (mSwitch1.isChecked()) stringsForFilterButtons.add("Groups");
        if (mSwitch2.isChecked()) stringsForFilterButtons.add("Kids");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4,0,4,0);
        lp.height = 105;
        if (stringsForFilterButtons.size() != 0) { // if filters have been applied
            appliedFiltersWrapper.removeViewsInLayout(1, appliedFiltersWrapper.getChildCount()-1);
            filtersRowTopBar.removeAllViewsInLayout(); // since never want AddFilters button showing up.

            Button plusButton = filtersRowGeneratePlusButton();
            Button plusButton2 = filtersRowGeneratePlusButton();

            appliedFiltersWrapper.addView(plusButton);
            filtersRowTopBar.addView(plusButton2);

            addFiltersButton.setVisibility(View.GONE);
            layoverRectangle.setImageAlpha(0);

            for (String label : stringsForFilterButtons) {
                Button buttonToAdd = filtersRowGenerateButton(label);
                Button buttonToAdd2 = filtersRowGenerateButton(label);
                appliedFiltersWrapper.addView(buttonToAdd, lp);
                filtersRowTopBar.addView(buttonToAdd2, lp);
            }
            filtersRowTopBar.setVisibility(View.VISIBLE);

        } else { // if no filters applied
            appliedFiltersWrapper.removeViewsInLayout(1, appliedFiltersWrapper.getChildCount()-1);
            addFiltersButton.setVisibility(View.VISIBLE);
            layoverRectangle.setImageAlpha(100);

            filtersRowTopBar.removeAllViewsInLayout();
            Button addFiltz = filtersRowGenerateButton("Add filters");
            addFiltz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            filtersRowTopBar.addView(addFiltz, lp);
//            filtersRowTopBar.setVisibility(View.GONE);
        }
    }

    /**
     * Auxiliary functions for filtersRowGenerator().
     */
    private Button filtersRowGenerateButton(String label) {
        Button newButton = new Button(this);
        newButton.setText(label);
        newButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        newButton.setTextColor(getResources().getColor(R.color.white));
        newButton.setAllCaps(Boolean.FALSE);
        return newButton;
    }
    private Button filtersRowGeneratePlusButton() {
        Button newPlusButton = new Button(this);
        newPlusButton.setBackground(getResources().getDrawable(R.drawable.plus_button));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                100, 104);
        params.setMargins(4, 0, 4, 0);
        newPlusButton.setLayoutParams(params);
        newPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        if (timeSpinnerSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            findRestaurantsButton.setVisibility(View.VISIBLE);
        }
        return newPlusButton;
    }

    /**
     * onClick for the 'Search' button - sets whereTo, time, draws route, etc.
     */
    private void setOnClickForExitInputButton() {
        exitInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filtersRowTopBar.setVisibility(View.VISIBLE);

                navigationCarButton.callOnClick(); // to set walk as the default routing option.

                geoLocate();

                destinationInformation.setVisibility(View.VISIBLE);
                timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                whereToInputViewFlipper.showNext();
                whereToElement.setVisibility(View.GONE); // this flippiness. search button greyed out until destination input.
                // search without any input. then tap where to. then cross - then things are overlayed.
                String whereToText = whereToEditText.getText().toString();
                whereToPlace.setText(whereToText);
                String meridian = "am";
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String minuteStr = timePicker.getCurrentMinute().toString();
                if (hour > 12) {meridian = "pm"; hour = hour - 12;}
                if (minute < 10) {minuteStr = "0".concat(minuteStr);}
                String hourStr = Integer.toString(hour);
                whereToTime.setText("by "+hourStr+":"+minuteStr+meridian);
                whereToPlace.setTextColor(getResources().getColor(R.color.textColorDark));
                whereToTime.setTextColor(getResources().getColor(R.color.textColorDark));

                drawRoute();
                findRestaurantsButton.setVisibility(View.VISIBLE);
                navigationRowWrapper.setVisibility(View.VISIBLE);
                toleranceSlider.setVisibility(View.GONE);

            }
        });
    }

    // all the same things as the on click for the search button, to pass to the key listener
    private void doAllTheThings() {
        filtersRowTopBar.setVisibility(View.VISIBLE);

        navigationCarButton.callOnClick(); // to set walk as the default routing option.

        geoLocate();

        destinationInformation.setVisibility(View.VISIBLE);
        timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        whereToInputViewFlipper.showNext();
        whereToElement.setVisibility(View.GONE); // this flippiness. search button greyed out until destination input.
        // search without any input. then tap where to. then cross - then things are overlayed.
        String whereToText = whereToEditText.getText().toString();
        whereToPlace.setText(whereToText);
        String meridian = "am";
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        String minuteStr = timePicker.getCurrentMinute().toString();
        if (hour > 12) {meridian = "pm"; hour = hour - 12;}
        if (minute < 10) {minuteStr = "0".concat(minuteStr);}
        String hourStr = Integer.toString(hour);
        whereToTime.setText("by "+hourStr+":"+minuteStr+meridian);
        whereToPlace.setTextColor(getResources().getColor(R.color.textColorDark));
        whereToTime.setTextColor(getResources().getColor(R.color.textColorDark));

        drawRoute();
        findRestaurantsButton.setVisibility(View.VISIBLE);
        navigationRowWrapper.setVisibility(View.VISIBLE);
        toleranceSlider.setVisibility(View.GONE);

    }

    private void setOnKeyListenerForWhereToPlace() {
        whereToEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            doAllTheThings();
                            hideSoftKeyboard(MainActivity.this);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Set up the UI such that the soft keyboard is collapsed when clicked off of.
     */
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    return false;
                }
            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    /**
     * Hide the soft keyboard and preview element for each marker.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * onClick for filter preferences sheet check mark - applies selected filters.
     */
    private void setOnClickForFilterTrigger(ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    filtersRowGenerator();
                    mapboxMap.removeAnnotations();
                    if (findRestaurantsButton.getVisibility() == View.VISIBLE) {
                        yelpQueryMaker(destinationPosition.latitude(), destinationPosition.longitude());
                    }

                } else { // else case will never occur. Cannot click checkmark while collapsed.
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }

    /**
     * onClick for filter preference sheet cross mark - clears all filters.
     */
    private void setOnClickForFilterBack(ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                layoverRectangle.setImageAlpha(0);

                int[] cuisine_ids = new int[] {R.id.thai_check, R.id.italian_check, R.id.chinese_check, R.id.mexican_check,
                        R.id.indian_check, R.id.american_check, R.id.japanese_check, R.id.burmese_check};
                int[] attire_ids = new int[] {R.id.casual_check, R.id.relaxed_check, R.id.dressy_check, R.id.formal_check};

                for (int i = 0; i < cuisine_ids.length; i++) {
                    CheckBox cBox = findViewById(cuisine_ids[i]);
                    cBox.setChecked(false);
                }
                mSeekBar.setRangePinsByValue(10, 50);
                priceSliderUsedFlag = false;

                for (int i = 0; i < attire_ids.length; i++) {
                    CheckBox cBox = findViewById(attire_ids[i]);
                    cBox.setChecked(false);
                }
                mSwitch1.setChecked(false);
                mSwitch2.setChecked(false);
                filtersRowGenerator();
            }
        });
    }

    /**
     * Request the user's location before querying the location engine.
     */
    private void requestLocationWithCheck() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
            locationEngine.requestLocationUpdates();
            //lastLocation = locationEngine.getLastLocation();
        }
    }

    /**
     * Trigger preview bottom sheet expansion.
     */
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
//                        previewSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

//            @Override public boolean dispatchTouchEvent(MotionEvent event){
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (previewSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
//
//                        Rect outRect = new Rect();
//                        layoutPreviewBottomSheet.getGlobalVisibleRect(outRect);
//
//                        if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
//                            previewSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    }
//                }
//
//                return super.dispatchTouchEvent(event);
//            }

            @Override
            public void onSlide(@NonNull View previewBottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * Update price range from seek bar.
     */
    private void setPriceRangeListener() {
        String upperStr = mSeekBar.getRightPinValue();
        String lowerStr = mSeekBar.getLeftPinValue();
        if (upperStr.equals("100")) upperStr = "100+";
        priceRangeFromSeekBar.setText("$".concat(lowerStr).concat("-").concat(upperStr));
        priceRange = "$".concat(lowerStr).concat("-").concat(upperStr);

        mSeekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                priceSliderUsedFlag = true;
                String upperStr = rightPinValue;
                String lowerStr = leftPinValue;
                if (upperStr.equals("100")) upperStr = "100+";
                priceRangeFromSeekBar.setText("$".concat(lowerStr).concat("-").concat(upperStr));
                priceRange = "$".concat(lowerStr).concat("-").concat(upperStr);
            }
        });
    }

    /**
     * Converts price range values to string for Yelp API queries.
     */
    private String getYelpifiedPriceRange() {
        // eg. https://api.yelp.com/v3/businesses/search?term=restaurant&location=boulder&price=1,2,3
        // will return anything that matches $, $$ and $$$. Max 4. (with Bearer = API key)
        String priceRangeQueryStr = "";
        Integer upperVal = Integer.parseInt(mSeekBar.getRightPinValue());
        Integer lowerVal = Integer.parseInt(mSeekBar.getLeftPinValue());
        if (priceSliderUsedFlag) {
            if ((lowerVal <= 25) || (upperVal <= 25)) {
                priceRangeQueryStr += "1,";
            }
            if ((lowerVal > 25 && lowerVal <= 50) || (upperVal > 25 && lowerVal <= 50)) {
                priceRangeQueryStr += "2,";
            }
            if ((lowerVal > 50 && lowerVal <= 75) || (upperVal > 50 && lowerVal <= 75)) {
                priceRangeQueryStr += "3,";
            }
            if ((lowerVal > 75) || (upperVal > 75)) {
                priceRangeQueryStr += "4,";
            }
        }
        if (!priceRangeQueryStr.equals("")) {
            priceRangeQueryStr = priceRangeQueryStr.substring(0, priceRangeQueryStr.length() - 1);
        }
        return priceRangeQueryStr;
    }

    Business restaurant;
    HashMap names1 = new HashMap<String, String>();
    HashMap addresses1 = new HashMap<String, String>();
    HashMap pics1 = new HashMap<String, String>();
    private void yelpQueryMaker(Double latitude, Double longitude) {
        // docs: https://www.yelp.com/developers/documentation/v3/business_search
        // GOOD FOR KIDS, GOOD FOR GROUPS - NOT POSSIBLE USING API.
        // also no options for attire. - could match to price range instead..
        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("price", getYelpifiedPriceRange());
        params.put("radius", String.valueOf(toleranceSlider.getProgress() + 200)); // wait lol this only shows up after query has been made.
        // ^ Needs some conversion factor, not a static addition. should also account for wait time, in theory...
        params.put("open_now", "true");
        params.put("term", "restaurants");
        params.put("limit", "2"); // 20 by default

        String cuisineQueryString = "";
        for (String str : currentCuisineFilters) {
            cuisineQueryString += str + ",";
        }
        params.put("categories", cuisineQueryString); // no space. only comma. // burgers,brunch

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                final SearchResponse searchResponse = response.body();
                for (int x = 0; x < searchResponse.getBusinesses().size(); x++) {
                    restaurant = searchResponse.getBusinesses().get(x);
//                    Log.d("YelpQueryMaker", restaurant.getName() + "," + restaurant.getCoordinates().getLatitude());
                    LatLng ll = new LatLng(restaurant.getCoordinates().getLatitude(), restaurant.getCoordinates().getLongitude());
                    mapboxMap.addMarker(new MarkerViewOptions()
                            .position(ll)
                            .icon(pinpointIcon));
                    names1.put(String.valueOf(restaurant.getCoordinates().getLatitude()) +
                            String.valueOf(restaurant.getCoordinates().getLongitude()), restaurant.getName());
                    addresses1.put(String.valueOf(restaurant.getCoordinates().getLatitude()) +
                            String.valueOf(restaurant.getCoordinates().getLongitude()), restaurant.getLocation().getAddress1() + "\n" + restaurant.getLocation().getCity() + " " + restaurant.getLocation().getState() + " " + String.valueOf(restaurant.getLocation().getZipCode()));
                    pics1.put(String.valueOf(restaurant.getCoordinates().getLatitude()) +
                            String.valueOf(restaurant.getCoordinates().getLongitude()), restaurant.getImageUrl());


                    mapboxMap.getMarkerViewManager().setOnMarkerViewClickListener(new MapboxMap.OnMarkerViewClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {
                            Timber.e(marker.toString());
                            //setContentView(R.layout.preview_bottom_sheet);
                            TextView tv1 = (TextView)findViewById(R.id.NameOf);
                            String namess = String.valueOf(marker.getPosition().getLatitude()) +
                                    String.valueOf(marker.getPosition().getLongitude());
                            tv1.setText(String.valueOf(names1.get(namess)));

                            TextView tv2 = (TextView)findViewById(R.id.AddressOf);
                            String addressess = String.valueOf(marker.getPosition().getLatitude()) +
                                    String.valueOf(marker.getPosition().getLongitude());
                            tv2.setText(String.valueOf(addresses1.get(addressess)));

                            selectedPlaceLat = restaurant.getCoordinates().getLatitude();
                            selectedPlaceLong = restaurant.getCoordinates().getLongitude();

                            /*ImageView iv1 =(ImageView)findViewById(R.id.PictureOf);
                            String picss = String.valueOf(marker.getPosition().getLatitude()) +
                                    String.valueOf(marker.getPosition().getLongitude());
                            Log.d("HiHiHiHiHi", String.valueOf(pics1.get(picss)));
                            Picasso.with(getApplicationContext()).load(picss).into(iv1);*/

                            if (previewSheetBehavior.getState() != previewSheetBehavior.STATE_EXPANDED) {
                                previewSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
//                            else {
//                                previewSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                                previewSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//                            }
                            return false;
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                Log.d("YelpQueryMaker", "oh no, something went wrong");
            }
        };
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        call.enqueue(callback);
    }

    /**
     * onClick for 'Find restaurants' button - executes goToHeatmapActivity intent.
     */
    private void setOnClickForFindRestaurants(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: make markers show up on map now
                toleranceSlider.setVisibility(View.VISIBLE);
                navigationRowWrapper.setVisibility(View.GONE);
                findRestaurantsButton.setVisibility(View.GONE);
                yelpQueryMaker(destinationPosition.latitude(), destinationPosition.longitude());
                // TODO: This and the one in setOnClickForFiltersTrigger just use destination position!!
                ArrayList<StepIntersection> intersections = new ArrayList<>();
                for (RouteLeg leg : currentRoute.legs()) { // this is only giving the first step right now.
                    for (LegStep s : leg.steps()) {
                        for (StepIntersection l : s.intersections()) {
                            if (!intersections.contains(l)) intersections.add(l);
                        }
                    }
                }
                for (StepIntersection s : intersections) {
                    yelpQueryMaker(s.location().latitude(), s.location().longitude());
                }
            }
        });
    }

    /**
     * onClick listeners for nav option buttons - sets visuals for which is selected.
     * TODO: hook these clicks up to Mapbox routing!
     */
    private void setOnClickForNavigationButtons(final ImageButton vWalk, final ImageButton vCar,
                                                final ImageButton vBike, final ImageButton vTaxi) {
        System.out.println("iojoijoiomim");
        vWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                vWalk.setBackground(getResources().getDrawable(R.drawable.nav_button_pressed_backg)); // for rounded buttons
                vWalk.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp_pressed));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vBike.setBackground(getResources().getDrawable(R.color.white));
                vBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_bike_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
                PROFILE_TYPE = DirectionsCriteria.PROFILE_WALKING;
                findRestaurantsButton.setVisibility(View.VISIBLE);
                drawRoute();
            }
        });
        vCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp_pressed));
                vBike.setBackground(getResources().getDrawable(R.color.white));
                vBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_bike_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
                PROFILE_TYPE = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC;
                findRestaurantsButton.setVisibility(View.VISIBLE);
                drawRoute();
            }
        });
        vBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vBike.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_bike_24dp_pressed));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
                PROFILE_TYPE = DirectionsCriteria.PROFILE_CYCLING;
                findRestaurantsButton.setVisibility(View.VISIBLE);
                System.out.println("wow bicycle click");
                drawRoute();
            }
        });
        vTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new RideshareDialog();
                newFragment.setStyle(R.style.MainFont, R.style.AppDialogTheme);
                newFragment.show(getFragmentManager(), "hello");
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vBike.setBackground(getResources().getDrawable(R.color.white));
                vBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_bike_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi_pressed));
                PROFILE_TYPE = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC;
                findRestaurantsButton.setVisibility(View.VISIBLE);
                drawRoute();
            }
        });
    }


    //////// MAPBOX THINGS ////////
    /**
     * Mapbox overrides.
     */
    @Override
    public void onMapReady(final MapboxMap mapboxMap) {

        mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, "my.layer.id");
                if (!features.isEmpty()) {
                    Feature selectedFeature = features.get(0);
                    String title = selectedFeature.getStringProperty("title");
                    Toast.makeText(getApplicationContext(), "You selected " + title, Toast.LENGTH_SHORT).show();
                }
                System.out.println(point);

            }
        });
    }

    private void drawRoute() {
        if (mapboxMap != null) mapboxMap.clear();

        originLat = originLocation.getLatitude();
        originLon = originLocation.getLongitude();

        originPosition = com.mapbox.geojson.Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        if (originLat < destLat) {
            southPoint = originLat;
            northPoint = destLat;
        } else {
            southPoint = originLat;
            northPoint = destLat;
        }
        if (originLon < destLon) {
            westPoint = originLon;
            eastPoint = destLon;
        } else {
            westPoint = destLon;
            eastPoint = originLon;
        }

//        double latDist = (java.lang.Math.abs(northPoint - southPoint)/0.027264299999998798)*0.017826;
//        double lonDist = (java.lang.Math.abs(java.lang.Math.abs(westPoint) - java.lang.Math.abs(eastPoint))/0.027043066666664117)*0.017826;

        double latDist = (java.lang.Math.abs(northPoint - southPoint));
        double lonDist = (java.lang.Math.abs(java.lang.Math.abs(westPoint) - java.lang.Math.abs(eastPoint)));


//        westPoint += lonDist*0.2; eastPoint -= lonDist*0.2; southPoint += latDist*0.2; northPoint -= latDist*0.2;

        System.out.println(northPoint-southPoint);
        System.out.println(westPoint-eastPoint);

        getRoute(originPosition, destinationPosition);

        LatLng southWestCorner = new LatLng(southPoint, westPoint);
        LatLng northEastCorner = new LatLng(northPoint, eastPoint);

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(southWestCorner) // Northeast
                .include(northEastCorner) // Southwest
                .build();

        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200, 600, 200, 400), 2000);
    }

    private void addMarkers() {
        if (!ADDED_MARKERS) {
            List<Feature> features = new ArrayList<>();
            /* Source: A data source specifies the geographic coordinate where the image marker gets placed. */
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.258875,37.865593})));
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269122,37.871856})));
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269532,37.879842})));

            // START
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {originLocation.getLongitude(),originLocation.getLatitude()})));
            // END
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.283399,37.873960})));
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
            GeoJsonSource source = new GeoJsonSource(MARKER_SOURCE, featureCollection);
            mapboxMap.addSource(source);
            /* Style layer: A style layer ties together the source and image and specifies how they are displayed on the map. */
            SymbolLayer markerStyleLayer = new SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE)
                    .withProperties(
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconImage(MARKER_IMAGE)
                    );
            mapboxMap.addLayer(markerStyleLayer);
        }
        ADDED_MARKERS = Boolean.TRUE;
    }

    /**
     * Mapbox overrides.
     */

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(MainActivity.this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
            Log.d("lastLocation", lastLocation.toString());
        } else {
            locationEngine.addLocationEngineListener(this);
            Log.d("null location", "sad reacts");
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
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
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
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
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * Add the listener to the Mapbox location engine.
     */
    private void addLocationEngineListener() {
        locationEngine.addLocationEngineListener(new LocationEngineListener() {
            @Override
            public void onConnected() {
                requestLocationWithCheck();
            }

            @Override
            public void onLocationChanged(Location location) {
            }
        });
    }


    // google places method needed for Places
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int)ev.getRawX();
                int rawY = (int)ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}