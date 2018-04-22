package cs160.dinestination;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

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
    SeekBar mSeekBar;
    boolean priceSliderUsedFlag;
    Switch mSwitch1;
    Switch mSwitch2;
    TextView priceRangeFromSeekBar;
    ImageView layoverRectangle;
    ImageView filtersCheckButton;
    ImageView filtersBackButton;
    //Location lastLocation;

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
    EditText whereToEditText;
    Button exitInputButton;
    LinearLayout appliedFiltersWrapper;
    ConstraintLayout mainTopInputElement;
    Button findRestaurantsButton;
    LinearLayout filtersRowTopBar;

    // Map-related.
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private MapboxMap mapboxMap;
    private MapView mapView;
    String mapboxAccessToken = "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg";

    // Route-related.
    private com.mapbox.geojson.Point originPosition;
    private com.mapbox.geojson.Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private Boolean ADDED_MARKERS = Boolean.FALSE;

    // Location layer-related.
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, mapboxAccessToken);

//        lastLocation = new Location("");
//        lastLocation.setLatitude(37.866528);
//        lastLocation.setLongitude(-122.258722);

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
        whereToPlace = findViewById(R.id.where_to_place);
        whereToTime = findViewById(R.id.where_to_time);
        topInputElement = findViewById(R.id.top_input_element);
        whereToEditText = findViewById(R.id.destination_top_input_elem);
        closeTopInputElement = findViewById(R.id.close_top_input_elem);
        addTopInputElement = findViewById(R.id.add_top_input_elem);
        rootLayout = findViewById(R.id.mainRootView);
        whereToInputViewFlipper = findViewById(R.id.viewFlipper1);
        whereToInputViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        whereToInputViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        appliedFiltersLayout = findViewById(R.id.applied_filters_top_input_elem);
        addFiltersButton = findViewById(R.id.add_filters_top_input_elem);
        // addMoreFiltersButton = findViewById(R.id.filters_row_addmore_top_input);
        exitInputButton = findViewById(R.id.exit_input_button);
        appliedFiltersWrapper = findViewById(R.id.applied_filters_wrapper);
        mainTopInputElement = findViewById(R.id.main_top_input_element);
        findRestaurantsButton = findViewById(R.id.find_restaurants_button);
        filtersRowTopBar = findViewById(R.id.filter_row_top_bar);

        whereToInputViewFlipper.setZ(999);
        timeSpinnerBottomSheet.setZ(999);
        filtersBottomSheet.setZ(1000);
        timeSpinnerBottomSheet.setZ(2);

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

        whereToRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeSpinnerSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    findRestaurantsButton.setVisibility(View.GONE);
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
                // addMarkers();
            }
        });

        layoutPreviewBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(goToDetailsIntent);
            }
        });

        addFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        setOnClickForExitInputButton();

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
        mSeekBar.setMax(100);
        mSeekBar.setProgress(30);
        mSwitch1.setZ(999);

        // set price range listener and update price range from seek bar
        setPriceRangeListener();

        // set up bottom sheet
        setPreviewBottomSheetCallback();

        setOnClickForFilterTrigger(filtersCheckButton);
        setOnClickForFilterBack(filtersBackButton);
        setOnClickForFindRestaurants(findRestaurantsButton);

        layoverRectangle.setImageAlpha(0);
        layoverRectangle.setZ(4);

        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.activate();
        addLocationEngineListener();

        setupUI(findViewById(R.id.mainRootView));


    } // END THE ON CREATE METHOD


    private void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        System.out.println("GET ROUTE CALLED");
        NavigationRoute.builder()
//                .accessToken(Mapbox.getAccessToken())
                .accessToken(mapboxAccessToken)
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
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    private void filtersRowGenerator() {
        ArrayList<String> stringsForButtons = new ArrayList<>();
        int[] cuisine_ids = new int[] {R.id.thai_check, R.id.italian_check, R.id.chinese_check, R.id.mexican_check,
                R.id.indian_check, R.id.american_check, R.id.japanese_check, R.id.burmese_check};
        int[] attire_ids = new int[] {R.id.casual_check, R.id.relaxed_check, R.id.dressy_check, R.id.formal_check};

        for (int i = 0; i < cuisine_ids.length; i++) {
            CheckBox cBox = findViewById(cuisine_ids[i]);
            if (cBox.isChecked()) stringsForButtons.add(cBox.getText().toString());
        }
        if (priceSliderUsedFlag) stringsForButtons.add(priceRange);
        for (int i = 0; i < attire_ids.length; i++) {
            CheckBox cBox = findViewById(attire_ids[i]);
            if (cBox.isChecked()) stringsForButtons.add(cBox.getText().toString());
        }

        if (mSwitch1.isChecked()) stringsForButtons.add("Groups");
        if (mSwitch2.isChecked()) stringsForButtons.add("Kids");

        if (stringsForButtons.size() != 0) { // if filters have been applied
            appliedFiltersWrapper.removeViewsInLayout(1, appliedFiltersWrapper.getChildCount()-1);
            filtersRowTopBar.removeAllViewsInLayout(); // since never want AddFilters button showing up.

            Button plusButton = filtersRowGeneratePlusButton();
            Button plusButton2 = filtersRowGeneratePlusButton();

            appliedFiltersWrapper.addView(plusButton);
            filtersRowTopBar.addView(plusButton2);

            addFiltersButton.setVisibility(View.GONE);
            layoverRectangle.setImageAlpha(0);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,0,4,0);
            lp.height = 105;
            for (String label : stringsForButtons) {
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
            filtersRowTopBar.setVisibility(View.GONE);
        }
    }

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
        return newPlusButton;
    }

    private void setOnClickForExitInputButton() {
        exitInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                whereToInputViewFlipper.showNext();
                String whereToText = whereToEditText.getText().toString();
                whereToPlace.setText(whereToText);
                String meridian = "am";
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String minuteStr = timePicker.getCurrentMinute().toString();
                if (hour > 12) {meridian = "pm"; hour = hour - 12;}
                if (minute < 10) {minuteStr = "0".concat(minuteStr);}
                String hourStr = Integer.toString(hour);
                whereToTime.setText(" "+hourStr+":"+minuteStr+meridian);
                whereToPlace.setTextColor(getResources().getColor(R.color.textColorDark));
                whereToTime.setTextColor(getResources().getColor(R.color.textColorDark));
                addMarkers();
                drawHardcodedRoute();
                findRestaurantsButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Set up the UI such that the soft keyboard is collapsed.
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
     * Hide the soft keyboard.
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    // temporary set on clicks for back and check button triggers
    private void setOnClickForFilterTrigger(ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    filtersRowGenerator();
                } else { // else case will never occur. Cannot click checkmark while collapsed.
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }

    private void setOnClickForFilterBack(ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                layoverRectangle.setImageAlpha(0);
            }
        });
    }

    /**
     * Listen for checkbox changes.
     */
    private void setOnCheckedChangeListener() {
//        tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    selectedStrings.add(tv.getText().toString());
//                }else{
//                    selectedStrings.remove(tv.getText().toString());
//                }
//
//            }
//        });
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
     * On clicks for closing preference popup.
     * @param iv
     */
//    private void setOnClickForFilterTrigger(ImageView iv) {
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // REPLACE THE NAME OF THE BEHAVIOR HERE TO MATCH THE FILTER ONE
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    layoverRectangle.setImageAlpha(100);
//                } else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    layoverRectangle.setImageAlpha(0);
//
//                }
//            }
//        });
//    }

    // trigger filter bottom sheet expansion DEPRECATED THIS WAS FOR FILTERS
//    private void setBottomSheetCallback() {
//        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        testButton.setText("Close");
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        testButton.setText("Expand");
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//    }

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

    /**
     * Update price range from seek bar.
     */
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
        priceRange = "$".concat(lowerStr).concat("-").concat(upperStr);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                priceSliderUsedFlag = true;
                Integer progUpper = 10*Math.round(mSeekBar.getProgress()/10);
                Integer progLower;
                if (progUpper <= 10) {
                    progUpper = 10;
                    progLower = 1;
                } else {
                    progLower = progUpper - 10;
                }
                String upperStr = progUpper.toString();
                String lowerStr = progLower.toString();
                priceRangeFromSeekBar.setText("$".concat(lowerStr).concat("-").concat(upperStr));
                priceRange = "$".concat(lowerStr).concat("-").concat(upperStr);
                System.out.println(priceRange);
            }
        });
    }

    private void setOnClickForFindRestaurants(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToHeatMapActivity = new Intent(MainActivity.this, HeatmapActivity.class);
                startActivity(goToHeatMapActivity);
            }
        });
    }


    /**
     * Mapboc overrides.
     * @param mapboxMap
     */
    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        MainActivity.this.mapboxMap = mapboxMap;
        /* Image: An image is loaded and added to the map. */
        Bitmap icon = BitmapFactory.decodeResource(
                MainActivity.this.getResources(), R.drawable.pinpoint, options);
        mapboxMap.addImage(MARKER_IMAGE, icon);
        //addMarkers();

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
                if ((point.getLatitude() <= 37.866528+0.0015 && point.getLatitude() >= 37.866528-0.0015) && (point.getLongitude() <= -122.258722+0.0015 && point.getLongitude() >= -122.258722-0.0015)) {
                    if (previewSheetBehavior.getState() != previewSheetBehavior.STATE_EXPANDED) {
                        previewSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        previewSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

            }
        });

        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng point) {
                System.out.println("Long click registered");
                PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, "my.layer.id");
                if (!features.isEmpty()) {
                    Feature selectedFeature = features.get(0);
                    String title = selectedFeature.getStringProperty("title");
                    Toast.makeText(getApplicationContext(), "You selected " + title, Toast.LENGTH_SHORT).show();
                }
                System.out.println(point);
                destinationPosition = com.mapbox.geojson.Point.fromLngLat(point.getLongitude(), point.getLatitude());
                //destinationPosition = com.mapbox.geojson.Point.fromLngLat(-122.283399, 37.873960);
                originPosition = com.mapbox.geojson.Point.fromLngLat(-122.257290, 37.867460);

                getRoute(originPosition, destinationPosition);
            }
        });

    }

    private void drawHardcodedRoute() {
        destinationPosition = com.mapbox.geojson.Point.fromLngLat(-122.283399, 37.873960);
        originPosition = com.mapbox.geojson.Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        getRoute(originPosition, destinationPosition);
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
//                Log.d("Latitude: ", Double.toString(location.getLatitude()));
//                Log.d("Longitude: ", Double.toString(location.getLongitude()));
//                Log.d("Latitude: ", Double.toString(lastLocation.getLatitude()));
//                Log.d("Longitude: ", Double.toString(lastLocation.getLongitude()));
//                Log.e("Location: ", lastLocation.toString());
            }
        });
    }

}