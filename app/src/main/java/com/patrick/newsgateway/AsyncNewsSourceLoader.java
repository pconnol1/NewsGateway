package com.patrick.newsgateway;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.caverock.androidsvg.SVG;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class AsyncNewsSourceLoader extends AsyncTask<String,Integer,ArrayList<Article>> {
    private static final String TAG = "AsyncNews";
    @SuppressLint("StaticFieldLink")
    private MainActivity mainActivity;
    private String  selectedNewsSource;

    AsyncNewsSourceLoader(MainActivity ma){mainActivity=ma;}

    @Override
    protected void onPostExecute(ArrayList<Article> articles) {
        mainActivity.setArticles(articles);
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {

        selectedNewsSource = strings[0];

        String dataURL = "https://newsapi.org/v2/top-headlines?pageSize=50&sources="+selectedNewsSource+"&apiKey=280da7c63e4c4232be9568bdf293fe08";

        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

        ArrayList<Article> articleList = parseJSON(sb.toString());

        return articleList;

    }
    private ArrayList<Article> parseJSON(String s){

        ArrayList<Article> articleList = new ArrayList<>();
        try{
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArticles = jObjMain.getJSONArray("articles");

            for (int i =0;i<jArticles.length(); i++){
                JSONObject jArticle = (JSONObject) jArticles.get(i);

                String author = jArticle.getString("author");
                if(author.equals("null")){
                    author = "";
                }

                String  title = jArticle.getString("title");

                String desc = jArticle.getString("description");
                if(desc.equals("null"))
                    desc="";

                String url = jArticle.getString("url");

                String publishTime = jArticle.getString("publishedAt");

                String imageURL = jArticle.getString("urlToImage");
                Log.d(TAG, "parseJSON: imageUrl: " + imageURL);
                articleList.add(
                        new Article(author,title, desc, url, imageURL, publishTime)
                );
            }
            return articleList;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
