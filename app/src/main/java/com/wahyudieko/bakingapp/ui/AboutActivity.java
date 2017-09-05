package com.wahyudieko.bakingapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.utils.Constant;
import com.wahyudieko.bakingapp.utils.Utility;

public class AboutActivity extends AppCompatActivity {

    private TextView lastUpdateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button updateButton = (Button) findViewById(R.id.update_recipe_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.getRecipeData(AboutActivity.this);
                Utility.setUpdateDate(getApplicationContext());
                displayLastUpdate();
            }
        });

        lastUpdateTextView = (TextView) findViewById(R.id.last_update_textview);

        displayLastUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

    private void displayLastUpdate(){
        SharedPreferences prefs = getSharedPreferences(Constant.DATE_PREF, MODE_PRIVATE);
        String lastUpdate = prefs.getString("date", null);
        if (lastUpdate != null) {
            String date = prefs.getString("date", "Not updated yet");//"No name defined" is the default value.
            String lastUpdated = "Last update: "+date;
            lastUpdateTextView.setText(lastUpdated);
            lastUpdateTextView.setVisibility(View.VISIBLE);
        }
    }
}
