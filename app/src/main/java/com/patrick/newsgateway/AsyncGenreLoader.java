package com.patrick.newsgateway;

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
import java.nio.Buffer;
import java.util.HashMap;
import java.util.HashSet;

public class AsyncGenreLoader extends AsyncTask<String, Integer,String> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    final private String TAG = "AsyncGenreLoader";
    private static final String dataURL = "https://newsapi.org/v2/sources?country=us&apiKey=280da7c63e4c4232be9568bdf293fe08";

    AsyncGenreLoader(MainActivity ma){mainActivity=ma;}

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, HashSet<NewsSource>> genreMap = parseJSON(s);
        Log.d(TAG, "onPostExecute: Hashmap: "+genreMap.toString());
        mainActivity.setupSources(genreMap);
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
            //Log.d(TAG, "doInBackground: sb: " + sb.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    private HashMap<String,HashSet<NewsSource>> parseJSON(String s) {
        HashMap<String,HashSet<NewsSource>> genreMap = new HashMap<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jSources = jObjMain.getJSONArray("sources");
            //Log.d(TAG, "parseJSON: Sources: " +jSources.toString('\n'));
            //here we want the names and the categories
            for(int i=0; i<jSources.length();i++){
                JSONObject jSource = jSources.getJSONObject(i);
                String name = jSource.getString("name");
                String id =jSource.getString("id");
                //Log.d(TAG, "parseJSON: name: " + name);
                String category = jSource.getString("category");
                //Log.d(TAG, "parseJSON: category: " + category);
                if(!genreMap.containsKey(category))
                    genreMap.put(category, new HashSet<NewsSource>());
                NewsSource ns = new NewsSource(id,name,category);
                genreMap.get(category).add(ns);


            }

            return genreMap;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "parseJSON: Exception: " +e.toString());
        }
        return null;
    }
}
