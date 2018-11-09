package com.example.khale.androidrefresher.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.khale.androidrefresher.QuizData.Question;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class QuestionDbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "androidrefresher.db";
    private static final int DATABASE_VERSION = 1;
    private Cursor cursor;

    public QuestionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<Question> getAllQuestions(){
        cursor = getCursor();
        List<Question> questions = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                Question question = new Question();

                int id =cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionColumns._ID));
                question.setId(id);

                int favorite = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionColumns.FAVORITE));
                question.setFavorite(favorite);

                int type = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionColumns.TYPE));
                question.setType(type);

                String questionText = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.QUESTION));
                question.setQuestion(questionText);

                String answerText = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.ANSWER));
                question.setAnswer(answerText);

                String choice1 = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.CHOICE1));
                question.setChoice(0, choice1);

                String choice2 = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.CHOICE2));
                question.setChoice(1, choice2);

                String choice3 = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.CHOICE3));
                question.setChoice(2, choice3);

                String choice4 = cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionColumns.CHOICE4));
                question.setChoice(3, choice4);

                questions.add(question);
            }while (cursor.moveToNext());
            Collections.shuffle(questions);
        }
        return questions;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }
}
