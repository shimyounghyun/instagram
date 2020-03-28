package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Place;
import com.example.myapplication.utils.ReplyAdapter;
import com.example.myapplication.utils.SearchEditText;
import com.example.myapplication.utils.SearchPlaceAdapter;

public class SearchLocationActivity extends AppCompatActivity {

    RecyclerView placeRecyclerView;
    SearchPlaceAdapter searchPlaceAdapter;
    SearchEditText searchView;
    ProgressBar searchPogressBar;
    ImageView statusImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        /* api 호출시 스레드 사용없이 인터넷 연결하여 데이터를 가져온다. */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* 위젯 초기화 */
        initView();
        initRecyclerView();


    }

    SearchPlaceAdapter.OnclickItemListener onclickItemListener = new SearchPlaceAdapter.OnclickItemListener() {
        @Override
        public void onclickLinearLayout(Place place) {
            Intent searchIntent = new Intent(SearchLocationActivity.this, AddFeedActivity.class);
            searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            searchIntent.putExtra("place",place);
            startActivity(searchIntent);
            finish();
        }
    };


    public void initRecyclerView(){
        searchPlaceAdapter = new SearchPlaceAdapter();
        searchPlaceAdapter.setOnclickItemListener(onclickItemListener);
        searchView.setSearchPlaceAdapter(searchPlaceAdapter);
        placeRecyclerView = findViewById(R.id.placeRecyclerView);
        placeRecyclerView.setAdapter(searchPlaceAdapter);
        placeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }

    public void initView(){
        searchView = findViewById(R.id.searchView);
        searchPogressBar = findViewById(R.id.progressBar);
        statusImg = findViewById(R.id.statusImg);
        searchView.setProgressBar(searchPogressBar, statusImg);
    }
}
