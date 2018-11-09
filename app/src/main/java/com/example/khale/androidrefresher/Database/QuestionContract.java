package com.example.khale.androidrefresher.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class QuestionContract {
    public static final String QUESTIONS_PATH = "questions";
    public static final String AUTHORITY = "com.example.khale.androidrefresher";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri URI_QUESTIONS =
            BASE_CONTENT_URI.buildUpon().appendPath(QUESTIONS_PATH).build();
    public static final Uri URI_CORRECT_QUESTIONS =
            BASE_CONTENT_URI.buildUpon().appendEncodedPath(QUESTIONS_PATH + "/correct").build();
    public static final Uri URI_WRONG_QUESTIONS =
            BASE_CONTENT_URI.buildUpon().appendEncodedPath(QUESTIONS_PATH +"/wrong").build();
    public static final Uri URI_FAVORITE_QUESTIONS =
            BASE_CONTENT_URI.buildUpon().appendEncodedPath(QUESTIONS_PATH + "/favorite").build();

    public static final class QuestionColumns implements BaseColumns{
        public static final String TABLE = "Question";
        public static final String TYPE = "type";
        public static final String QUESTION = "question";
        public static final String FAVORITE = "favorite";
        public static final String CHOICE1 = "choice1";
        public static final String CHOICE2 = "choice2";
        public static final String CHOICE3 = "choice3";
        public static final String CHOICE4 = "choice4";
        public static final String CORRECT = "correct";
        public static final String ANSWER = "answer";

    }
}
