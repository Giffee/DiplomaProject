package com.sergey_kost.diploma.diplomaproject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    TextView helpDescription;
    TextView helpCommands;
    TextView helpCommandsExamples;
    TextView helpAboutAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpDescription = findViewById(R.id.helpDescription);
        helpCommands = findViewById(R.id.helpCommands);
        helpCommandsExamples = findViewById(R.id.helpCommandsExamples);
        helpAboutAuthor = findViewById(R.id.helpAboutAuthor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button clicked, go to parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}