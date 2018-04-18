package com.example.administrator.okhttplearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        mListView.setAdapter(adapter);
        adapter.add("Get请求");
        adapter.add("Post请求");
        adapter.add("复习okhttp");
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this,TestGetActivity.class));
                break;

            case 1:
                startActivity(new Intent(this,TestPostActivity.class));
                break;

            case 2:
                startActivity(new Intent(this,ReviewActivity.class));
                break;
        }
    }
}
