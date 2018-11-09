package com.example.khale.androidrefresher.Adapter;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.khale.androidrefresher.R;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.QuizViewHolder> {

    private List<String> usecaseList;
    private RecyclerViewClickListener listener;
    private int height, width;

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView description;
        private RecyclerViewClickListener listener;

        public  QuizViewHolder(View view, RecyclerViewClickListener listener){
            super(view);
            description = view.findViewById(R.id.tv_usecase);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public CaseAdapter (List<String> usecaseList, RecyclerViewClickListener listener){
        this.usecaseList = usecaseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);

        if(parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
             height = parent.getMeasuredHeight() / 4;
             width = parent.getMeasuredWidth();

            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        }else {
            height = parent.getMeasuredHeight() / 2;
            width = parent.getMeasuredWidth() / 2;

            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        }

        return new QuizViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        String useCase = usecaseList.get(position);
        holder.description.setText(useCase);
    }

    @Override
    public int getItemCount() {
        return usecaseList.size();
    }

    public void replaceItem(String item, int position){
        usecaseList.set(position, item);
        notifyItemChanged(position);
    }
}
