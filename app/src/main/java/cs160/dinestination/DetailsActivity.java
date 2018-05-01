package cs160.dinestination;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

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
    Double placeLat;
    Double placeLong;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        // TextViews that need to change based on data passed through
        restaurantName = findViewById(R.id.restaurantName);
        restaurantAddr = findViewById(R.id.restaurantAddr);
        waitTime = findViewById(R.id.waitTime);
        arrivalEstimate = findViewById(R.id.arrivalEstimate);
        // Buttons
        routeMe = findViewById(R.id.routeMe);
        back = findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();
        placeLat = bundle.getDouble("placeLat");
        placeLong = bundle.getDouble("placeLong");

        

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
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
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

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        return imageItems;
    }
}