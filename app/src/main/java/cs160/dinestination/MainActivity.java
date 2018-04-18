package cs160.dinestination;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    TextView testText;
    TimePicker timePicker;
    ImageView whereToRectangle;
    RelativeLayout whereToRectangleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
//        testText = (TextView)findViewById(R.id.textView);
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        whereToRectangle = (ImageView)findViewById(R.id.where_to_rectangle);
        whereToRectangleLayout = (RelativeLayout)findViewById(R.id.where_to_rectangle_layout);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        testButton.setText("Close");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        testButton.setText("Expand");
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

        whereToRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    whereToRectangleLayout.setVisibility(View.VISIBLE);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    whereToRectangleLayout.setVisibility(View.INVISIBLE);

                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
//                    testText.setText(hour + ":" + min);
                }
            }
        });


    }


}
