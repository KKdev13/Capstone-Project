package com.example.khale.androidrefresher.QuizData;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable{

    private int id, favorite, type;
    private String question, answer;
    private String[] choice = new String[4];

    public Question(){}

    public Question(int id, int favorite, int type, String question, String answer, String[] choice){
        this.id = id;
        this.favorite = favorite;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.choice[0] = choice[0];
        this.choice[1] = choice[1];
        this.choice[2] = choice[2];
        this.choice[3] = choice[3];
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }
    public void setFavorite(int favorite){
        this.favorite = favorite;
    }
    public int getFavorite(){
        return favorite;
    }
    public void setQuestion(String question){
        this.question = question;
    }
    public String getQuestion(){
        return question;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }
    public String getAnswer(){
        return answer;
    }
    public void setChoice(int i, String choice){
        this.choice[i] = choice;
    }
    public String getChoice(int i){
        return choice[i];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.favorite);
        parcel.writeInt(this.type);
        parcel.writeString(this.question);
        parcel.writeString(this.answer);
        parcel.writeStringArray(this.choice);

    }

    protected Question(Parcel parcel){
        this.id = parcel.readInt();
        this.favorite = parcel.readInt();
        this.type = parcel.readInt();
        this.question = parcel.readString();
        this.answer = parcel.readString();
        this.choice = parcel.createStringArray();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
