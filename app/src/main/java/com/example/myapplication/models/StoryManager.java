package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 클래스 명 : StoryManager class
 * 설명 :  앱에 등록된 모든 스토리 정보를 나타내고 관리하는 클래스이다.
 */
public class StoryManager implements Parcelable {
    private final String TAG = getClass().getSimpleName();
    private List<Story> storyList;

    public StoryManager(){
        storyList = new ArrayList<>();
    }

    public int getStoryCount(){
        return storyList.size();
    }

    /**
     * 팔로잉한 유저들이 올린 스토리가 몇개가 있는지 알려준다.
     * @param followUserIdList
     * @return
     */
    public int getFollowStoryCount(String followUserIdList){
        int count = 0;
        for(Story each : storyList){
            if(each!= null && followUserIdList.contains(each.getWriter())){
                count++;
            }
        }
        return count;
    }


    public void addStory(Story story){
        storyList.add(story);
    }

    public void updateStory(Story story){
        int index = 0;
        for(Story each : storyList){
            if(each!= null && each.equals(story)){
                storyList.set(index,story);

                Log.d(TAG,"updateStory 완료");
                break;
            }
            index++;
        }
        Log.d(TAG,"updateStory 실패");
    }

    public void deleteStory(Story story){
        for(Story each : storyList){
            if(each!= null && each.equals(story)){
                storyList.remove(each);
                Log.d(TAG,"deleteStory 완료");
                break;
            }
        }
        Log.d(TAG,"deleteStory 실패");
    }

    public List<Story> getHomeStoryList(String followUserId){
        List<Story> homeStoryList = new ArrayList<>();
        for(Story each : storyList){
            if(each!= null && followUserId.contains(each.getWriter())){
                homeStoryList.add(each);
            }
        }
        Collections.reverse(homeStoryList);
        return homeStoryList;
    }

    protected StoryManager(Parcel in) {
        storyList = in.createTypedArrayList(Story.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(storyList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoryManager> CREATOR = new Creator<StoryManager>() {
        @Override
        public StoryManager createFromParcel(Parcel in) {
            return new StoryManager(in);
        }

        @Override
        public StoryManager[] newArray(int size) {
            return new StoryManager[size];
        }
    };
}
