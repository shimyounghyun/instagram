package com.example.myapplication.utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;

import java.util.List;

/**
 * 클래스 명 : HomeStoryAdapter class
 * 설명 : 홈 화면의 게시물 목록(vertical)을 보여주기위한 클래스이다.
 */
public class HomeStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.RecycledViewPool viewPool  = new RecyclerView.RecycledViewPool();
    private SparseIntArray listPosition = new SparseIntArray(); // 해당 위치를 기억시킨다.
    private List<Story> storyList;
    private UserManager userManager; // 모든 유저 관리 객체
    private Context context;
    private OnclickItemListener onclickItemListener; // 클릭 이벤트를 받아와 onBindViewHolder에 적용시킨다.

    /**
     * 받아온 Story가 storyList에 존재한다면 변경시킨다.
     * @param story
     */
    public void updateStory(Story story){
        int index = 0;
        for(Story each : storyList){
            if(each.equals(story)){
                storyList.set(index,story);
            }
            index++;
        }
    }

    /**
     * 아이템 클릭 이벤트 정의
     */
    public interface OnclickItemListener{
        void onclickInfoItem(HomeStoryHolder holder, int position);
        void onclickReplyBtn(HomeStoryHolder holder, int position);
    }

    public void setOnclickItemListener(OnclickItemListener onclickItemListener){
        this.onclickItemListener = onclickItemListener;
    }

    public HomeStoryAdapter(List<Story> storyList, UserManager userManager){
        this.storyList = storyList;
        this.userManager = userManager;
    }

    public List<Story> getStoryList() {
        return storyList;
    }

    @NonNull
    @Override
    public HomeStoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_story,parent,false);
        context = parent.getContext();
        return new HomeStoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        HomeStoryHolder homeStoryHolder = (HomeStoryHolder)holder;

        /* 위치값에 맞는 Story를 가져와 정보를 넣는다. */
        final Story story = storyList.get(position);
        User user = userManager.getUser(story.getWriter());
        Glide.with(context)
                    .load(user.getThumbnailPath())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.basic)
                    .into(homeStoryHolder.userThumbnail);
//        homeStoryHolder.userThumbnail.setImageBitmap(FileUtil.createBitmapAsPath(user.getThumbnailPath()));
        homeStoryHolder.like.setText("좋아요 "+story.getLikeCount()+"개");
        homeStoryHolder.userId.setText(user.getUserName());
        if(!TextUtils.isEmpty(story.getContent())){
            homeStoryHolder.content.setText(Html.fromHtml(
                    "<font color=\"#000000\">"+user.getUserName()+"</font> "+story.getContent()));
        }
        homeStoryHolder.setData(story);

        /* 상세 버튼(...) 클릭시 다이얼로그를 띄어준다.*/
        homeStoryHolder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onclickItemListener != null){
                    onclickItemListener.onclickInfoItem((HomeStoryHolder)holder,position);
                }
            }
        });

        /* 댓글 이미지 클릭시 댓글 화면으로 이동한다. */
        homeStoryHolder.replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onclickItemListener != null){
                    onclickItemListener.onclickReplyBtn((HomeStoryHolder)holder,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    /**
     * 목록에서 잠시 사라질때 해당 위치를 기억한다.
     * @param holder
     */
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        final int position = holder.getAdapterPosition();
        HomeStoryHolder homeStoryHolder = (HomeStoryHolder)holder;
        int firstVisiblePosition = homeStoryHolder.layoutManager.findFirstVisibleItemPosition();
        listPosition.put(position,firstVisiblePosition);
        super.onViewRecycled(homeStoryHolder);
    }

    public class HomeStoryHolder extends RecyclerView.ViewHolder {
        private ImageView userThumbnail;
        private TextView userId;
        private RecyclerView mediaRecyclerView;
        private TextView like;
        private ImageView detailBtn;
        private MediaAdapter mediaAdapter;
        private LinearLayoutManager layoutManager;
        private TextView writer;
        private TextView content;
        private ImageView replyBtn;
        private String storyId;

        public HomeStoryHolder(View view){
            super(view);
            /* 위젯 셋팅 */
            this.detailBtn = view.findViewById(R.id.detailBtn);
            this.userThumbnail = view.findViewById(R.id.userThumbnail);
            this.replyBtn = view.findViewById(R.id.replyBtn);
            this.userId = view.findViewById(R.id.userId);
            this.like = view.findViewById(R.id.like);
            this.content = view.findViewById(R.id.content);
            this.writer = view.findViewById(R.id.writer);
            this.mediaRecyclerView = view.findViewById(R.id.mediaRecyclerView);
            this.mediaAdapter = new MediaAdapter();
            this.layoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
            this.mediaRecyclerView.setLayoutManager(layoutManager);
            this.mediaRecyclerView.setAdapter(mediaAdapter);
            this.mediaRecyclerView.setRecycledViewPool(viewPool);
            this.mediaRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration());

            /* api 25 이상에서 recyclerView는 viewPager처럼 페이지를 넘기는 방식으로 동작할 수 있다. */
            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(this.mediaRecyclerView);
        }

        public void setData(Story story){
            mediaAdapter.setMediaList(story.getMediaList());
            storyId = story.getStoryId();
        }

    }
}
