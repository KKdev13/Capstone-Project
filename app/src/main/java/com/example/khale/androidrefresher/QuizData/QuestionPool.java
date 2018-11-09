package com.example.khale.androidrefresher.QuizData;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.khale.androidrefresher.Database.QuestionDbHelper;

import java.util.ArrayList;
import java.util.List;

public class QuestionPool implements Parcelable {

    List<Question> questions = new ArrayList<>();

    public int getId(int i){
        return questions.get(i).getId();
    }
    public int getFavorite(int i){
        return questions.get(i).getFavorite();
    }
    public int getLength(){
        return questions.size();
    }
    public String getQuestion(int i){
        return questions.get(i).getQuestion();
    }
    public String getCorrectAnswer(int i){
        return questions.get(i).getAnswer();
    }
    public String getChoice(int i, int num){
        return questions.get(i).getChoice(num - 1);
    }
    public void initQuestions(QuestionDbHelper questionDbHelper){
        questions = questionDbHelper.getAllQuestions();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeList(this.questions);
    }
    public QuestionPool(){

    }

    protected QuestionPool(Parcel in){
        this.questions = new ArrayList<>();
        in.readList(this.questions, Question.class.getClassLoader());
    }

    public static final Creator<QuestionPool> CREATOR = new Creator<QuestionPool>() {
        @Override
        public QuestionPool createFromParcel(Parcel source) {
            return new QuestionPool(source);
        }

        @Override
        public QuestionPool[] newArray(int size) {
            return new QuestionPool[size];
        }
    };
}
