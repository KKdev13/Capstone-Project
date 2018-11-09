package com.example.khale.androidrefresher.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.khale.androidrefresher.QuizFragment;
import com.example.khale.androidrefresher.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragment == null && intent != null && intent.getStringExtra(getString(R.string.key_usecase)) != null){
            String useCase = intent.getStringExtra(getString(R.string.key_usecase));
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.key_usecase), useCase);
            QuizFragment quizFragment = new QuizFragment();
            quizFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, quizFragment).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quiz_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
