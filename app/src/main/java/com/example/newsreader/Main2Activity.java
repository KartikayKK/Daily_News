package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Switch;

public class Main2Activity extends AppCompatActivity {
    String hello;
    String wuhoo;
    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String sessionId = getIntent().getStringExtra("Urls");
        String sessionTitle=getIntent().getStringExtra("Title");
        hello =sessionId;
        wuhoo =sessionTitle;
        System.out.println("I am enjoying this very much"+sessionId);

        WebView mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(sessionId);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId())
    {
        case R.id.new_game :
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = hello;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Good choice");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
        return true;
        case R.id.fav :

            if (MainActivity.arrurl.contains(hello)){

                item.setIcon(R.drawable.ic_star_border_black_24dp);
                MainActivity.arrurl.remove(hello);
                MainActivity.arrtit.remove(wuhoo);


            }else{
                item.setIcon(R.drawable.ic_star_black_24dp);

                MainActivity.arrurl.add(hello);
                MainActivity.arrtit.add(wuhoo);

            }

            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    }
}
