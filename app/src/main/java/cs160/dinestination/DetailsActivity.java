package cs160.dinestination;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    TextView restaurantName;
    TextView restaurantAddr;
    TextView waitTime;
    TextView arrivalEstimate;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageButton routeMe;
    ImageButton back;
    String name;
    String addr;
    String estimate;
    String wait;
    Double placeLat;
    Double placeLong;
    String placeID;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    BarChart barChart;
    ArrayList<String> dates;
    Random random;
    ArrayList<BarEntry> barEntries;

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
        String originLat = getIntent().getStringExtra("inputLon");
        String originLon = getIntent().getStringExtra("inputLat");
//        String originPosition = getIntent().getStringExtra("originPosition");

//        gridView = (GridView) findViewById(R.id.gridView);
//        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
//        gridView.setAdapter(gridAdapter);

        // TextViews that need to change based on data passed through
        restaurantName = findViewById(R.id.restaurantName);
        restaurantName.setText(restaurantNameVal);
        restaurantAddr = findViewById(R.id.restaurantAddr);
        restaurantAddr.setText(restaurantAddressVal);
        waitTime = findViewById(R.id.waitTime);
        arrivalEstimate = findViewById(R.id.arrivalEstimate);
        arrivalEstimate.setText(restaurantInputPlace + " " + restaurantInputTime);
        // Buttons
        routeMe = (ImageButton) findViewById(R.id.routeMe);
        back = (ImageButton) findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();
        placeLat = bundle.getDouble("placeLat");
        placeLong = bundle.getDouble("placeLong");
        placeID = bundle.getString("placeID");

        yelpApiFactory = new YelpFusionApiFactory();
        yelpFusionApi = null;
        try {
            yelpFusionApi = yelpApiFactory.createAPI("lpPa7H7FfyvUsUwy3SXzFTPvzT3XMpKigRKwjtVy1DIvzEXFISs5_qc5u33z0-jDB2VbGnXdjAGn9RayJW5ft0Ayx3irRzdfvGMVyOm0yGcWVPtAD3HfF_a8w3XpWnYx");
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
        barChart = (BarChart) findViewById(R.id.chart);

        createRandomBarGraph();

        final com.mapbox.geojson.Point originPosition = com.mapbox.geojson.Point.fromLngLat(-122.258844, 37.876051);
        final com.mapbox.geojson.Point destinationPosition = com.mapbox.geojson.Point.fromLngLat(placeLong, placeLat);


        routeMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Point origin = Point.fromLngLat(originLocation.getLongitude(), destinationLocation.getLatitude());
//                Point destination = destinationPosition;
                // Pass in your Amazon Polly pool id for speech synthesis using Amazon Polly
                // Set to null to use the default Android speech synthesizer
                String awsPoolId = null;
                boolean simulateRoute = true;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .awsPoolId(awsPoolId)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(DetailsActivity.this, options);
            }
        });

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

    public void createRandomBarGraph(){

        XAxis xaxis = barChart.getXAxis();
        YAxis yaxisL = barChart.getAxisLeft();
        YAxis yaxisR = barChart.getAxisRight();

        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        yaxisL.setDrawGridLines(false);
        yaxisL.setDrawLabels(false);
        yaxisR.setDrawGridLines(false);
        yaxisR.setDrawLabels(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 80f));
        entries.add(new BarEntry(1f, 60f));
        entries.add(new BarEntry(2f, 50f));
        entries.add(new BarEntry(3f, 40f));
        entries.add(new BarEntry(4f, 80f));
        entries.add(new BarEntry(5f, 60f));
        entries.add(new BarEntry(6f, 60f));
        entries.add(new BarEntry(7f, 30f));
        entries.add(new BarEntry(8f, 80f));
        entries.add(new BarEntry(9f, 60f));
        entries.add(new BarEntry(10f, 50f));
        entries.add(new BarEntry(11f, 70f));
        entries.add(new BarEntry(12f, 60f));
        entries.add(new BarEntry(13f, 40f));
        entries.add(new BarEntry(14f, 20f));
        entries.add(new BarEntry(15f, 80f));
        entries.add(new BarEntry(16f, 30f));
        entries.add(new BarEntry(17f, 40f));
        entries.add(new BarEntry(18f, 60f));
        entries.add(new BarEntry(19f, 80f));
        entries.add(new BarEntry(20f, 80f));
        entries.add(new BarEntry(21f, 80f));
        entries.add(new BarEntry(22f, 80f));
        entries.add(new BarEntry(23f, 80f));



        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(Color.parseColor("#9FD5FF"));

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        data.setDrawValues(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getDescription().setText("");
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setVisibleXRangeMaximum(12);
        barChart.moveViewToX(8);
        barChart.invalidate(); // refresh

    }
}