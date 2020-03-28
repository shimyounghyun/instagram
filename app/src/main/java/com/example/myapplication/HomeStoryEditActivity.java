package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.LinePagerIndicatorDecoration;
import com.example.myapplication.utils.MediaAdapter;

/**
 * 클래스 명 : HomeStoryEditActivity class
 * 설명 : 작성한 게시물을 수정하는 화면이다.
 */
public class HomeStoryEditActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체
    private Story story; // 현재 activity에서 사용되는 객체

    // activity_home_story_edit 레이아웃 위젯
    private TextView writer;
    private ImageView writerThumbnail;
    private EditText content;
    private TextView saveBtn;
    private TextView cancelBtn;
    private RecyclerView mediaRecyclerView;
    private MediaAdapter mediaAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_story_edit);

        /* 넘어온 값 저장 */
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        story = getIntent().getParcelableExtra("story");

        /* view 초기화 */
        initView();
        setView();
        initRecyclerView();

        /* 버튼 이벤트 정의 */
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStory();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 위젯 초기화
     */
    public void initView(){
        writer = findViewById(R.id.writer);
        writerThumbnail = findViewById(R.id.writerThumbnail);
        content = findViewById(R.id.content);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        mediaRecyclerView = findViewById(R.id.mediaRecyclerView);
    }

    /**
     * 현재 정보를 위젯에 셋팅
     */
    public void setView(){
        writer.setText(user.getUserName());
        content.setText(story.getContent());
        if(!TextUtils.isEmpty(user.getThumbnailPath())){
            writerThumbnail.setImageBitmap(FileUtil.createBitmapAsPath(user.getThumbnailPath()));
        }
    }

    /**
     * 미디어 목록 리싸이클러뷰 초기화
     */
    public void initRecyclerView(){
        mediaAdapter = new MediaAdapter();
        mediaAdapter.setMediaList(story.getMediaList());
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()
                ,RecyclerView.HORIZONTAL,false));
        mediaRecyclerView.setAdapter(mediaAdapter);
        mediaRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration());

        /* api 25 이상 리싸이클러뷰에서 페이징 기능이 가능 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(this.mediaRecyclerView);
    }

    /**
     * 수정한 게시물을 저장한다.
     */
    public void saveStory(){
        Story updateStory = new Story();

        // 위젯, 기존 story에서 정보 가져오기
        String storyId = story.getStoryId();
        String storyContent = content.getText().toString();
        String reportingDate = story.getReportingDate();

        // 새 story에 복사
        updateStory.setContent(storyContent);
        updateStory.setStoryId(storyId);
        updateStory.setWriter(user.getUserId());
        updateStory.setReportingDate(reportingDate);
        updateStory.setMediaList(story.getMediaList());
        // 새 story 반영
        user.updateUserStory(updateStory);
        storyManager.updateStory(updateStory);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("user", user);
        intent.putExtra("storyManager", storyManager);
        intent.putExtra("userManager", userManager);
        intent.putExtra("story", updateStory);
        startActivity(intent);
    }


}
