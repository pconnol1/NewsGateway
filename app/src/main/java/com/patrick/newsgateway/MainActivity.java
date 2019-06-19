 package com.patrick.newsgateway;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

 public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private ArrayList<NewsSource> sourcesDisplayed = new ArrayList<>();
    private HashMap<String, ArrayList<NewsSource>> genreData = new HashMap<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private NewsSource currentNewSource;
    public static int screenWidth, screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth=size.x;
        screenHeight=size.y;

        mDrawerLayout=findViewById(R.id.drawer_layout);
        mDrawerList=findViewById(R.id.drawer_list);


        //Set up drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );



        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // Load the data
        if (genreData.isEmpty()) {
            Log.d(TAG, "onCreate: Load the genre Data");
            new AsyncGenreLoader(this).execute();
        }

    }

     //sets up the genres to get the sources from
    public void setupSources(HashMap<String,HashSet<NewsSource>> genreSourceIn){

        genreData.clear();
        for(String s : genreSourceIn.keySet()){
            ArrayList<NewsSource> sources = new ArrayList<>(genreSourceIn.get(s));
            Collections.sort(sources);
            genreData.put(s, sources);
        }

        ArrayList<String> tempList = new ArrayList<>(genreData.keySet());

        Collections.sort(tempList);
        //This is where it crashes on the rotation.
        for(String s: tempList) {
            if(opt_menu==null)//The menu is disappearing on rotation
                ;
            else
                opt_menu.add(s);
        }

        sourcesDisplayed.addAll(genreData.get(tempList.get(0)));

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item,sourcesDisplayed));

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void selectItem(int position){

        pager.setBackground(null);
        currentNewSource = sourcesDisplayed.get(position);
        new AsyncNewsSourceLoader(this).execute(currentNewSource.getId());

        mDrawerLayout.closeDrawer(mDrawerList);

    }

    public void setArticles(ArrayList<Article> articleList){

        setTitle(currentNewSource.getName());

        for(int i=0; i<pageAdapter.getCount();i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for(int i = 0; i< articleList.size();i++){
            fragments.add(
                    ArticleFragment.newInstance(articleList.get(i), i+1, articleList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

     @Override
     public void onPostCreate( Bundle savedInstanceState) {
         super.onPostCreate(savedInstanceState);
         //sync the toggle state after onRestoreInstanceState
         mDrawerToggle.syncState();
     }

     @Override
     public void onConfigurationChanged(Configuration newConfig) {
         super.onConfigurationChanged(newConfig);
         //pass any configuration Chanfe to the drawer toggle
         mDrawerToggle.syncState();
     }



     public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle "+ item );
            return true;
        }
        setTitle(item.getTitle());

        sourcesDisplayed.clear();
        sourcesDisplayed.addAll(genreData.get(item.getTitle()));

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.opt_menu, menu);
         opt_menu = menu;
         return true;
     }

     ////////////////////////////////////////////////////////////////////

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

         /**
          * Notify that the position of a fragment has been changed.
          * Create a new ID for each position to force recreation of the fragment
          * @param n number of items which have been changed
          */
         void notifyChangeInPosition(int n) {
             // shift the ID returned by getItemId outside the range of all previous fragments
             baseId += getCount() + n;
         }

     }
}
