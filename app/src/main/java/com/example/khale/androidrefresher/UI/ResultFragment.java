package com.example.khale.androidrefresher.UI;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.khale.androidrefresher.Database.QuestionContract;
import com.example.khale.androidrefresher.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultFragment extends Fragment {

    @BindView(R.id.tv_score)
    TextView score;

    @BindView(R.id.bu_again)
    Button again;

    private String uriString;
    private boolean favoriteQuestionsAvailable;
    private boolean wrongQuestionsAvailable;

    public ResultFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.result_screen));

        Bundle bundle =  this.getArguments();
        if(bundle != null){
            int userScore = bundle.getInt(getString(R.string.key_score));
            int questionsAmount = bundle.getInt(getString(R.string.key_questions_amount));
            uriString = bundle.getString(getString(R.string.key_uri));
            score.setText(userScore +"/" + questionsAmount);
        }

        if (uriString.equals(QuestionContract.URI_WRONG_QUESTIONS.toString())){
            FetchWrongQuestionsTask fetchWrong = new FetchWrongQuestionsTask(new AsyncResponse() {
                @Override
                public void finalize(Integer output) {
                    if(output != 0){
                        wrongQuestionsAvailable = true;
                    } else {
                        again.setVisibility(View.INVISIBLE);
                    }
                }
            });
            fetchWrong.execute();
        } else if (uriString.equals(QuestionContract.URI_FAVORITE_QUESTIONS.toString())){
            FetchFavoriteQuestionsTask fetchFavorite = new FetchFavoriteQuestionsTask(new AsyncResponse() {
                @Override
                public void finalize(Integer output) {
                    if(output != 0){
                        favoriteQuestionsAvailable = true;
                    } else {
                        again.setVisibility(View.INVISIBLE);
                    }
                }
            });
            fetchFavorite.execute();
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_favorite).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.bu_again)
    void repeatQuestions(){
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        if(uriString.equals(QuestionContract.URI_QUESTIONS.toString())){
            intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_QUESTIONS.toString());
        }else if(uriString.equals(QuestionContract.URI_WRONG_QUESTIONS.toString())){
            if(wrongQuestionsAvailable) {
                intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_WRONG_QUESTIONS.toString());
            }
        }else if(uriString.equals(QuestionContract.URI_FAVORITE_QUESTIONS.toString())){
            if(favoriteQuestionsAvailable) {
                intent.putExtra(getString(R.string.key_usecase), QuestionContract.URI_FAVORITE_QUESTIONS.toString());
            }
        }
        getActivity().finish();
        startActivity(intent);
    }

    public interface AsyncResponse{
        void finalize(Integer output);
    }

    private int checkWrong() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(QuestionContract.URI_WRONG_QUESTIONS, null, null, null, null);
        cursor.close();
        return cursor.getCount();
    }

    private int checkFavorite(){
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(QuestionContract.URI_FAVORITE_QUESTIONS, null, null, null, null);
        cursor.close();
        return cursor.getCount();
    }

    public class FetchWrongQuestionsTask extends AsyncTask<Void, Void, Integer>{

        public AsyncResponse delegate = null;

        public FetchWrongQuestionsTask(AsyncResponse asyncResponse){
            delegate = asyncResponse; //assigning interface via constructor
        }
        @Override
        protected Integer doInBackground(Void... params) {
            return checkWrong();
        }

        @Override
        protected void onPostExecute(Integer result) {
            delegate.finalize(result);
        }
    }

    public class FetchFavoriteQuestionsTask extends AsyncTask<Void, Void, Integer>{

        public AsyncResponse delegate = null;

        public FetchFavoriteQuestionsTask(AsyncResponse asyncResponse){
            delegate = asyncResponse;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            return checkFavorite();
        }

        @Override
        protected void onPostExecute(Integer result) {
            delegate.finalize(result);
        }
    }


}
