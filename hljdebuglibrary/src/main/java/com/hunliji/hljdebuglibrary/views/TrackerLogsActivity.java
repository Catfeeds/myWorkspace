package com.hunliji.hljdebuglibrary.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljdebuglibrary.HljDebuger;
import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;
import com.hunliji.hljdebuglibrary.adapters.TrackerLogsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2018/3/1.
 */

@Route(path = RouterPath.IntentPath.Debug.TRACKER_LOG_LIST)
public class TrackerLogsActivity extends HljBaseActivity {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    private List<String> logs;
    private TrackerLogsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_logs);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }


    private void initValues() {
        logs = HljDebuger.getTrackers();
        adapter = new TrackerLogsAdapter(logs);
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        setOkText("Clear");
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        logs.clear();
        adapter.notifyDataSetChanged();
        onBackPressed();
    }
}
