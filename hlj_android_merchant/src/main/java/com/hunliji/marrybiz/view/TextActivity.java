package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;

/**
 * Created by Suncloud on 2016/5/10.
 */
public class TextActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        TextView textView = (TextView) findViewById(R.id.text);
        String text = getIntent().getStringExtra("text");
        textView.setText(text);
    }
}
