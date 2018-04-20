package cs160.dinestination;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    TextView restaurantName;
    TextView restaurantAddr;
    TextView waitTime;
    TextView arrivalEstimate;
    Button routeMe;
    Button back;
    String name;
    String addr;
    String estimate;
    String wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // TextViews that need to change based on data passed through
        restaurantName = findViewById(R.id.restaurantName);
        restaurantAddr = findViewById(R.id.restaurantAddr);
        waitTime = findViewById(R.id.waitTime);
        arrivalEstimate = findViewById(R.id.arrivalEstimate);
        // Buttons
        routeMe = findViewById(R.id.routeMe);
        back = findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();
//        name = bundle.getString("");
//        addr = bundle.getString("");
//        estimate = bundle.getString("");
//        wait = bundle.getString("");

        /* Get data from prev page:
            need restaurant name or key to grab more info
        */
//        restaurantName.setText(name);
//        restaurantAddr.setText(addr);
//        waitTime.setText(wait);
//        arrivalEstimate.setText(estimate);

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, HeatmapActivity.class);
                startActivity(intent);
            }

        });

//        routeMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent (DetailsActivity.this, RoutingActivity.class);
//            }
//        });


    }
}
