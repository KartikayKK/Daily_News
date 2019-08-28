package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

//    private static int splah_time_out=3000;

    static ArrayList<String>  arr=new ArrayList<String>();
    static ArrayList<String>  arr2=new ArrayList<String>();
    static ArrayList<String>  arrurl=new ArrayList<String>();
    static ArrayList<String>  arrtit=new ArrayList<String>();

    ListView listView;
    ArrayAdapter<String> adapter;
    String kresult;
    ProgressDialog progressDialog;
    SQLiteDatabase articledb;


    public  void task(){
        Cursor c= articledb.rawQuery("SELECT * FROM articles",null);
        int Titleind = c.getColumnIndex("title");
        int Urlind = c.getColumnIndex("url");
        if (c.moveToFirst()){
            arr.clear();
            arr2.clear();
            do{
                arr.add(c.getString(Titleind));
                arr2.add(c.getString(Urlind));
            }while (c.moveToNext());
            adapter.notifyDataSetChanged();
        }

    }


    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("kkkkkkkkkkkk","mmmmmmmmmmmmmmmmm");
            HttpURLConnection urlConnection = null;
            String result = "";


            try {
                URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
                urlConnection = (HttpURLConnection) url.openConnection();


                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line ="";
                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();

               // return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            finally {
                urlConnection.disconnect();
            }
            try {
                int p;
                JSONArray array=new JSONArray(result);
                articledb.execSQL("DELETE FROM articles");
                for (p=0;p<30;p++){
                    if (array.getString(p)!=null){
                        String Id =array.getString(p);
                        URL yrl= new URL("https://hacker-news.firebaseio.com/v0/item/"+Id+".json?print=pretty");
                        HttpURLConnection connection=(HttpURLConnection) yrl.openConnection();
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        String kline;
                        String kresult="";
                        if (in != null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            while ((kline = bufferedReader.readLine()) != null)
                                kresult += kline;
                        }
                        JSONObject title=new JSONObject(kresult);
                        String titleled=title.getString("title");
                        String tileurl=title.getString("url");
                        in.close();

                    String sql="INSERT INTO articles ( article_id, title, url) VALUES ( ?, ?, ?)";
                    SQLiteStatement statement= articledb.compileStatement(sql);

                    statement.bindString(1,Id);
                        statement.bindString(2,titleled);
                        statement.bindString(3,tileurl);
                        statement.execute();
//  //                  arr.add(titleled);
//  //                  arr2.add(tileurl);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Getting the best Content :)");
            progressDialog.setTitle("Loading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            task();
            //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, arr);


            super.onPostExecute(result);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Handler ().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent in=new Intent(MainActivity.this,HomeActivity.class);
//                startActivity(in);
//                finish();
//            }
//        },splah_time_out);
        listView=findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("Urls",arr2.get(i));
                intent.putExtra("Title",arr.get(i));
                startActivity(intent);
            }
        });

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, arr);
        listView.setAdapter(adapter);
        articledb = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        articledb.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY ,article_id INTEGER,title VARCHAR,url VARCHAR)");
        task();
        new GetData().execute();


        };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Intent i=new Intent(this,Main3Activity.class);
        i.putStringArrayListExtra("list", arrurl);
        i.putStringArrayListExtra("titles", arrtit);

        startActivity(i);

        return  true;
    }
}
