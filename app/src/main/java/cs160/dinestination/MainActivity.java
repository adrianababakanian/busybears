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

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
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

    }


}
