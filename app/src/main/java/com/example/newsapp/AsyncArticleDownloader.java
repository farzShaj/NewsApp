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

public class AsyncArticleDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncArticleDownloader";
    private NewsService newsService;
    private String id;
    @SuppressLint("StaticFieldLeak")
    private static String NEWS_API_KEY="c3af71dbd351422881334e03b162a027";
    private static String NEWS_CAT="https://newsapi.org/v2";

    AsyncArticleDownloader(NewsService newsService,String id) {
        this.id=id;
        this.newsService = newsService;
    }
    protected String doInBackground(String... params) {
        String urlToUse = NEWS_CAT + "/everything";
        Uri.Builder buildURL = Uri.parse(urlToUse).buildUpon();
        buildURL.appendQueryParameter("language", "en");
        buildURL.appendQueryParameter("sources", id);
        buildURL.appendQueryParameter("pageSize","100");
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
        newsService.setArticles(getArticleList(s));
    }

    public ArrayList<Article> getArticleList(String s){
        ArrayList<Article> articlesList=new ArrayList<>();
        try {
            JSONObject data = new JSONObject(s);
            //Log.d(TAG, "AFD:"+data);
            JSONArray articles=data.getJSONArray("articles");
            int len=articles.length();
            for(int i=0;i<len;i++){
                JSONObject jsonObject=articles.getJSONObject(i);
                String author=jsonObject.getString("author");
                String title=jsonObject.getString("title");
                String description=jsonObject.getString("description");
                String url=jsonObject.getString("url");
                String image_url=jsonObject.getString("urlToImage");
                String published=jsonObject.getString("publishedAt");
                Article article=new Article(author,title,image_url,url,description,published);
                articlesList.add(article);
            }
        } catch (Exception e) {
            Log.d(TAG, "AFD Error:"+e);
            e.printStackTrace();

        }
        return articlesList;
    }

}
