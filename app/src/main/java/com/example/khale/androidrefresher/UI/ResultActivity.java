package com.example.khale.androidrefresher.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.khale.androidrefresher.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState  != null){
                return;
            }

            ResultFragment resultFragment = new ResultFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
