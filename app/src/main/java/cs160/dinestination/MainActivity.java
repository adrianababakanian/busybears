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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    TimePicker timePicker;
    ImageView whereToRectangle;
    ConstraintLayout topInputElement;
    ImageView closeTopInputElement;
    ImageView addTopInputElement;
    CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        whereToRectangle = (ImageView)findViewById(R.id.where_to_rectangle);
        topInputElement = (ConstraintLayout)findViewById(R.id.top_input_element);
        closeTopInputElement = (ImageView)findViewById(R.id.close_top_input_elem);
        addTopInputElement = (ImageView)findViewById(R.id.add_top_input_elem);
        rootLayout = (CoordinatorLayout)findViewById(R.id.mainRootView);
//        whereToRectangle.setAnimation();

//        Animation fadeIn = new AlphaAnimation(0, 1);
//        fadeIn.setInterpolator(new DecelerateInterpolator());
//        fadeIn.setDuration(1000);
//        AnimationSet inputFadeInSet = new AnimationSet(true);
//        AnimationSet inputFadeOutSet = new AnimationSet(true);
//        animation.addAnimation(fadeIn);
        final Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
//        whereToRectangle.setAnimation(animation);


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        topInputElement.startAnimation(fadeIn);
                        whereToRectangle.startAnimation(fadeOut);

                        topInputElement.setVisibility(View.VISIBLE);
                        whereToRectangle.setVisibility(View.INVISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        topInputElement.startAnimation(fadeOut);
                        whereToRectangle.startAnimation(fadeIn);

                        topInputElement.setVisibility(View.INVISIBLE);
                        whereToRectangle.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // prevents dragging
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
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
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

//                    int topInputElemHeight = topInputElement.getHeight();
//                    int rootHeight = findViewById(R.id.mainRootView).getHeight();
//                    // note: on first click, this topInputElem takes the hardcoded value, not what's set here.
//                    layoutBottomSheet.getLayoutParams().height = rootHeight - topInputElemHeight;
//                    layoutBottomSheet.requestLayout();
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
//                    testText.setText(hour + ":" + min);
                }
            }
        });

        closeTopInputElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                // force close soft keyboard, else pushes layout up.. hmm.. this works, but poor animation.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });



    }


}
