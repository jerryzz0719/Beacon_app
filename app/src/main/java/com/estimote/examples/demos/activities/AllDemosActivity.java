package com.estimote.examples.demos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import static com.estimote.examples.demos.R.id;
import static com.estimote.examples.demos.R.layout;

/**
 * Shows all available demos.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class AllDemosActivity extends AppCompatActivity {



  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.all_demos);
    Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
    toolbar.setTitle(getTitle());

    findViewById(id.distance_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
        intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, DistanceBeaconActivity.class.getName());
        startActivity(intent);
      }
    });

    findViewById(id.position).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(AllDemosActivity.this, MapActivity.class);
        startActivity(intent);
      }
    });
    findViewById(id.parse).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(AllDemosActivity.this, ParsefromDB.class);
        startActivity(intent);
      }
    });
  }
}
