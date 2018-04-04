package com.hunliji.hljdebuglibrary.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;
import com.hunliji.hljhttplibrary.models.InterceptorLogger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JsonViewerActivity extends HljBaseActivity {

    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_viewer);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        msg = getIntent().getStringExtra("msg");
    }

    private void initViews() {
        setTitle("Log Viewer");
        String[] strArray = msg.split(InterceptorLogger.LINE_BREAK);

        contentLayout.removeAllViews();
        for (final String str : strArray) {
            if (str.startsWith("{")) {
                // json格式
                View view = LayoutInflater.from(this)
                        .inflate(R.layout.json_text_view, null, false);
                TextView tvMsg = view.findViewById(R.id.tv_msg);
                tvMsg.setText(Html.fromHtml(str));
                view.findViewById(R.id.tv_copy)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager cmb = (ClipboardManager) getSystemService(
                                        CLIPBOARD_SERVICE);
                                cmb.setPrimaryClip(ClipData.newPlainText("", str));
                                Toast.makeText(JsonViewerActivity.this, "复制成功", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                contentLayout.addView(view);
            } else {
                // txt格式
                TextView tvMsg = (TextView) LayoutInflater.from(this)
                        .inflate(R.layout.plain_text_view, null, false);
                tvMsg.setText(str);
                contentLayout.addView(tvMsg);
            }
        }
    }
}
