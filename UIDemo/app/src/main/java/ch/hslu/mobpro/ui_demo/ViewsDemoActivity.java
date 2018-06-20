package ch.hslu.mobpro.ui_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

public class ViewsDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views_demo);
        setRatingBarListener();
    }

    private void setRatingBarListener() {
        final TextView ratingView = findViewById(R.id.txtViewsDemosRating);
        final RatingBar ratingBar = findViewById(R.id.rbViewsDemo);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            ratingView.setText(String.valueOf(rating));
        });
    }
}
