package com.example.newsapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncCatData extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncCatData";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static String NEWS_API_KEY="c3af71dbd351422881334e03b162a027";
    private static String NEWS_CAT="https://newsapi.org/v2";
    AsyncCatData(MainActivity ma) {
        mainActivity = ma;
    }

    protected String doInBackground(String... params) {
        String urlToUse = NEWS_CAT + "/sources";
        Uri.Builder buildURL = Uri.parse(urlToUse).buildUpon();
        buildURL.appendQueryParameter("language", "en");
        buildURL.appendQueryParameter("country", "us");
        buildURL.appendQueryParameter("category","");
        buildURL.appendQueryParameter("apiKey",NEWS_API_KEY);
        urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200)
            {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackgroundError: "+e);
            return null;
        }
        return sb.toString();
    }

    protected void onPostExecute(String s) {
        mainActivity.updateData(listSource(s));
    }

    public ArrayList<Source> listSource(String s){
        ArrayList<Source> sources_list=new ArrayList<>();
        try {
            JSONObject data = new JSONObject(s);
            JSONArray src=data.getJSONArray("sources");
            int len=src.length();
            for(int i=0;i<len;i++){
                JSONObject jsonObject=src.getJSONObject(i);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                String category=jsonObject.getString("category");
                Source source=new Source(id,name,category);
                sources_list.add(source);
            }
        } catch (Exception e) {
            Log.d(TAG, "AFD Error:"+e);
            e.printStackTrace();

        }
        return sources_list;
    }

}
