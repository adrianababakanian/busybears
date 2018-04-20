package cs160.dinestination;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;
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

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.telemetry.location.AndroidLocationEngine;
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
import java.util.logging.Filter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    String MY_PERMISSIONS_ACCESS_FINE_LOCATION = "Please enable locationing!";

    Intent goToHeatmapActivityIntent;

    String priceRange;

    // UI elements.
    BottomSheetBehavior previewSheetBehavior;
    LinearLayout layoutPreviewBottomSheet;
    BottomSheetBehavior filtersSheetBehavior;
    LinearLayout filtersBottomSheet;
    Button testButton;
    SeekBar mSeekBar;
    Switch mSwitch1;
    Switch mSwitch2;
    TextView priceRangeFromSeekBar;
    ImageView layoverRectangle;
    ImageView checkButton;
    ImageView backButton;
    ImageView marker;
    Location lastLocation;

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
    ImageButton addMoreFiltersButton;
    Button exitInputButton;
    LinearLayout appliedFiltersWrapper;
    // LinearLayout appliedFiltersWrapperMainScreen;
    ConstraintLayout mainTopInputElement;

    // Mapbox items.
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private MapboxMap mapboxMap;
    private MapView mapView;
    private LocationEngine locationEngine;
    String mapboxAccessToken = "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg";

    // Route-related.
    private com.mapbox.geojson.Point originPosition;
    private com.mapbox.geojson.Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    private Boolean ADDED_MARKERS = Boolean.FALSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, mapboxAccessToken);

        lastLocation = new Location("");
        lastLocation.setLatitude(37.866528);
        lastLocation.setLongitude(-122.258722);

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
        layoverRectangle = findViewById(R.id.layover_rectangle);
        checkButton = findViewById(R.id.check_button);
        backButton = findViewById(R.id.back_button);

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
        // appliedFiltersWrapperMainScreen = findViewById(R.id.main_screen_version);
        mainTopInputElement = findViewById(R.id.main_top_input_element);
        addFiltersButton = findViewById(R.id.add_filters_top_input_elem);
        exitInputButton = findViewById(R.id.exit_input_button);


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
                    case BottomSheetBehavior.STATE_EXPANDED: { }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: { }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
//                        timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
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
                    timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // but only starts anim when fully expanded
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
                if (appliedFiltersLayout.getVisibility() == View.VISIBLE) {
//                    appliedFiltersLayout.setVisibility(View.VISIBLE);
//                    addFiltersButton.setVisibility(View.INVISIBLE);
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        setOnClickForExitInputButton();

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getApplicationContext().setTheme(R.style.AppTheme);

        mSeekBar.setZ(999);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(30);
        mSwitch1.setZ(999);

        // set price range listener and update price range from seek bar
        setPriceRangeListener();

        // set up bottom sheet
        setPreviewBottomSheetCallback();

        setOnClickForFilterTrigger(checkButton);
        setOnClickForFilterTrigger(backButton);

        layoverRectangle.setImageAlpha(0);
        layoverRectangle.setZ(4);

        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.activate();
        addLocationEngineListener();

        setupUI(findViewById(R.id.mainRootView));

        //appliedFiltersWrapper.addView(backButton);


        goToHeatmapActivityIntent = new Intent(MainActivity.this, HeatmapActivity.class);
        //startActivity(goToHeatmapActivityIntent);

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

                        Toast.makeText(getApplicationContext(), Double.toString(currentRoute.distance()), Toast.LENGTH_SHORT).show();
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

        mSwitch1.isChecked(); // groups
        mSwitch2.isChecked(); // kids

        for (int i = 0; i < cuisine_ids.length; i++) {
            CheckBox cBox = findViewById(cuisine_ids[i]);
            if (cBox.isChecked()) {
                stringsForButtons.add(cBox.getText().toString());
            }
        }
        stringsForButtons.add(priceRange);
        for (int i = 0; i < attire_ids.length; i++) {
            CheckBox cBox = findViewById(attire_ids[i]);
            if (cBox.isChecked()) {
                stringsForButtons.add(cBox.getText().toString());
            }
        }

        if (mSwitch1.isChecked()) {
            stringsForButtons.add("Groups");
        }
        if (mSwitch2.isChecked()) {
            stringsForButtons.add("Kids");
        }

        if (stringsForButtons.size() == 0) {
//            stringsForButtons.add();
        }

        appliedFiltersWrapper.removeAllViewsInLayout();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4,0,4,0);
        lp.height = 105;

        if (stringsForButtons.size() != 0) {
            Button plusButton = new Button(this);
            Button plusButton2 = new Button(this);
            plusButton.setBackground(getResources().getDrawable(R.drawable.plus_button));
            plusButton2.setBackground(getResources().getDrawable(R.drawable.plus_button));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    100, 104);
            params.setMargins(4, 0, 4, 0);

            plusButton.setLayoutParams(params);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            appliedFiltersWrapper.addView(plusButton);
            // appliedFiltersWrapperMainScreen.addView(plusButton2);
            addFiltersButton.setVisibility(View.INVISIBLE);
        } else {
            addFiltersButton.setVisibility(View.VISIBLE); // NOT YET TESTED!!!! Not sure if it shows up again.
        }

        for (String label : stringsForButtons) {
            Button buttonToAdd = new Button(this);
            buttonToAdd.setText(label);
            buttonToAdd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            buttonToAdd.setTextColor(getResources().getColor(R.color.white));
            buttonToAdd.setAllCaps(Boolean.FALSE);
            appliedFiltersWrapper.addView(buttonToAdd, lp);
//            appliedFiltersWrapperMainScreen.addView(buttonToAdd, lp);
        }

    }

    private void setOnClickForExitInputButton() {
        exitInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSpinnerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                whereToInputViewFlipper.showNext();
                whereToPlace.setText(whereToEditText.getText());
                String meridian = "am";
                int hour = timePicker.getCurrentHour();
                if (hour > 12) {
                    meridian = "pm";
                    hour = hour - 12;
                }
                String hourStr = Integer.toString(hour);
                String minuteStr = timePicker.getCurrentMinute().toString();
                if (minuteStr.length() == 1) minuteStr = "0" + minuteStr;
                whereToTime.setText(hourStr+":"+minuteStr+meridian);
                addMarkers();

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
                if (filtersSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    layoverRectangle.setImageAlpha(100);
                } else {
                    filtersSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    layoverRectangle.setImageAlpha(0);
                    filtersRowGenerator();
                }
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
            lastLocation = locationEngine.getLastLocation();
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
                priceRange = "$".concat(lowerStr).concat("-").concat(upperStr);
                System.out.println(priceRange);
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
                originPosition = com.mapbox.geojson.Point.fromLngLat(-122.258875, 37.865593);

                getRoute(originPosition, destinationPosition);
            }
        });

    }

    private void addMarkers() {
        if (!ADDED_MARKERS) {
            List<Feature> features = new ArrayList<>();
        /* Source: A data source specifies the geographic coordinate where the image marker gets placed. */
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.258875,37.865593})));
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269122,37.871856})));
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269532,37.879842})));

            // START: CHANNING BOWDITCH
            features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.257290,37.867460})));
            // END: NORTH BERKELEY
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

    @Override
    protected void onStart() {
        super.onStart();

        mapView.onStart();

        if (locationEngine != null) {
            requestLocationWithCheck();
        }
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
                Log.d("Latitude: ", Double.toString(location.getLatitude()));
                Log.d("Longitude: ", Double.toString(location.getLongitude()));
                Log.d("Latitude: ", Double.toString(lastLocation.getLatitude()));
                Log.d("Longitude: ", Double.toString(lastLocation.getLongitude()));
                Log.e("Location: ", lastLocation.toString());
            }
        });
    }

}