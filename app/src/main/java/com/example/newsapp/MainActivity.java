package com.example.newsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String DATA_TO_SERVICE="DATA_TO_SERVICE";
    public static final String DATA_ARTICLES="DATA_ARTICLES";
    public static final String ACTION_MSG_TO_SERVICE="ACTION_MSG_TO_SERVICE";
    public static final String ACTION_NEWS_STORY="ACTION_NEWS_STORY";
    private static String NEWS_API_KEY="c3af71dbd351422881334e03b162a027";
    private static String NEWS_CAT="https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=";
    private static ArrayList<String> categories;
    private static ArrayList<String> sources;
    private static ArrayAdapter<String> adapter;
    private static String category_selected;
    private static HashMap<String,String> nm_id_map;
    public static HashMap<String,Integer> color_map;
    public static HashSet<Integer> color_list;
    public static HashMap<String,ArrayList<Source>> cat_map;
    public static HashMap<String,ArrayList<String>> cat_nm_map;
    public static String selected_source="";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Menu menu;
    public static ActionBar toolbar;
    private boolean show_articles=false;
    NewsReceiver newsReceiver;
    private static String apptitle="";
    private static MyPageAdapter pageAdapter;
    private static List<Fragment> fragments = new ArrayList<>();
    private static ViewPager pager;
    private static int drawercount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        color_map=new HashMap<>();
        color_list=new HashSet<>();
        // Add a background image to the view pager
        pager.setBackground(
                getResources().getDrawable(R.drawable.news_img, this.getTheme()));
        Intent intent=new Intent(this,NewsService.class);
        startService(intent);
        categories=new ArrayList<>();
        Log.d(TAG, "onCreate: MA created"+categories.size());
        sources=new ArrayList<>();
        nm_id_map=new HashMap<>();
        cat_map=new HashMap<>();
        cat_nm_map=new HashMap<>();
        mDrawerLayout = findViewById(R.id.drawer_layout); // <== Important!
        mDrawerList = findViewById(R.id.left_drawer); // <== Important!
        mDrawerList.setOnItemClickListener(   // <== Important!
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(   // <== Important!
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        new AsyncCatData(this).execute();
        apptitle="NewsGateway";
        if(savedInstanceState!=null) {
            category_selected = (savedInstanceState.getString("Category"));
            show_articles = savedInstanceState.getBoolean("article");
            selected_source = savedInstanceState.getString("ID");
            if (show_articles) {
                Intent intent1 = new Intent();
                intent1.setAction(ACTION_MSG_TO_SERVICE);
                intent1.putExtra(DATA_TO_SERVICE, selected_source);
                sendBroadcast(intent1);
            }
            apptitle=savedInstanceState.getString("TITLE");
        }
        setTitle(apptitle);


    }
    public void startReceiver(){
        newsReceiver=new NewsReceiver();
        IntentFilter intentFilter=new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver,intentFilter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option_menu,menu);
        this.menu=menu;
        Log.d(TAG, "onCreateOptionsMenu: created menu");
        return super.onCreateOptionsMenu(menu);
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {  // <== Important!
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        else {
            sources.removeAll(sources);
            category_selected=item.getTitle().toString();
            Iterator iterator=cat_map.get(category_selected).iterator();
            while(iterator.hasNext()){
                Source source=(Source)iterator.next();
                sources.add(source.getName());
            }
            updateDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        String src_nm=sources.get(position);
        String id=nm_id_map.get(src_nm);
        setTitle(src_nm);
        apptitle=src_nm;
        selected_source=id;
        show_articles=true;
        Intent intent=new Intent();
        intent.setAction(ACTION_MSG_TO_SERVICE);
        intent.putExtra(DATA_TO_SERVICE,id);
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void updateDrawer(){
        //Log.d(TAG, "onCreate: source="+sources.size());
       mDrawerList.setAdapter(new ArrayAdapter(this,   // <== Important!
                R.layout.drawer_list_item, sources){
           @NonNull
           @Override
           public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
               View v= super.getView(position, convertView, parent);
               TextView textView=v.findViewById(android.R.id.text1);
               String src=textView.getText().toString();
               Iterator cat_it=cat_nm_map.keySet().iterator();
               int i=0;
               while(cat_it.hasNext()){
                   String nxt=(String)cat_it.next();
                   if(cat_nm_map.get(nxt).contains(src))
                   {
                       textView.setTextColor(color_map.get(nxt));
                       break;
                   }
               }
               return v;
           }
       });
    }

    public void updateData(ArrayList<Source> sources_list){
        menu.clear();
        cat_map.put("all",new ArrayList<Source>());
        Iterator iterator=sources_list.iterator();
        while(iterator.hasNext()){
            Source source=(Source)iterator.next();
            String category=source.getCategory();
            String name=source.getName();
            String id=source.getId();
            if(cat_map.containsKey(category))
            {
                cat_map.get(category).add(source);
                cat_nm_map.get(category).add(source.getName());
            }
            else {

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                while(!color_list.add(color)){
                    color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                }
                color_map.put(category,color);
                ArrayList<Source> cat_arr=new ArrayList<>();
                cat_arr.add(source);
                cat_map.put(category,cat_arr);
                cat_nm_map.put(category,new ArrayList<String>());
                cat_nm_map.get(category).add(source.getName());
            }
            cat_map.get("all").add(source);
            nm_id_map.put(name,id);
            sources.add(name);
        }
        int num=0;
        if(cat_map.size()>0) {
            iterator = cat_map.keySet().iterator();
            while (iterator.hasNext()) {
                String next_cat=(String) iterator.next();
                menu.add(next_cat);
                MenuItem item=menu.getItem(num);
                SpannableString s = new SpannableString(next_cat);
                if(color_map.containsKey(next_cat))
                {
                    s.setSpan(new ForegroundColorSpan(color_map.get(next_cat)), 0, s.length(), 0);
                }
                item.setTitle(s);
                num++;
            }
        }
        if(category_selected!=null){
            sources.removeAll(sources);
            Iterator iter=cat_map.get(category_selected).iterator();
            while(iter.hasNext()){
                Source source=(Source)iter.next();
                sources.add(source.getName());
            }
        }
        updateDrawer();
    }

    public void generateFragments(ArrayList<Article> articleList){
        int num=10;
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();
        for (int i = 0; i < num; i++) {
            fragments.add(
                    NewsFragment.newInstance( (i + 1) + " of " + num,articleList.get(i)));
        }
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
        pager.setBackground(null);
        Log.d(TAG, "generateFragments: "+selected_source);

    }

    public void onResume(){
        Log.d(TAG, "onResume: MA resumed");
        super.onResume();
        startReceiver();
    }

    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: MA paused");
        unregisterReceiver(newsReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent i=new Intent(this,NewsService.class);
        stopService(i);
        Log.d(TAG, "onStop: MA stopped");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.d(TAG, "onSaveInstanceState: Called"+selected_source);
        outState.putString("Category",category_selected);
        outState.putBoolean("article",show_articles);
        outState.putString("ID",selected_source);
        outState.putString("TITLE",apptitle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
    class NewsReceiver extends BroadcastReceiver {
        private static final String TAG = "NewsReceiver";
        MainActivity mainActivity=new MainActivity();
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Article> articles;
            if (intent == null || intent.getAction() == null)
                return;

            switch (intent.getAction()) {
                case MainActivity.ACTION_NEWS_STORY:
                    Log.d(TAG, "onReceive: Received data from service ");
                    if (intent.hasExtra(MainActivity.DATA_ARTICLES)) {
                        //  value = intent.getStringExtra(MainActivity.DATA_EXTRA1);
                        articles = (ArrayList) intent.getSerializableExtra(MainActivity.DATA_ARTICLES);
                        mainActivity.generateFragments(articles);
                    }
                    break;

                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }
    }


    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }


    }
}
