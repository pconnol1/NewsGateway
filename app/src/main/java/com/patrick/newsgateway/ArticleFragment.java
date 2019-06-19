package com.patrick.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleFragment extends Fragment {

    public ArticleFragment(){

    }

    public static ArticleFragment newInstance(Article article, int index, int max){
        ArticleFragment f = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate Layout for fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_news, container, false);

        final Article currentArticle = (Article) getArguments().getSerializable("ARTICLE_DATA");
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");


        TextView title = fragment_layout.findViewById(R.id.textView);
        title.setText(currentArticle.getTitle());
        title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doClick(currentArticle.getUrl());
            }
        });

        //TODO make date look OKAY
        TextView date = fragment_layout.findViewById(R.id.textView2);
        String time=currentArticle.getPublishedAt();

        String inputP = "yyyy-mm-dd'T'hh:mm:ss";
        String outputP = "MMM dd yyyy hh:mm";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputP);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputP);
        try{
            Date inputDate = inputFormat.parse(time);
            time = outputFormat.format(inputDate);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        date.setText(time);

        TextView authors = fragment_layout.findViewById(R.id.textView3);
        authors.setText(currentArticle.getAuthor());

        TextView text = fragment_layout.findViewById(R.id.textView4);
        text.setText(currentArticle.getDescription());
        text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doClick(currentArticle.getUrl());
            }
        });

        TextView pageNum = fragment_layout.findViewById(R.id.textView5);
        pageNum.setText(String.format(Locale.US, "%d of %d", index,total));

        ImageView imageView = fragment_layout.findViewById(R.id.imageView);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

        if(!currentArticle.getImageURL().equals("null")||!currentArticle.getImageURL().equals("")){
            Picasso picasso = new Picasso.Builder(this.getContext())
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        }
                    }).build();
            picasso.load(currentArticle.getImageURL()).into(imageView);
        }
        //imageView.setImageDrawable(currentArticle.getImageURL());
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doClick(currentArticle.getUrl());
            }
        });
        return fragment_layout;
    }

    public void doClick(String URL) {
        Uri uri = Uri.parse(URL);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
