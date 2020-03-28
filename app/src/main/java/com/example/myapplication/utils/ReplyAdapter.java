package com.example.myapplication.utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.models.Comment;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;

import java.util.List;

/**
 * 클래스 명 : ReplyAdapter class
 * 설명 : 댓글 리스트 뷰 어댑터이다.
 */
public class ReplyAdapter extends BaseAdapter {

    private List<Comment> commentList; // 댓글 목록
    private Story story; // 게시물
    private UserManager userManager; // 모든 유저 정보
    private OnClickItemListener onClickItemListener; // 클릭 이벤트

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener{
        void onclickReply(int position);
    }

    public ReplyAdapter(Story story, UserManager userManager){
        this.story = story;
        this.commentList = story.getCommentList();
        this.userManager= userManager;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        final Context context = viewGroup.getContext();
        Comment comment = commentList.get(position);
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_reply,viewGroup,false);
        }

        /* view 셋팅 */
        ImageView thumbnail = view.findViewById(R.id.thumbnailParent);
        TextView content = view.findViewById(R.id.contentParent);
        TextView replyBtn = view.findViewById(R.id.replyParent);
        ImageView thumbnailChild = view.findViewById(R.id.thumbnailChild);
        TextView contentChild = view.findViewById(R.id.contentChild);
        TextView replyBtnChild = view.findViewById(R.id.replyChild);

        /* 일반 댓글과 답글을 구분해 View를 숨기거나 보여준다. */
        if(comment.getChildIndex() == 0){ // 일반 댓글

            /* 일반 댓글, 답글 뷰 */
            thumbnail.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            replyBtn.setVisibility(View.VISIBLE);
            thumbnailChild.setVisibility(View.GONE);
            contentChild.setVisibility(View.GONE);
            replyBtnChild.setVisibility(View.GONE);

            User writer = userManager.getUser(comment.getWriter());
            if(writer != null ){
//                thumbnail.setImageBitmap(FileUtil.createBitmapAsPath(writer.getThumbnailPath()));
                Glide.with(view.getContext())
                        .load(writer.getThumbnailPath())
                        .apply(RequestOptions.circleCropTransform())
                        .error(R.drawable.basic)
                        .into(thumbnail);
            }
            /* 유저 아이디는 굵게 표시한다. */
            content.setText(Html.fromHtml(
                    "<font color=\"#000000\">"+writer.getUserName()+"</font> "+comment.getContent()));

            /* 답글 달기 클릭시 이벤트 정의 */
            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onClickItemListener != null){
                        onClickItemListener.onclickReply(position);
                    }
                }
            });
        }else{ // 답글

            /* 일반 댓글, 답글 뷰 */
            thumbnail.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            replyBtn.setVisibility(View.GONE);
            thumbnailChild.setVisibility(View.VISIBLE);
            contentChild.setVisibility(View.VISIBLE);
            replyBtnChild.setVisibility(View.VISIBLE);

            User writer = userManager.getUser(comment.getWriter());
            if(writer != null){
//                thumbnailChild.setImageBitmap(FileUtil.createBitmapAsPath(writer.getThumbnailPath()));
                Glide.with(view.getContext())
                        .load(writer.getThumbnailPath())
                        .apply(RequestOptions.circleCropTransform())
                        .error(R.drawable.basic)
                        .into(thumbnailChild);
            }

            /* 유저 아이디는 굵게 표시한다. */
            contentChild.setText(Html.fromHtml(
                    "<font color=\"#000000\">"+writer.getUserName()+"</font> "+comment.getContent()));

            /* 답글 달기 클릭시 이벤트 정의 */
            replyBtnChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onClickItemListener != null){
                        onClickItemListener.onclickReply(position);
                    }
                }
            });
        }


        return view;
    }

    public void setData(Story story){
        this.story = story;
        this.commentList = story.getCommentList();
    }

    public List<Comment> getCommentList() {
        return commentList;
    }
}
