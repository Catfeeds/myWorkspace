package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.Story;

public class StoryPreviewActivity extends BaseStoryActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        findViewById(R.id.review).setVisibility(View.GONE);
        findViewById(R.id.collect).setVisibility(View.GONE);
        findViewById(R.id.share).setVisibility(View.GONE);
        Story story = (Story) getIntent().getSerializableExtra("story");
        ArrayList<Item> nitems = (ArrayList<Item>) getIntent().getSerializableExtra("items");
        if (nitems != null && !nitems.isEmpty()) {
            items.addAll(nitems);
        }
        showStory(story);
        adapter.notifyDataSetChanged();
    }

}