package com.example.sylla.trivialim;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Score> listScore;

    public ScoreAdapter(Context context, ArrayList<Score> listScore) {
        this.context = context;
        this.listScore = listScore;
    }

    @Override
    public int getCount() {
        return listScore.size();
    }

    @Override
    public Object getItem(int i) {
        return listScore.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView=new TextView(context);
        textView.setPadding(5,5,5,5);
        Score score=(Score) getItem(i);
        textView.setText(score.getPseudo()+" : "+score.getNbQuestions()+" question(s), "+score.getTemps()+" secondes");
        return textView;
    }
}
