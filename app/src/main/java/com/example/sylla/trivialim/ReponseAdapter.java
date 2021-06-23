package com.example.sylla.trivialim;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ReponseAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> Reponses;

    public ReponseAdapter(Context context, List<String> reponses) {
        this.context = context;
        this.Reponses = reponses;
    }

    @Override
    public int getCount() {
        return Reponses.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView=new TextView(context);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(15,15,15,15);
        textView.setBackgroundColor(textView.getResources().getColor(R.color.Gray,null));
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        textView.setText(Reponses.get(i));
        return textView;
    }
}
