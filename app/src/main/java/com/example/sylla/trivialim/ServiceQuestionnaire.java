package com.example.sylla.trivialim;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceQuestionnaire extends IntentService {
    private static final String Tag="ServiceQuestion";

    public ServiceQuestionnaire() { super("ServiceQuestionnaire"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        TriviaAPI triviaService=TriviaAPI.retrofit.create(TriviaAPI.class);
        final Call<Enonce> call=triviaService.getFeed(11,"multiple",intent.getStringExtra("Difficulte"));
        call.enqueue(new Callback<Enonce>() {
            @Override
            public void onResponse(Call<Enonce> call, Response<Enonce> response) {
                Log.d(Tag,response.body().toString());

                if(response.body().getResponseCode()==0){
                    Intent broadcast=new Intent();
                    broadcast.setAction("GetQuestions");
                    broadcast.putExtra("Questions",(new Gson()).toJson(response.body()));
                    sendBroadcast(broadcast);
                }
            }

            @Override
            public void onFailure(Call<Enonce> call, Throwable t) {
                Log.d(Tag,t.toString());
            }
        });
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(Tag,"Starting service");
    }
}
