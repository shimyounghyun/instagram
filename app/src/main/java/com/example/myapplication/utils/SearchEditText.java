package com.example.myapplication.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.myapplication.R;
import com.example.myapplication.models.Place;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchEditText extends AppCompatEditText implements TextWatcher {
    private final String TAG = "위치 검색 > ";
    private Drawable searchIcon;
    private OnFocusChangeListener onFocusChangeListener;
    private Gson gson = new Gson();
    private SearchPlaceAdapter searchPlaceAdapter;
    private ProgressBar progressBar;
    private ImageView statusImg;

    public void setProgressBar(ProgressBar progressBar, ImageView statusImg) {
        this.progressBar = progressBar;
        this.statusImg = statusImg;
    }

    public void setSearchPlaceAdapter(SearchPlaceAdapter searchPlaceAdapter) {
        this.searchPlaceAdapter = searchPlaceAdapter;
    }

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_search_black_24dp);
        searchIcon = DrawableCompat.wrap(tempDrawable);
        DrawableCompat.setTintList(searchIcon, getHintTextColors());
        searchIcon.setBounds(0,0, searchIcon.getIntrinsicWidth(), searchIcon.getIntrinsicHeight());
        searchIcon.setVisible(true,false);
        setCompoundDrawables(searchIcon, null, null, null);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        progressBar.setVisibility(VISIBLE);
        statusImg.setVisibility(GONE);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(getText().length() <= 0) return;
        String clientId = "bX3PhyKnGSJ3ZtFXw5G8";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "Kj3fm2vULS";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode(getText().toString(), "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/local.json?query="+ text; // json 결과
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            System.out.println(response.toString());
            List<Place> placeList = parseJsonData(response.toString());

            searchPlaceAdapter.setSearchList(placeList);
            searchPlaceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        progressBar.setVisibility(GONE);
        statusImg.setVisibility(VISIBLE);
    }


    public List<Place> parseJsonData(String data){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(data);
        JsonArray jsonArray = (JsonArray) jsonObject.get("items");

        List<Place> placeList = new ArrayList<>();
        if(jsonArray.size() > 0){
            for(int i =0; i<jsonArray.size(); i++){
                JsonObject placeData = (JsonObject) jsonArray.get(i);
                String title = placeData.get("title").getAsString();
                String address = placeData.get("address").getAsString();
                Place newPlace = new Place(title,address);
                placeList.add(newPlace);
            }
        }else{
            Place place = new Place("","결과가 없습니다.");
            placeList.add(place);
        }

        return placeList;
    }
}
