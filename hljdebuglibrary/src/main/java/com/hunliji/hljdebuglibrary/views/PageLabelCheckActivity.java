package com.hunliji.hljdebuglibrary.views;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;
import com.hunliji.hljdebuglibrary.adapters.ActivityNameAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2018/3/28.
 */
@Route(path = RouterPath.IntentPath.Debug.PAGE_LABEL_CHECK)
public class PageLabelCheckActivity extends HljBaseActivity {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    List<String> pageNames;
    private ActivityNameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_logs);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        pageNames = new ArrayList<>();
        try {
            PackageInfo appInfo = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            CharSequence defaultName = getPackageManager().getApplicationLabel(getApplicationInfo
                    ());
            if (appInfo.activities != null) {
                for (ActivityInfo activityInfo : appInfo.activities) {
                    String name=activityInfo.name;
                    if(!name.contains("hunliji")&&!name.contains("suncloud")){
                        continue;
                    }
                    CharSequence c = activityInfo.loadLabel(this.getPackageManager());
                    if (c == null || c.equals(defaultName)) {
                        pageNames.add(activityInfo.name);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        adapter = new ActivityNameAdapter(pageNames);
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

}
