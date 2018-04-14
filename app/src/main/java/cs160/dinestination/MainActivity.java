package cs160.dinestination;

import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {


    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DialogFragment d = new EntryDialog();
//        final Dialog dialog = new Dialog(new ContextThemeWrapper(this, R.style.DialogSlideAnim));
//        getWindow().setGravity(Gravity.BOTTOM);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

//        Dialog dialog = new Dialog(this);
//        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        testButton = (Button)findViewById(R.id.button);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

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


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    testButton.setText("Close Bottom sheet");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    testButton.setText("Expand Bottom sheet");
                }
            }
        });
    }


}
