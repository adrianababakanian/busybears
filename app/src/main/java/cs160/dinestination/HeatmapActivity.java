package cs160.dinestination;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeatmapActivity extends AppCompatActivity {

    TextView whereToPlace;
    TextView whereToTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        whereToPlace = findViewById(R.id.where_to_place);
        whereToTime = findViewById(R.id.where_to_time);
        whereToPlace.setText(getIntent().getStringExtra("whereToLocation"));
        whereToTime.setText(getIntent().getStringExtra("whereToTime"));

        // TODO: include a 'back' navigation button.

    }
}
