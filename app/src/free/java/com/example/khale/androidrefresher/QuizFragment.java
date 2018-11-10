package com.example.khale.androidrefresher;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khale.androidrefresher.Database.QuestionContract;
import com.example.khale.androidrefresher.Database.QuestionDbHelper;
import com.example.khale.androidrefresher.QuizData.QuestionPool;
import com.example.khale.androidrefresher.UI.ResultFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int score;
    private int number = 0;
    private int clickCounter = 0;
    private static final int LOADER_ID = 0x02;
    private QuestionPool questionPool;
    private HashMap<Integer, Integer> userSelection;
    private String uriString;
    private MenuItem favorite;

    public static final String TAG  = QuizFragment.class.getSimpleName();

    @BindView(R.id.tv_question_number)
    TextView questionNumber;

    @BindView(R.id.tv_question)
    TextView question;

    @BindView(R.id.tv_answer)
    TextView answer;

    @BindView(R.id.tv_no_questions)
    TextView noQuestionMessage;

    @BindView(R.id.layout_singlechoice)
    LinearLayout singleChoiceLayout;

    @BindView(R.id.rg_singlechoice)
    RadioGroup radioGroupSingleChoice;

    @BindView(R.id.rb_choice1)
    RadioButton singleChoice1;

    @BindView(R.id.rb_choice2)
    RadioButton singleChoice2;

    @BindView(R.id.rb_choice3)
    RadioButton singleChoice3;

    @BindView(R.id.rb_choice4)
    RadioButton singleChoice4;

    @BindView(R.id.adView)
    AdView banner;

    @BindView(R.id.iv_next)
    ImageView next;

    @BindView(R.id.iv_back)
    ImageView back;

    @BindView(R.id.question_and_answers)
    CardView questionsAnswers;

    public QuizFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState  != null){
            if(savedInstanceState.getParcelable(getString(R.string.key_question_pool))!= null){
                questionPool = savedInstanceState.getParcelable(getString(R.string.key_question_pool));
            }

            if(savedInstanceState.getInt(getString(R.string.key_question_number)) != 0){
                number = savedInstanceState.getInt(getString(R.string.key_question_number));
            }

            if(savedInstanceState.getInt(getString(R.string.key_score)) != 0){
                score = savedInstanceState.getInt(getString(R.string.key_score));
            }
        }else {
            questionPool = new QuestionPool();
        }

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.getSerializable(getString(R.string.key_user_selection)) != null){
                userSelection = (HashMap<Integer, Integer>) savedInstanceState.getSerializable(getString(R.string.key_user_selection));
                updateQuestion();
                if(questionPool.getLength() != 0 && userSelection.containsKey(questionPool.getId(number))){
                    showAnswer();
                }
            }
        }else {
            userSelection = new HashMap<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.quiz_screen));

        Bundle bundle = this.getArguments();
        if(bundle != null){
            uriString = bundle.getString(getString(R.string.key_usecase));
        }

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        banner.loadAd(adRequest);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            questionNumber.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        favorite = menu.findItem(R.id.action_favorite);
        favorite.setVisible(true);
        favorite.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addToFavorites();
                return true;
            }
        });

        isFavorite();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
            Cursor questionData = null;

            @Override
            protected void onStartLoading() {
                if(questionData != null){
                    deliverResult(questionData);
                }else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    ContentResolver contentResolver = getActivity().getContentResolver();

                    Uri uri = Uri.parse(uriString);
                    return contentResolver.query(uri, null, null, null, null);

                }catch (Exception e){
                    Log.e(TAG, "Failed to load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, which is a Cursor, to the registered listener
            public void deliverResult(Cursor data){
                questionData = data;
                super.deliverResult(data);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        QuestionDbHelper questionDbHelper = new QuestionDbHelper(getContext());
        questionDbHelper.setCursor(data);

        if(questionPool != null && questionPool.getLength() == 0) {
            questionPool.initQuestions(questionDbHelper);
        }

        updateQuestion();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void updateQuestion(){
        if(number < questionPool.getLength() && number >= 0){
            singleChoice1.setText(questionPool.getChoice(number, 1));
            singleChoice2.setText(questionPool.getChoice(number, 2));
            singleChoice3.setText(questionPool.getChoice(number, 3));
            singleChoice4.setText(questionPool.getChoice(number, 4));

            int realNumber = number + 1;
            questionNumber.setText(getString(R.string.quiz_title) + " " + realNumber + "/" + questionPool.getLength());
            question.setText(questionPool.getQuestion(number));
            if(favorite != null){
                isFavorite();
            }
        }else if (number > 0){
            ResultFragment resultFragment = new ResultFragment();
            Bundle bundle = new Bundle();

            bundle.putInt(getString(R.string.key_score), score);
            bundle.putInt(getString(R.string.key_questions_amount), questionPool.getLength());
            bundle.putString(getString(R.string.key_uri), uriString);
            resultFragment.setArguments(bundle);

            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultFragment).commit();
        } else {
            questionNumber.setVisibility(View.INVISIBLE);
            question.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            questionsAnswers.setVisibility(View.INVISIBLE);
            noQuestionMessage.setVisibility(View.VISIBLE);
            setHasOptionsMenu(false);
        }
    }

    private void showAnswer(){
        String userAnswer = "";
        int checkedButtonId = 0;

        if(number < questionPool.getLength()){
            if(!userSelection.containsKey(questionPool.getId(number))){
                checkedButtonId = radioGroupSingleChoice.getCheckedRadioButtonId();

                switch (checkedButtonId){
                    case R.id.rb_choice1:
                        userAnswer = (String) singleChoice1.getText();
                        break;
                    case R.id.rb_choice2:
                        userAnswer = (String) singleChoice2.getText();
                        break;
                    case R.id.rb_choice3:
                        userAnswer = (String) singleChoice3.getText();
                        break;
                    case R.id.rb_choice4:
                        userAnswer = (String) singleChoice4.getText();
                        break;
                }

                for (int i = 0; i < radioGroupSingleChoice.getChildCount(); i++){
                    radioGroupSingleChoice.getChildAt(i).setEnabled(false);
                }
            } else {
                int storedCheckedButtonId = userSelection.get(questionPool.getId(number));

                switch (storedCheckedButtonId){
                    case R.id.rb_choice1:
                        singleChoice1.setChecked(true);
                        userAnswer = (String) singleChoice1.getText();
                        break;
                    case R.id.rb_choice2:
                        singleChoice2.setChecked(true);
                        userAnswer = (String) singleChoice2.getText();
                        break;
                    case R.id.rb_choice3:
                        singleChoice3.setChecked(true);
                        userAnswer = (String) singleChoice3.getText();
                        break;
                    case R.id.rb_choice4:
                        singleChoice4.setChecked(true);
                        userAnswer = (String) singleChoice4.getText();
                        break;
                }

                for (int i = 0; i < radioGroupSingleChoice.getChildCount(); i++){
                    radioGroupSingleChoice.getChildAt(i).setEnabled(false);
                }
            }

            String correctAnswer = questionPool.getCorrectAnswer(number);
            answer.setText(correctAnswer);
            answer.setVisibility(View.VISIBLE);

            if(correctAnswer.equals(userAnswer)){
                answer.setBackgroundColor(getResources().getColor(R.color.correctBackground));
            }else {
                answer.setBackgroundColor(getResources().getColor(R.color.wrongBackground));
            }

            if(!userSelection.containsKey(questionPool.getId(number))){
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                if(correctAnswer.equals(userAnswer)){
                    values.put(QuestionContract.QuestionColumns.CORRECT, 1);
                    answer.setBackgroundColor(getResources().getColor(R.color.correctBackground));
                    score += 1;
                }else {
                    values.put(QuestionContract.QuestionColumns.CORRECT, 0);
                    answer.setBackgroundColor(getResources().getColor(R.color.wrongBackground));
                }

                Uri uri = Uri.parse(QuestionContract.URI_QUESTIONS + "/" + questionPool.getId(number));
                resolver.update(uri, values, null, null);
                userSelection.put(questionPool.getId(number), checkedButtonId);

            }
            clickCounter++;
        }
    }

    private void updateUserInterface(){
        if(number + 1 < questionPool.getLength()){
            if(!userSelection.containsKey(questionPool.getId(number + 1))){
                radioGroupSingleChoice.clearCheck();
                for(int i = 0; i < radioGroupSingleChoice.getChildCount(); i++){
                    radioGroupSingleChoice.getChildAt(i).setEnabled(true);
                }
                answer.setVisibility(View.INVISIBLE);
            }
        }
        number++;
        clickCounter = 0;
    }
    @OnClick(R.id.iv_next)
    void nextQuestion(){
        if((clickCounter == 0 && !userSelection.containsKey(questionPool.getId(number)))){
            if(radioGroupSingleChoice.getCheckedRadioButtonId() == -1){
                Toast.makeText(getActivity(),getString(R.string.quiz_info_message), Toast.LENGTH_SHORT).show();
            } else {
                showAnswer();
            }
        } else if (number < questionPool.getLength()){
            updateUserInterface();
            updateQuestion();
            if(number < questionPool.getLength() && userSelection.containsKey(questionPool.getId(number))){
                showAnswer();
            }
        }
    }
    @OnClick(R.id.iv_back)
    void previousQuestion(){
        if(number != 0){
            number -= 1;
            updateQuestion();
            clickCounter = 0;
            showAnswer();
        }
    }

    private void isFavorite(){
        if(questionPool.getLength() > 0){
            if(isInFavorites(questionPool.getId(number)) == 1){
                favorite.setIcon(R.drawable.ic_star_black_24dp);
            }else {
                favorite.setIcon(R.drawable.ic_star_border_black_24dp);
            }
        }
    }

    private int isInFavorites(int id){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(QuestionContract.URI_QUESTIONS, null,
                QuestionContract.QuestionColumns._ID + " = ? AND " + QuestionContract.QuestionColumns.FAVORITE + " = 1",
                new String[]{"" + id}, null);
        cursor.close();
        return cursor.getCount();
    }

    private void addToFavorites(){
        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                return isInFavorites(questionPool.getId(number));
            }

            @Override
            protected void onPostExecute(Integer isInFavorites) {
                if(isInFavorites == 1){
                    new AsyncTask<Void, Void, Integer>(){

                        @Override
                        protected Integer doInBackground(Void... params) {
                            ContentValues values = new ContentValues();
                            values.put(QuestionContract.QuestionColumns.FAVORITE, 0);

                            Uri uri = Uri.parse(QuestionContract.URI_QUESTIONS + "/" + questionPool.getId(number));

                            return getActivity().getContentResolver().update(uri, values, null, null);
                        }

                        @Override
                        protected void onPostExecute(Integer rowsDeleted) {
                            favorite.setIcon(R.drawable.ic_star_border_black_24dp);
                        }
                    }.execute();
                } else {
                    //add to favorites
                    new AsyncTask<Void, Void, Integer>(){

                        @Override
                        protected Integer doInBackground(Void... params) {
                            ContentValues values = new ContentValues();
                            values.put(QuestionContract.QuestionColumns.FAVORITE, 1);

                            Uri uri = Uri.parse(QuestionContract.URI_QUESTIONS + "/" + questionPool.getId(number));

                            return getActivity().getContentResolver().update(uri, values, null, null);

                        }

                        @Override
                        protected void onPostExecute(Integer returnUri) {
                            favorite.setIcon(R.drawable.ic_star_black_24dp);
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(getString(R.string.key_user_selection), userSelection);
        outState.putParcelable(getString(R.string.key_question_pool), questionPool);
        outState.putInt(getString(R.string.key_question_number), number);
        outState.putInt(getString(R.string.key_score), score);
    }
}
