package com.example.myapplication.models;

import android.icu.text.Edits;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 클래스 명 : User class
 * 설명 : 회원 정보를 나타내는 클래스이다.
 *
 */
public class User implements Parcelable {
    private final String TAG = getClass().getSimpleName();

    private String userId;
    private String password;
    private String phoneNum;
    private String userName;
    private String userSex;
    private String userSite;
    private List<User> followingList;
    private int followers;
    private String thumbnailPath;
    private List<Story> storyList;

    public User(String userId, String phoneNum, String userName, String password) {
        this.userId = userId;
        this.phoneNum = phoneNum;
        this.userName = userName;
        this.password = password;
        this.followingList = new ArrayList<>();
        this.followers = 0;
        this.storyList = new ArrayList<>();
    }


    protected User(Parcel in) {
        userId = in.readString();
        password = in.readString();
        phoneNum = in.readString();
        userName = in.readString();
        userSex = in.readString();
        userSite = in.readString();
        followingList = in.createTypedArrayList(User.CREATOR);
        followers = in.readInt();
        thumbnailPath = in.readString();
        storyList = in.createTypedArrayList(Story.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(password);
        dest.writeString(phoneNum);
        dest.writeString(userName);
        dest.writeString(userSex);
        dest.writeString(userSite);
        dest.writeTypedList(followingList);
        dest.writeInt(followers);
        dest.writeString(thumbnailPath);
        dest.writeTypedList(storyList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getStoryCount(){
        return storyList.size();
    }

    /**
     * 팔로잉 리스트의 유저 아이디목록을 문자열로 반환한다.
     * @return
     */
    public String getFollowUserListStr(){
        String userIdList = "";
        int lastIndex = followingList.size();
        int index = 1;
        for(User each : followingList){
            userIdList += each.getUserId();
            if(index < lastIndex) userIdList += "|";
            index++;
        }
        return userIdList;
    }

    /**
     * 비밀번호 일치 여부를 확인한다.
     * @param password : 사용자 입력 비밀번호
     * @return true : 계정의 비밀번호와 일치, false : 불일치
     */
    public boolean checkPassword(String password){
        boolean result = false;
        if(!TextUtils.isEmpty(password) && password.equals(password)){
            result = true;
        }
        return result;
    }

    /**
     * 팔로우 수를 반환한다.
     * @return int : follower list 크기
     */
    public int getFollowCount(){
        return followingList.size();
    }

    /**
     * 팔로우 목록에 추가한다.
     * @param user : 추가할 팔로우 유저 아이디
     */
    public void addFollowUser(User user){

        if(TextUtils.isEmpty(userId) && checkDupFollowId(user)){
            Log.d(TAG,"addFollowUser - 추가할 수 없습니다.");
            return;
        }
        followingList.add(user);
    }

    public void addFollowUser(String userId){
        addFollowUser(
                getFollowingUser(userId)
        );
    }

    /**
     * 팔로우 목록에서 삭제한다.
     */
    public void deleteFollowUser(User user){
        for(User each : followingList){
            if(each.equals(user)){
                followingList.remove(user);
                return;
            }
        }
    }

    public void deleteFollowUser(String userId){
        deleteFollowUser(
            getFollowingUser(userId)
        );
    }
    /**
     * 팔로잉 목록에서 유저를 꺼낸다.
     * @param userId
     * @return
     */
    public User getFollowingUser(String userId){
        User user = null;
        for(User each : followingList){
            if(each.getUserId().equals(userId)){
                user = each;
                break;
            }
        }
        return user;
    }

    /**
     * 소유한 팔로우 목록에 인자로 넘어온 아이디가 중복됐는지 확인한다.
     * @return
     */
    public boolean checkDupFollowId(User user){
        for(User each : followingList){
            if(each.equals(user)) return false;
        }
        return true;
    }

    /**
     * 피드를 추가한다.
     * @param story
     */
    public void addUserStory(Story story){
        storyList.add(story);
    }

    public void updateUserStory(Story story){
        int index = 0;
        for(Story each : storyList ){
            if(each != null && each.equals(story)){
                storyList.set(index,story);
                Log.d(TAG,"updateUserStory : 유저 Story 수정 성공");
            }
            index++;
        }
        Log.d(TAG,"updateUserStory : 유저 Story 수정 실패");
    }

    public void deleteUserStory(String storyId){
        Iterator<Story> iterator = storyList.iterator();
        while(iterator.hasNext()){
            Story each = iterator.next();
            if(each.getStoryId().equals(storyId)){
                iterator.remove();
                Log.d(TAG,"deleteUserStory : 유저 Story 삭제 성공");
            }
        }
        Log.d(TAG,"deleteUserStory : 유저 Story 삭제 실패");
    }

    public String getUserId() {
        return userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() { return password; }


    public String getUserSex() {
        return userSex;
    }

    public String getUserSite() {
        return userSite;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserSite(String userSite) {
        this.userSite = userSite;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowers() {
        return followers;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }


    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public List<Story> getStoryList() {
        return storyList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
