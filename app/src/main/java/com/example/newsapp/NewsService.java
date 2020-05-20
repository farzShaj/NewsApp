package com.example.newsapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private static ArrayList<Article> articles=new ArrayList<>();;
    ServiceReceiver serviceReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        serviceReceiver=new ServiceReceiver();
        registerReceiver(serviceReceiver,intentFilter);
        Log.d(TAG, "onStartCommand: Service running");
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    try {
                       //Log.d(TAG, "run: thread started and running");

                        if(articles.isEmpty())
                            Thread.sleep(250);
                        else {
                            Log.d(TAG, "Got data in articles");
                            sendArticles();
                            articles.removeAll(articles);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //Log.d(TAG, "run: Ending loop");
            }
        }).start();
        return Service.START_STICKY;
    }

    private void sendArticles() {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.DATA_ARTICLES, articles);
        sendBroadcast(intent);
        Log.d(TAG, "sendArticles: Broadcasted articles");
    }

    @Override
    public void onDestroy() {
        try {
            Log.d(TAG, "onDestroy: Destroying service");
            running = false;
            unregisterReceiver(serviceReceiver);
            super.onDestroy();
        }catch(Exception e){
            //Log.e(TAG, "onDestroy: ",e );
            Log.d(TAG, "onDestroy: exception");
        }
    }

    public void setArticles(ArrayList<Article> articles){
        this.articles=articles;
    }

    class ServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;

            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    if (intent.hasExtra(MainActivity.DATA_TO_SERVICE)) {
                        //Log.d(TAG, "onReceive: got a message to service");
                        new AsyncArticleDownloader(new NewsService(), intent.getStringExtra(MainActivity.DATA_TO_SERVICE)).execute();
                    }
                    break;

                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }
    }
}
