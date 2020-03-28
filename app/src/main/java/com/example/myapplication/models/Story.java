package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 클래스 명 : Story class
 * 설명 : 게시글 작성시 정보가 담기는 클래스이다.
 */
public class Story implements Parcelable {
    /**
     * 작성자 아이디
     */
    private String writer;

    /**
     * 첨부된 이미지, 동영상 목록
     */
    private List<StoryMedia> mediaList;

    /**
     * 작성된 내용
     */
    private String content;

    /**
     * 작성일
     */
    private String reportingDate;

    /**
     * 스토리 식별자
     */
    private String storyId;

    /**
     * 좋아요 표시를한 유저 목록
     */
    private List<Like> likeList;

    /**
     * 댓글 목록
     */
    private List<Comment> commentList;


    public Story(){
        this.mediaList = new ArrayList<>();
        this.likeList = new ArrayList<>();
        this.commentList = new ArrayList<>();
    }
    public Story(StoryMedia storyMedia){
        this.mediaList = new ArrayList<>();
        this.likeList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        mediaList.add(storyMedia);
    }
    public Story(List<StoryMedia> mediaList){
        this.mediaList = mediaList;
        this.likeList = new ArrayList<>();
        this.commentList = new ArrayList<>();
    }

    protected Story(Parcel in) {
        writer = in.readString();
        mediaList = in.createTypedArrayList(StoryMedia.CREATOR);
        content = in.readString();
        reportingDate = in.readString();
        storyId = in.readString();
        likeList = in.createTypedArrayList(Like.CREATOR);
        commentList = in.createTypedArrayList(Comment.CREATOR);
    }

    /**
     * 댓글을 추가한다.
     */
    public void addComment(Comment comment){
        commentList.add(comment);
        Log.d("등록 정보 : ",comment.toString());
        Collections.sort(commentList,commentCompare);

    }

    /**
     * 댓글을 삭제한다.
     */
    public void deleteComment(){

    }

    /**
     * 댓글을 수정한다.
     */
    public void updateComment(){

    }

    /**
     * 사용 가능한 parentIndex를 리턴한다.
     * @return
     */
    public int getAvailableParentIndex(){
        if(commentList == null || commentList.size() == 0 ) return 0;
        Collections.sort(commentList,commentCompare);
        return commentList.get(commentList.size()-1).getParentIndex()+1;
    }

    /**
     * 사용 가능한 childIndex를 리턴한다.
     * @param parentIndx
     * @return
     */
    public int getAvailableChildIndex(int parentIndx){
        if(commentList == null || commentList.size() == 0 ) return 0;
        int result = -1;
        for(Comment each : commentList){
            if(each.getParentIndex() == parentIndx){
                result = each.getChildIndex();
            }
        }

        return result+1;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(writer);
        dest.writeTypedList(mediaList);
        dest.writeString(content);
        dest.writeString(reportingDate);
        dest.writeString(storyId);
        dest.writeTypedList(likeList);
        dest.writeTypedList(commentList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    public void addMedia(StoryMedia storyMedia){
        mediaList.add(storyMedia);
    }
    /**
     * 좋아요 개수 반환
     * @return
     */
    public int getLikeCount(){
        return likeList.size();
    }


    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setMediaList(List<StoryMedia> mediaList) {
        this.mediaList = mediaList;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getWriter() {
        return writer;
    }

    public List<StoryMedia> getMediaList() {
        return mediaList;
    }

    public String getContent() {
        return content;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public String getStoryId() {
        return storyId;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    private CommentCompare commentCompare = new CommentCompare();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story story = (Story) o;
        return storyId.equals(story.storyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId);
    }

    public List<Comment> getCommentList() {
        Collections.sort(commentList, commentCompare);
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        Collections.sort(commentList, commentCompare);
    }

    /**
     * 댓글 정렬을 위한 클래스
     */
    class CommentCompare implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            if(o1.getParentIndex() > o2.getParentIndex()) {
                return 1;
            }else if(o1.getParentIndex() < o2.getParentIndex()) {
                return -1;
            }else {
                if(o1.getChildIndex() > o2.getChildIndex()) {
                    return 1;
                }else if(o1.getChildIndex() < o2.getChildIndex()) {
                    return -1;
                }else {
                    return 0;
                }
            }
        }
    }


}

