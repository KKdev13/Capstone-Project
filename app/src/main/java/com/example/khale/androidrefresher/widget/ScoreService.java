package com.example.khale.androidrefresher.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.khale.androidrefresher.Database.QuestionContract;
import com.example.khale.androidrefresher.QuizData.Constants;

public class ScoreService extends IntentService{
    public static final String GET_CURRENT_SCORE = "com.example.khale.androidrefresher.widget.action.get_current_score";
    public ScoreService(){
        super("ScoreService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(GET_CURRENT_SCORE.equals(action)){
                handleAction();
            }
        }
    }

    public static void startAction(Context context){
        Intent intent = new Intent(context, ScoreService.class);
        intent.setAction(GET_CURRENT_SCORE);
        context.startService(intent);
    }

    private void handleAction(){
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(QuestionContract.URI_CORRECT_QUESTIONS, null, null, null, null);
        String score = cursor.getCount() + "/" + Constants.TOTAL_QUESTIONS;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, QuizWidgetProvider.class));
        //update all widgets
        QuizWidgetProvider.updateScore(this, appWidgetManager, score, appWidgetIds);

    }
}
