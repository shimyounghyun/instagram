package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

    /**
     * 댓글 고유 식별 문자
     */
    private String commentIndex;
    /**
     * 댓글 순서
     */
    private int parentIndex;

    /**
     * 댓글의 댓글 순서
     */
    private int childIndex;

    /**
     * 댓글 내용
     */
    private String content;

    /**
     * 작성자 아이디
     */
    private String writer;

    /**
     * 작성일
     */
    private String reportingDate;


    public Comment(String commentIndex, int parentIndex, int childIndex, String content, String writer, String reportingDate) {
        this.commentIndex = commentIndex;
        this.parentIndex = parentIndex;
        this.childIndex = childIndex;
        this.content = content;
        this.writer = writer;
        this.reportingDate = reportingDate;
    }

    protected Comment(Parcel in) {
        commentIndex = in.readString();
        parentIndex = in.readInt();
        childIndex = in.readInt();
        content = in.readString();
        writer = in.readString();
        reportingDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentIndex);
        dest.writeInt(parentIndex);
        dest.writeInt(childIndex);
        dest.writeString(content);
        dest.writeString(writer);
        dest.writeString(reportingDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public void setCommentIndex(String commentIndex) {
        this.commentIndex = commentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getCommentIndex() {
        return commentIndex;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentIndex='" + commentIndex + '\'' +
                ", parentIndex=" + parentIndex +
                ", childIndex=" + childIndex +
                ", content='" + content + '\'' +
                '}';
    }
}
