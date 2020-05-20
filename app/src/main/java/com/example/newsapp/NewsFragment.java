package com.example.newsapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.squareup.picasso.Picasso;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String ARTICLE_URL="";
    private ImageView image;
    private TextView title, date, author, description, count;
    private Article article ;
    String message ;

    private String mParam1;
    private String mParam2;


    public NewsFragment() {
        // Required empty public constructor
    }


    public static NewsFragment newInstance(String msg, Article article) {
        Log.d(TAG, "NF new instance: called for fragment");
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, msg);
        args.putSerializable(ARG_PARAM2, article);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Log.d(TAG, "onActivityCreated: fragment activity created");
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: fragment created ");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: Creating fragments menu");
        inflater.inflate(R.menu.option_menu,menu);
        HashMap cat_map=MainActivity.cat_map;
        menu.clear();
        int num=0;
        if(cat_map.size()>0) {
            Iterator iterator = cat_map.keySet().iterator();
            while (iterator.hasNext()) {
                String next_cat=(String) iterator.next();
                // menu.add(R.menu.option_menu,Menu.NONE,0,next_cat);
                menu.add(next_cat);
                MenuItem item=menu.getItem(num);
                SpannableString s = new SpannableString(next_cat);
                if(MainActivity.color_map.containsKey(next_cat))
                {
                    s.setSpan(new ForegroundColorSpan(MainActivity.color_map.get(next_cat)), 0, s.length(), 0);
                }
                item.setTitle(s);
                num++;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setMenuVisibility(true);
        Log.d(TAG, "onCreateView: creating fragment view");
        View v = inflater.inflate(R.layout.fragment_news, container, false);
            try {

                if (getArguments() != null) {
                    message = getArguments().getString(ARG_PARAM1);
                    article = (Article) getArguments().getSerializable(ARG_PARAM2);
                    String dt = "";
                    image= v.findViewById(R.id.image);
                    if (article != null) {
                        if (article.getPublishedAt() != null)
                            dt = article.getPublishedAt().replace("T", " ").replace("Z", "").trim();
                        SimpleDateFormat sdf_in = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        SimpleDateFormat sdf_out = new SimpleDateFormat("MMM dd, yyyy HH:mm ");
                        title = v.findViewById(R.id.title);
                        title.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openArticle();
                            }
                        });
                        date = v.findViewById(R.id.date);
                        author = v.findViewById(R.id.author);
                        description = v.findViewById(R.id.desc);
                        description.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openArticle();
                            }
                        });
                        count = v.findViewById(R.id.count);
                        String atitle = article.getTitle();
                        String aauthor = article.getAuthor();
                        String adescription = article.getDescription();
                        if (atitle != null && !atitle.equals("null"))
                            title.setText(atitle);
                        date.setText(sdf_out.format(sdf_in.parse(dt)));
                        if (aauthor != null && !aauthor.equals("null"))
                            author.setText(article.getAuthor());
                        if (adescription != null && !adescription.equals("null"))
                            description.setText(article.getDescription());
                        if (message != null)
                            count.setText(message);
                        Picasso picasso = new Picasso.Builder(getContext()).build();
                        if (article.getImage_url() != null)
                            picasso.load(article.getImage_url())
                                    .into(image);
                        if (article.getUrl() != null)
                            ARTICLE_URL = article.getUrl();
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openArticle();
                            }
                        });
                    }

                }
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: ", e);
            }

        return v;
    }

    public void openArticle(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(ARTICLE_URL));
        startActivity(i);
    }


}
