package com.example.khale.androidrefresher.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.example.khale.androidrefresher.Database.QuestionContract.*;

public class QuestionContentProvider extends ContentProvider{
    private QuestionDbHelper questionDbHelper;
    private static final UriMatcher uriMatcher;

    private static final int ALL_QUESTIONS = 1;
    private static final int CORRECT_QUESTIONS = 2;
    private static final int WRONG_QUESTIONS = 3;
    private static final int FAVORITE_QUESTIONS = 4;
    private static final int QUESTIONS_ID = 5;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH, ALL_QUESTIONS);
        uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH + "/correct",CORRECT_QUESTIONS);
        uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH +"/wrong", WRONG_QUESTIONS);
        uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH +"/favorite", FAVORITE_QUESTIONS);
        uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH +"#", QUESTIONS_ID);
    }

    @Override
    public boolean onCreate() {
        questionDbHelper = new QuestionDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = questionDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case ALL_QUESTIONS:
                break;
            case CORRECT_QUESTIONS:
                selection = "correct = 1";
                break;
            case WRONG_QUESTIONS:
                selection = "correct = 0";
                break;
            case FAVORITE_QUESTIONS:
                selection = "favorite = 1";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        cursor = db.query(QuestionColumns.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = questionDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case QUESTIONS_ID:
                String id = uri.getPathSegments().get(1);
                selection = QuestionContract.QuestionColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            case FAVORITE_QUESTIONS:
                values = new ContentValues();
                values.put(QuestionContract.QuestionColumns.FAVORITE, 0);
                break;
            case WRONG_QUESTIONS:
                values = new ContentValues();
                values.putNull(QuestionContract.QuestionColumns.CORRECT);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int updateCount = db.update(QuestionContract.QuestionColumns.TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}
