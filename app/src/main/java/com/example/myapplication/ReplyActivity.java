package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.Comment;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.ClearEditText;
import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.ReplyAdapter;


/**
 * 클래스 명 : ReplyActivity class
 * 설명 : 댓글 화면을 나타낸다. 답글 달기가 가능하다.
 */
public class ReplyActivity extends AppCompatActivity {

    final String TAG = getClass().getSimpleName();

    // activity_reply 레이아웃 위젯
    private ReplyAdapter replyAdapter;
    private ListView listView;
    private ImageView userThumbnail;
    private ClearEditText replyTxt;
    private LinearLayout replyAlert;
    private TextView replyAlertText;
    private ImageView replyAlertBtn;
    private ImageView backBtn;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체
    private Story story;
    private ConstraintLayout replyLayout;
    // 답글, 일반 댓글 구분
    private int commentParent = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        story = getIntent().getParcelableExtra("story");
//        Comment comment1 = new Comment("1",0,0,"1","writer1","2019-03=01");
//        Comment comment2 = new Comment("1",0,1,"2","writer1","2019-03=01");
//        Comment comment3 = new Comment("1",1,0,"3","writer1","2019-03=01");
//        Comment comment4 = new Comment("1",1,1,"4","writer1","2019-03=01");
//        Comment comment5 = new Comment("1",2,0,"5","writer1","2019-03=01");
//        List<Comment> a= new ArrayList<>();
//        a.add(comment1);
//        a.add(comment2);
//        a.add(comment3);
//        a.add(comment4);
//        a.add(comment5);
//        story.setCommentList(a);
        initView();
        initListView();
        replyTxt.setOnclickSendEvent(onclickSendEvent);

        /* 클릭 이벤트 정의 */
        backBtn.setOnClickListener(new View.OnClickListener() { // 뒤로가기 버튼 클릭시 홈메뉴로 이동한다.
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        replyAlertBtn.setOnClickListener(new View.OnClickListener() { // 답글하기 버튼 클릭시 동작한다.
            @Override
            public void onClick(View view) {
                replyAlert.setVisibility(View.GONE);
                commentParent = -1;
            }
        });

        replyLayout = findViewById(R.id.replyLayout);
        replyLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("올라온다.","가즈아");
            }
        });
    }

    // 종이비행기 버튼 클릭시 댓글을 저장한다.
    ClearEditText.onclickSendEvent onclickSendEvent = new ClearEditText.onclickSendEvent() {
        @Override
        public void onclickSendBtn() {
            saveReply();
        }
    };

    /**
     * 홈메뉴로 이동한다.
     */
    public void goHome(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("user", user);
        intent.putExtra("storyManager", storyManager);
        intent.putExtra("userManager", userManager);
        intent.putExtra("story", story);
        startActivity(intent);
    }

    /**
     * 댓글, 답글을 구분해 저장한다.
     */
    public void saveReply(){
        String content = replyTxt.getText().toString();
        Comment comment = null;

        /* 일반 댓글과 답글을 구분해 Comment 저장을 분기 처리한다. */
        if(commentParent != -1){
            int childIndex = story.getAvailableChildIndex(commentParent);
            comment = new Comment(
                    user.getUserId()+"_"+commentParent+"_"+childIndex
                    , commentParent,childIndex,content
                    , user.getUserId(),DateUtil.getToday());
        }else{
            int parentIndex = story.getAvailableParentIndex();
            comment = new Comment(
                    user.getUserId()+"_"+parentIndex+"_"+0
                    , parentIndex,0,content
                    , user.getUserId(),DateUtil.getToday());
        }

        story.addComment(comment);
        storyManager.updateStory(story);
        replyAdapter.setData(story);
        replyAdapter.notifyDataSetChanged();
        replyTxt.setText("");
        replyAlert.setVisibility(View.GONE);
        commentParent = -1;

    }

    /**
     * 위젯 초기화
     */
    public void initView(){
        listView = findViewById(R.id.replyListView);
        userThumbnail= findViewById(R.id.userThumbnail);
        replyTxt = findViewById(R.id.replyTxt);
        replyAlert = findViewById(R.id.replyAlert);
        replyAlertText = findViewById(R.id.replyAlertText);
        replyAlertBtn = findViewById(R.id.replyAlertBtn);
        backBtn = findViewById(R.id.backBtn);

        Glide.with(getApplicationContext())
                .load(user.getThumbnailPath())
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.basic)
                .into(userThumbnail);
//        if(!TextUtils.isEmpty(user.getThumbnailPath())){
//            Glide.with(getApplicationContext())
//                    .load(user.getThumbnailPath())
//                    .apply(RequestOptions.circleCropTransform())
//                    .error(R.drawable.basicjpg)
//                    .into(userThumbnail);
//            userThumbnail.setImageBitmap(FileUtil.createBitmapAsPath(user.getThumbnailPath()));
//        }
    }

    /**
     * 리스트 뷰 셋팅
     */
    public void initListView(){
        replyAdapter = new ReplyAdapter(story, userManager);
        listView.setAdapter(replyAdapter);
        replyAdapter.setOnClickItemListener(onClickItemListener);
    }

    /**
     * 답글 하기 버튼 클릭시 이벤트 정의
     */
    public ReplyAdapter.OnClickItemListener onClickItemListener = new ReplyAdapter.OnClickItemListener() {
        @Override
        public void onclickReply(int position) {
            Comment comment = replyAdapter.getCommentList().get(position);
            String writerName = userManager.getUser(comment.getWriter()).getUserName();
            replyAlertText.setText(writerName+"님에게 답글 남기는 중");
            replyAlert.setVisibility(View.VISIBLE);
            commentParent = comment.getParentIndex();
        }
    };
}
