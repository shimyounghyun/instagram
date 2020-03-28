package com.example.myapplication.utils;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Place;

import java.util.ArrayList;
import java.util.List;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.SearchPlaceHolder>{

    private List<Place> searchList;
    private OnclickItemListener onclickItemListener;

    public void setOnclickItemListener(SearchPlaceAdapter.OnclickItemListener onclickItemListener) {
        this.onclickItemListener = onclickItemListener;
    }

    public interface OnclickItemListener{
        public void onclickLinearLayout(Place place);
    }

    public SearchPlaceAdapter(){
        searchList = new ArrayList<>();
    }

    public SearchPlaceAdapter(List<Place> searchList){
        this.searchList = searchList;
    }

    public void setSearchList(List<Place> searchList) {
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public SearchPlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_place,parent,false);
        return new SearchPlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPlaceHolder holder, int position) {
        final Place place = searchList.get(position);
        if(TextUtils.isEmpty(place.getPlaceTitle())){
            holder.title.setVisibility(View.GONE);
            holder.address.setText("결과가 없습니다.");
        }else{
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(Html.fromHtml(place.getPlaceTitle()));
            holder.address.setText(place.getAddress());
        }

        holder.placeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickItemListener.onclickLinearLayout(place);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class SearchPlaceHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView address;
        public LinearLayout placeItem;

        public SearchPlaceHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.address = itemView.findViewById(R.id.address);
            this.placeItem = itemView.findViewById(R.id.placeItem);
        }
    }
}
