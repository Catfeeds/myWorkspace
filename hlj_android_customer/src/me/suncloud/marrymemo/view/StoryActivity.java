package me.suncloud.marrymemo.view;


import android.os.Bundle;

import com.hunliji.hljtrackerlibrary.HljTracker;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.TrackerUtil;

public class StoryActivity extends BaseStoryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        long id = getIntent().getLongExtra("id", 0);
        new HljTracker.Builder(this)
                .eventableId(id)
                .eventableType("Story")
                .screen("story_detail_page")
                .action("hit")
                .build()
                .add();
        new GetStoryInfo().executeOnExecutor(Constants.INFOTHEADPOOL, Constants.getAbsUrl(String.format(Constants.HttpPath.STORY_INFO_URL, id)));
    }

}