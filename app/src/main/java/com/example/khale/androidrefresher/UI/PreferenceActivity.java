package com.example.khale.androidrefresher.UI;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.khale.androidrefresher.Database.QuestionContract;
import com.example.khale.androidrefresher.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferenceActivity extends AppCompatActivity{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.settings_screen));
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new PrefFragment()).commit();

    }

    public static class PrefFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference progress = findPreference(getString(R.string.key_progress));
            progress.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getContentResolver().update(QuestionContract.URI_WRONG_QUESTIONS, null, null, null);
                    Toast.makeText(getActivity(), getString(R.string.pref_progress_message), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            Preference favorites = findPreference(getString(R.string.key_favorites));
            favorites.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getContentResolver().update(QuestionContract.URI_FAVORITE_QUESTIONS, null, null, null);
                    Toast.makeText(getActivity(), getString(R.string.pref_favorites_message), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
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
