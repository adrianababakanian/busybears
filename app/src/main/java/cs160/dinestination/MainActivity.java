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
import android.widget.RelativeLayout;
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

import com.appyvet.materialrangebar.RangeBar;
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
    RangeBar mSeekBar;
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

    SeekBar toleranceSlider;

    // Map-related.
    RelativeLayout navigationRowWrapper;
    ImageButton navigationWalkButton;
    ImageButton navigationCarButton;
    ImageButton navigationTransitButton;
    ImageButton navigationTaxiButton;
    RelativeLayout whereToElement;

    // Mapbox items.
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
        whereToElement = findViewById(R.id.where_to_element);
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
        toleranceSlider = findViewById(R.id.tolerance_slider);

        navigationRowWrapper = findViewById(R.id.navigation_row_wrapper);
        navigationWalkButton = findViewById(R.id.navigation_walk_button);
        navigationCarButton = findViewById(R.id.navigation_car_button);
        navigationTransitButton = findViewById(R.id.navigation_transit_button);
        navigationTaxiButton = findViewById(R.id.navigation_taxi_button);

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
                    navigationRowWrapper.setVisibility(View.GONE);
                    whereToElementReposition(false);
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
        setOnClickForNavigationButtons(navigationWalkButton, navigationCarButton, navigationTransitButton, navigationTaxiButton);
        navigationWalkButton.callOnClick(); // to set walk as the default routing option.

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


    /**
     * Generates buttons to be displayed in the filters scroll row.
     */
    private void filtersRowGenerator() {
        ArrayList<String> stringsForFilterButtons = new ArrayList<>();
        int[] cuisine_ids = new int[] {R.id.thai_check, R.id.italian_check, R.id.chinese_check, R.id.mexican_check,
                R.id.indian_check, R.id.american_check, R.id.japanese_check, R.id.burmese_check};
        int[] attire_ids = new int[] {R.id.casual_check, R.id.relaxed_check, R.id.dressy_check, R.id.formal_check};

        for (int i = 0; i < cuisine_ids.length; i++) {
            CheckBox cBox = findViewById(cuisine_ids[i]);
            if (cBox.isChecked()) stringsForFilterButtons.add(cBox.getText().toString());
        }
        if (priceSliderUsedFlag) stringsForFilterButtons.add(priceRange);
        for (int i = 0; i < attire_ids.length; i++) {
            CheckBox cBox = findViewById(attire_ids[i]);
            if (cBox.isChecked()) stringsForFilterButtons.add(cBox.getText().toString());
        }

        if (mSwitch1.isChecked()) stringsForFilterButtons.add("Groups");
        if (mSwitch2.isChecked()) stringsForFilterButtons.add("Kids");

        if (stringsForFilterButtons.size() != 0) { // if filters have been applied
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
            filtersRowTopBar.setVisibility(View.GONE);
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
        return newPlusButton;
    }

    /**
     * onClick for the 'Search' button - sets whereTo, time, draws route, etc.
     */
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

                findRestaurantsButton.setVisibility(View.VISIBLE);
                whereToElementReposition(true);
                navigationRowWrapper.setVisibility(View.VISIBLE);
                toleranceSlider.setVisibility(View.GONE);

                addMarkers();
                drawHardcodedRoute();
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
                priceSliderUsedFlag = false;
                mSeekBar.setRangePinsByValue(10, 50);

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
=        if (lowerVal <= 25 || upperVal <= 25) {
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
        return priceRangeQueryStr.substring(0, priceRangeQueryStr.length() - 1);
    }

    /**
     * onClick for 'Find restaurants' button - executes goToHeatmapActivity intent.
     */
    private void setOnClickForFindRestaurants(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent goToHeatMapActivity = new Intent(MainActivity.this, HeatmapActivity.class);
//                goToHeatMapActivity.putExtra("whereToLocation", whereToEditText.getText().toString());
//                goToHeatMapActivity.putExtra("whereToTime", whereToTime.getText().toString());
//                // T ODO: Pass required values across
////                goToHeatMapActivity.putExtra(); // need to pass all applied filters lol
////                      //- can do hacky implementation with standardised var names and loop through, or serializable/parcelable class.
////                goToHeatMapActivity.putExtra() // pass routing latitude & longitude over. or some way to do using mapbox?
//                startActivity(goToHeatMapActivity);

                // TODO: make markers show up on map now
                toleranceSlider.setVisibility(View.VISIBLE);
                navigationRowWrapper.setVisibility(View.GONE);


            }
        });
    }

    /**
     * onClick listeners for nav option buttons - sets visuals for which is selected.
     * TODO: hook these clicks up to Mapbox routing!
     */
    private void setOnClickForNavigationButtons(final ImageButton vWalk, final ImageButton vCar,
                                                final ImageButton vTransit, final ImageButton vTaxi) {
        vWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                vWalk.setBackground(getResources().getDrawable(R.drawable.nav_button_pressed_backg)); // for rounded buttons
                vWalk.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp_pressed));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vTransit.setBackground(getResources().getDrawable(R.color.white));
                vTransit.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_transit_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
            }
        });
        vCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp_pressed));
                vTransit.setBackground(getResources().getDrawable(R.color.white));
                vTransit.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_transit_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
            }
        });
        vTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vTransit.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vTransit.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_transit_24dp_pressed));
                vTaxi.setBackground(getResources().getDrawable(R.color.white));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi));
            }
        });
        vTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vWalk.setBackground(getResources().getDrawable(R.color.white));
                vWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_walk_24dp));
                vCar.setBackground(getResources().getDrawable(R.color.white));
                vCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_car_24dp));
                vTransit.setBackground(getResources().getDrawable(R.color.white));
                vTransit.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_transit_24dp));
                vTaxi.setBackground(getResources().getDrawable(R.color.colorPrimary));
                vTaxi.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_call_taxi_pressed));
            }
        });
    }


    /**
     * Repositions whereToElement up or down. Moves in tandem with 'Find restaurants' button visibility.
     */
    private void whereToElementReposition(Boolean shouldShiftUp) {
        ViewGroup.MarginLayoutParams whereToElementParams = (ViewGroup.MarginLayoutParams) whereToElement.getLayoutParams();
        if (shouldShiftUp) {
            whereToElementParams.setMargins(whereToElementParams.leftMargin, 133, whereToElementParams.rightMargin, whereToElementParams.bottomMargin);
        } else {
            whereToElementParams.setMargins(whereToElementParams.leftMargin, 300, whereToElementParams.rightMargin, whereToElementParams.bottomMargin);
        }
        whereToElement.requestLayout();
    }


    //////// MAPBOX THINGS ////////
    /**
     * Mapboc overrides.
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