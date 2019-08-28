package com.example.newsreader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    ArrayAdapter<String>adapter ;
    ListView hello;
    ArrayList<String> ar1i;
    ArrayList<String> ar2i;
    static ArrayList<String> ar2=new ArrayList<String>();
    static ArrayList<String> ar1=new ArrayList<String>();
    int i=0;


    SQLiteDatabase db;

    public  void task(){
        Cursor cursor= db.rawQuery("SELECT * FROM favs ",null);
        int Titleind = cursor.getColumnIndex("tittle");
        int Urlind = cursor.getColumnIndex("urrl");

        if (cursor.moveToFirst()){
            do{
                if (ar2.contains(cursor.getString(Titleind))){
                    //Hello
                }else{
                ar2.add(cursor.getString(Titleind));
                ar1.add(cursor.getString(Urlind));
                }
            }while (cursor.moveToNext());
            //adapter.notifyDataSetChanged();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toast.makeText(Main3Activity.this, "Tap and hold to delete favourite",
                Toast.LENGTH_LONG).show();
        hello=findViewById(R.id.heloo);
        db = this.openOrCreateDatabase("favourite",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS favs (id INTEGER PRIMARY KEY,tittle VARCHAR,urrl VARCHAR)");
        if (i==0) {
            ar1i = getIntent().getExtras().getStringArrayList("list");
            ar2i = getIntent().getExtras().getStringArrayList("titles");
            int lop = ar1i.size();
            for (int p = 0; p < lop; p++) {

                String sql = "INSERT INTO favs (tittle,urrl) VALUES ( ?, ?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, ar2i.get(p));
                statement.bindString(2, ar1i.get(p));
                statement.execute();
                i=1;
            }
            task();
        }else{
            System.out.println("Yo");
        }

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                ar2);
        hello.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
                intent.putExtra("Urls",ar1.get(i));
                intent.putExtra("Title",ar2.get(i));

                startActivity(intent);
            }
        });
        hello.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                           final int pos, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Main3Activity.this);
                alert.setTitle("DELETE");
                alert.setMessage("ARE YOU SURE ?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fin = ar2.get(pos);
                        Log.i("String...........",fin);
                        String blink = "DELETE FROM favs WHERE tittle= ?";
                        SQLiteStatement blind = db.compileStatement(blink);
                        blind.bindString(1,fin);
                        blind.execute();
                        ar2.remove(fin);
                        adapter.notifyDataSetChanged();
                        //                        db.execSQL("DELETE FROM favs where tittle =" +fin );

                    }
                });
                alert.setNegativeButton("No",null);
                alert.show();

                return true;
            }
        });

//        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, arr);
//        hello.setAdapter(adapter);

        hello.setAdapter(adapter);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clear, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        db.execSQL("DELETE FROM favs");
        adapter.clear();
        adapter.notifyDataSetChanged();

//        Toast.makeText(this,"Favourites will be cleared next time you open the app.",
//                Toast.LENGTH_LONG).show();

        return  true;
    }
}
