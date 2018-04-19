package cs160.dinestination;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
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

    // UI elements
//    BottomSheetBehavior sheetBehavior;
//    LinearLayout layoutBottomSheet;
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

    private LocationEngine locationEngine;

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    TimePicker timePicker;
    ImageView whereToRectangle;
    ConstraintLayout topInputElement;
    ImageView closeTopInputElement;
    ImageView addTopInputElement;
    CoordinatorLayout rootLayout;
    ViewFlipper whereToInputViewFlipper;
    ConstraintLayout appliedFiltersLayout;
    Button addFiltersButton;
    EditText whereToEditText;
    ImageButton addMoreFiltersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg");

        lastLocation = new Location("");
        lastLocation.setLatitude(37.866528);
        lastLocation.setLongitude(-122.258722);

        setContentView(R.layout.activity_main);

        layoutBottomSheet = findViewById(R.id.time_spinner_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        whereToRectangle = (ImageView)findViewById(R.id.where_to_rectangle);
        topInputElement = (ConstraintLayout)findViewById(R.id.top_input_element);
        whereToEditText = (EditText)findViewById(R.id.destination_top_input_elem);
        closeTopInputElement = (ImageView)findViewById(R.id.close_top_input_elem);
        addTopInputElement = (ImageView)findViewById(R.id.add_top_input_elem);
        rootLayout = (CoordinatorLayout)findViewById(R.id.mainRootView);
        whereToInputViewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper1);
        whereToInputViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        whereToInputViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        appliedFiltersLayout = (ConstraintLayout)findViewById(R.id.applied_filters_top_input_elem);
        addFiltersButton = (Button)findViewById(R.id.add_filters_top_input_elem);
        addMoreFiltersButton = (ImageButton)findViewById(R.id.filters_row_addmore_top_input);

        final Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
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
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // but only starts anim when fully expanded
                    whereToInputViewFlipper.showNext();
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
                }
            }
        });

        closeTopInputElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // force close softkeyboard, else pushes layout up
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                whereToInputViewFlipper.showPrevious();
            }
        });

        addFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appliedFiltersLayout.getVisibility() == View.INVISIBLE) {
                    appliedFiltersLayout.setVisibility(View.VISIBLE);
                    addFiltersButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        addMoreFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appliedFiltersLayout.setVisibility(View.INVISIBLE);
                addFiltersButton.setVisibility(View.VISIBLE);
            }
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getApplicationContext().setTheme(R.style.AppTheme);

        // hook up UI elements
        //layoutBottomSheet = findViewById(R.id.bottom_sheet);
        layoutPreviewBottomSheet = findViewById(R.id.preview_bottom_sheet);
        // sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
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

        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.activate();
        addLocationEngineListener();

    }



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

    // mapbox overrides
    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        MainActivity.this.mapboxMap = mapboxMap;
        /* Image: An image is loaded and added to the map. */
        Bitmap icon = BitmapFactory.decodeResource(
                MainActivity.this.getResources(), R.drawable.pinpoint, options);
        mapboxMap.addImage(MARKER_IMAGE, icon);
        addMarkers();

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
    }

    private void addMarkers() {
        List<Feature> features = new ArrayList<>();
        /* Source: A data source specifies the geographic coordinate where the image marker gets placed. */
        features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.258875,37.865593})));
        features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269122,37.871856})));
        features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-122.269532,37.879842})));
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
