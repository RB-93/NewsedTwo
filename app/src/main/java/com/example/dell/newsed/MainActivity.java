package com.example.dell.newsed;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsFeatures>> {
    private static final String TAG = MainActivity.class.getName();
    private static final String REQUESTED_URL = "https://content.guardianapis.com/search?q=Sports&format=json&order-by=newest&page-size=20&from-date=2018-09-01&show-fields=headline,thumbnail,short-url&show-tags=contributor,publication&api-key=751d026c-5315-4412-824f-90852ee18451";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private ImageView emptyImageView;
    private Button emptyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView newsListView =(ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);
        emptyImageView = findViewById(R.id.duck);
        newsListView.setEmptyView(emptyImageView);
        emptyButton = findViewById(R.id.empty_button);
        emptyButton.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //adapter code
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null|| !networkInfo.isConnected()){
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet);
            emptyImageView.setImageResource(R.drawable.sad_duck);
            emptyButton.setVisibility(View.VISIBLE);
            emptyButton.setText(R.string.Internet_Settings);
            emptyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(settingsIntent);
                }
            });
        }
        else {
            LoaderManager loaderManager  = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID,null,this);

        }
        newsAdapter = new NewsAdapter(this, new ArrayList<NewsFeatures>());
        newsListView.setAdapter(newsAdapter);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsFeatures newsFeatures = newsAdapter.getItem(position);
                Uri uri = Uri.parse(newsFeatures.getWebUrl().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }
    @Override
    public Loader<List<NewsFeatures>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this,REQUESTED_URL);
    }
    @Override
    public void onLoadFinished(Loader<List<NewsFeatures>> loader, List<NewsFeatures> data) {
        progressBar.setVisibility(View.GONE);
         if(data!=null&&!data.isEmpty()){
             newsAdapter.addAll(data);
         }
         else{
             emptyButton.setVisibility(View.VISIBLE);
             emptyButton.setText(R.string.contact_developer);
             emptyStateTextView.setText(R.string.no_news);
             emptyImageView.setImageResource(R.drawable.sad_duck);
             newsAdapter.clear();
             emptyButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                             "mailto","srivastava.shivanshu4@gmail.com", null));
                     emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Newsed app is not working");
                     emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                     startActivity(Intent.createChooser(emailIntent, "Send email..."));
                 }
             });
         }
    }
    @Override
    public void onLoaderReset(Loader<List<NewsFeatures>> loader) {
         newsAdapter.clear();
    }
}
