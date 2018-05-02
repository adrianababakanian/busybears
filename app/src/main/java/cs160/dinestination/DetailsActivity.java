package cs160.dinestination;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    TextView restaurantName;
    TextView restaurantAddr;
    TextView waitTime;
    TextView arrivalEstimate;
    TextView routeMe2;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageButton routeMe;
    Button back;
    String name;
    String addr;
    String estimate;
    String wait;
    Double placeLat;
    Double placeLong;
    String placeID;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    YelpFusionApiFactory yelpApiFactory;
    YelpFusionApi yelpFusionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String restaurantNameVal = getIntent().getStringExtra("names");
        String restaurantAddressVal = getIntent().getStringExtra("addresses");
        String restaurantPictureVal = getIntent().getStringExtra("pictures");
        String restaurantInputTime = getIntent().getStringExtra("inputTime");
        String restaurantInputPlace = getIntent().getStringExtra("inputPlace");

        Double restaurantWaitTimeSeconds = Double.valueOf(getIntent().getStringExtra("waitTime"));
        String restaurantWaitTimeStr = String.valueOf(Math.ceil(restaurantWaitTimeSeconds / 60));
        restaurantWaitTimeStr = restaurantWaitTimeStr.substring(0, restaurantWaitTimeStr.length()-2);
        String restaurantArriveTime = getIntent().getStringExtra("arriveTime");

//        gridView = (GridView) findViewById(R.id.gridView);
//        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
//        gridView.setAdapter(gridAdapter);

        // TextViews that need to change based on data passed through
        restaurantName = findViewById(R.id.restaurantName);
        restaurantName.setText(restaurantNameVal);
        restaurantAddr = findViewById(R.id.restaurantAddr);
        restaurantAddr.setText(restaurantAddressVal);
        waitTime = findViewById(R.id.waitTime);
        waitTime.setText("Wait ~" + restaurantWaitTimeStr + " mins");
        arrivalEstimate = findViewById(R.id.arrivalEstimate);
//        arrivalEstimate.setText(restaurantInputPlace + " " + restaurantInputTime);
        arrivalEstimate.setText("Likely to arrive at "+ restaurantInputPlace + " by " + restaurantArriveTime);

        // Buttons
        routeMe = (ImageButton) findViewById(R.id.routeMe);
        routeMe2 = findViewById(R.id.routeMe2);
        routeMe2.setText("Route me to " + restaurantNameVal);
        back = findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();
        placeLat = bundle.getDouble("placeLat");
        placeLong = bundle.getDouble("placeLong");
        placeID = bundle.getString("placeID");

        yelpApiFactory = new YelpFusionApiFactory();
        yelpFusionApi = null;
        try {
            yelpFusionApi = yelpApiFactory.createAPI("lpPa7H7FfyvUsUwy3SXzFTPvzT3XMpKigRKwjtVy1DIvzEXFISs5_qc5u33z0-jDB2VbGnXdjAGn9RayJW5ft0Ayx3irRzdfvGMVyOm0yGcWVPtAD3HfF_a8w3XpWnYx");
//            yelpFusionApi = yelpApiFactory.createAPI("dczs4nuyUTOJWGPaXth8Zqt0IwzGoD0Wr-8OZgDmdu4G0oa3M3K-GzlPVYFAh4indjgmImwbDSSaWnh2d7KQgSFly0AresZM9PGy6p4IRUgJcE3ElHJyWyXIb7jeWnYx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        yelpBusinessQuery();


        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
//                startActivity(intent);
            finish();

            }

        });

//        routeMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent (DetailsActivity.this, RoutingActivity.class);
//            }
//        });


    }

    private void yelpBusinessQuery() {
        Callback<Business> callback = new Callback<Business>() {
            @Override
            public void onResponse(Call<Business> call, Response<Business> response) {
                Business business = response.body();
                ArrayList<String> photos = business.getPhotos();
                img1 = findViewById(R.id.food1);
                Picasso.with(getApplicationContext()).load(photos.get(0)).resize(img1.getMeasuredWidth(), img1.getMeasuredHeight()).centerCrop().into(img1);
                img2 = findViewById(R.id.food2);
                Picasso.with(getApplicationContext()).load(photos.get(1)).resize(img1.getMeasuredWidth(), img1.getMeasuredHeight()).centerCrop().into(img2);
                img3 = findViewById(R.id.food3);
                Picasso.with(getApplicationContext()).load(photos.get(2)).resize(img1.getMeasuredWidth(), img1.getMeasuredHeight()).centerCrop().into(img3);

            }
            @Override
            public void onFailure(Call<Business> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                Log.d("business", "HALP");
            }
        };

        Call<Business> call = yelpFusionApi.getBusiness(placeID);
        call.enqueue(callback);
    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        return imageItems;
    }
}