package com.example.khale.androidrefresher.UI;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.khale.androidrefresher.Adapter.CaseAdapter;
import com.example.khale.androidrefresher.Adapter.RecyclerViewClickListener;
import com.example.khale.androidrefresher.Analytics.AnalyticsApplication;
import com.example.khale.androidrefresher.Database.QuestionContract;
import com.example.khale.androidrefresher.QuizData.Constants;
import com.example.khale.androidrefresher.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SCORE_LOADER = 1;
    private static final int WRONG_QUESTIONS_LOADER = 2;
    private static final int FAVORITE_QUESTIONS_LOADER = 3;

    private int wrongQuestions, favoriteQuestions;
    private ArrayList<String> usecaseList = new ArrayList<>();
    private CaseAdapter caseAdapter;
    private Tracker tracker;

    public static final String TAG = MainFragment.class.getSimpleName();
    public MainFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication analyticsApplication = (AnalyticsApplication) getActivity().getApplication();
        tracker = analyticsApplication.getDefaultTracker();
        prepareUsecaseData();


        getActivity().getSupportLoaderManager().initLoader(SCORE_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(WRONG_QUESTIONS_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(FAVORITE_QUESTIONS_LOADER, null, this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_items);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), QuizActivity.class);
                boolean startQuiz = true;
                switch (position){
                    case 0:
                        bundle.putString(getString(R.string.key_usecase), QuestionContract.URI_QUESTIONS.toString());
                        intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_QUESTIONS.toString());
                        tracker.setScreenName(Constants.SCREEN_QUESTIONS_WRONG);
                        break;
                    case 1:
                        bundle.putString(getString(R.string.key_usecase), QuestionContract.URI_WRONG_QUESTIONS.toString());
                        intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_WRONG_QUESTIONS.toString());
                        tracker.setScreenName(Constants.SCREEN_QUESTIONS_WRONG);
                        if (wrongQuestions == 0) {
                            startQuiz = false;
                            showSnackbar(getActivity().findViewById(R.id.main_layout), getString(R.string.main_no_favorites), Snackbar.LENGTH_SHORT);
                        }
                        break;
                    case 2:
                        bundle.putString(getString(R.string.key_usecase), QuestionContract.URI_FAVORITE_QUESTIONS.toString());
                        intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_FAVORITE_QUESTIONS.toString());
                        tracker.setScreenName(Constants.SCREEN_QUESTIONS_FAVORITE);
                        if(favoriteQuestions == 0){
                            startQuiz = false;
                            showSnackbar(getActivity().findViewById(R.id.main_layout), getString(R.string.main_no_favorites), Snackbar.LENGTH_SHORT);
                        }
                        break;
                }
                if (position != 3 && startQuiz) {
                    tracker.send(new HitBuilders.ScreenViewBuilder().build());
                    startActivity(intent);
                }
            }
        };

        caseAdapter = new CaseAdapter(usecaseList, listener);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(caseAdapter);

        caseAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(SCORE_LOADER, null, this);
        getActivity().getSupportLoaderManager().restartLoader(WRONG_QUESTIONS_LOADER, null, this);
        getActivity().getSupportLoaderManager().restartLoader(FAVORITE_QUESTIONS_LOADER, null, this);
    }

    private void prepareUsecaseData() {
        String usecase = getString(R.string.main_start);
        usecaseList.add(usecase);
        usecase = getString(R.string.main_repeat);
        usecaseList.add(usecase);
        usecase = (getString(R.string.main_favorites));
        usecaseList.add(usecase);
        usecase = (getString(R.string.main_score));
        usecaseList.add(usecase);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SCORE_LOADER:
                return new AsyncTaskLoader<Cursor>(getContext()) {

                    Cursor scoreData = null;

                    protected void onStartLoading() {
                        if (scoreData != null) {
                            deliverResult(scoreData);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {
                        try {
                            ContentResolver resolver = getActivity().getContentResolver();
                            return resolver.query(QuestionContract.URI_CORRECT_QUESTIONS,
                                    null, null, null, null);

                        } catch (Exception e) {
                            Log.e(TAG, "Failed to load data.");
                            e.printStackTrace();
                            return null;
                        }
                    }

                    public void deliverResult(Cursor data) {
                        scoreData = data;
                        super.deliverResult(data);
                    }
                };
            case WRONG_QUESTIONS_LOADER:
                return new AsyncTaskLoader<Cursor>(getContext()) {
                    Cursor wrongData = null;

                    @Override
                    protected void onStartLoading() {
                        if (wrongData != null) {
                            deliverResult(wrongData);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {
                        try {
                            // Get the content resolver
                            ContentResolver resolver = getActivity().getContentResolver();

                            return resolver.query(QuestionContract.URI_WRONG_QUESTIONS,
                                    null, null, null, null);

                        } catch (Exception e) {
                            Log.e(TAG, "Failed to load data.");
                            e.printStackTrace();
                            return null;
                        }
                    }

                    public void deliverResult(Cursor data) {
                        wrongData = data;
                        super.deliverResult(data);
                    }
                };

            case FAVORITE_QUESTIONS_LOADER:
                return new AsyncTaskLoader<Cursor>(getContext()) {
                    Cursor favoriteData = null;

                    @Override
                    protected void onStartLoading() {
                        if (favoriteData != null) {
                            deliverResult(favoriteData);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {
                        try {
                            // Get the content resolver
                            ContentResolver resolver = getActivity().getContentResolver();

                            return resolver.query(QuestionContract.URI_FAVORITE_QUESTIONS,
                                    null, null, null, null);

                        } catch (Exception e) {
                            Log.e(TAG, "Failed to load data.");
                            e.printStackTrace();
                            return null;
                        }
                    }

                    public void deliverResult(Cursor data) {
                        favoriteData = data;
                        super.deliverResult(data);
                    }
                };

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SCORE_LOADER:
                Log.i(TAG, "The current score is: " + data.getCount() + "/" + Constants.TOTAL_QUESTIONS);
                caseAdapter.replaceItem(data.getCount() + "/" + Constants.TOTAL_QUESTIONS, 3);
                break;
            case WRONG_QUESTIONS_LOADER:
                wrongQuestions = data.getCount();
                break;
            case FAVORITE_QUESTIONS_LOADER:
                favoriteQuestions = data.getCount();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void showSnackbar(View view, String message, int duration) {
        //create a snackbar and set a handler action on it
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction(getString(R.string.main_dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }
}

