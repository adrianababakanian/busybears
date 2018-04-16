package cs160.dinestination;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.logging.Filter;

public class MainActivity extends FragmentActivity { // implements OnMapReadyCallback {

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button testButton;
    SeekBar mSeekBar;
    Switch mSwitch;
    TextView priceRangeFromSeekBar;
    ImageView layoverRectangle;
    ImageView checkButton;
    ImageView backButton;
    ImageView whereTo;
//    MapFragment mapFragment;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWRyaWFuYWJhYmFrYW5pYW4iLCJhIjoiY2pnMTgxeDQ4MWdwOTJ4dGxnbzU4OTVyMCJ9.CetiZIb8bdIEolkPM4AHbg");

        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        getApplicationContext().setTheme(R.style.AppTheme);

        // hook up UI elements
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        testButton = findViewById(R.id.test_button);
        mSeekBar = findViewById(R.id.seekBar);
        mSwitch = findViewById(R.id.switch1);
        priceRangeFromSeekBar = findViewById(R.id.price_range_from_seek_bar);
        layoverRectangle = findViewById(R.id.layover_rectangle);
        checkButton = findViewById(R.id.check_button);
        backButton = findViewById(R.id.back_button);
        //whereTo = findViewById(R.id.where_to_rectangle);

        //whereTo.setTranslationZ(80);

        mSeekBar.setZ(999);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(30);
        mSwitch.setZ(999);

        // set price range listener and update price range from seek bar
        setPriceRangeListener();

        // set up bottom sheet
        setBottomSheetCallback();
        setOnClickForTestButton();

        setOnClickForFilterTrigger(checkButton);
        setOnClickForFilterTrigger(backButton);

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // get a handle to the map fragment
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    // get a handle to the GoogleMap object
//    @Override
//    public void onMapReady(GoogleMap map) {
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
//    }

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
    public void onStart() {
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
    public void onStop() {
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
