package cs160.dinestination;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.util.logging.Filter;

public class MainActivity extends AppCompatActivity {

    Button testFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testFilterButton = findViewById(R.id.test_filter_button);
        setOnClickForTestFilterButton();

    }

    private void setOnClickForTestFilterButton() {
        testFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterDialog filter = new FilterDialog();
                filter.show(getSupportFragmentManager(), "filters");

            }
        });
    }
}
