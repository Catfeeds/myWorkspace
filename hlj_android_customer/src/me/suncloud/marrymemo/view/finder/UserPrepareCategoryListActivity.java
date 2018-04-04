package me.suncloud.marrymemo.view.finder;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.UserPrepareCategoryListAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.finder.UserPrepareCategory;
import me.suncloud.marrymemo.util.Session;

/**
 * 备婚分类列表-dialog形式的activity
 * Created by chen_bin on 2017/10/17 0017.
 */
public class UserPrepareCategoryListActivity extends Activity implements
        OnItemClickListener<UserPrepareCategory> {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private UserPrepareCategoryListAdapter adapter;
    private HljHttpSubscriber saveSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prepare_category_list);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        User user = Session.getInstance()
                .getCurrentUser(this);
        tvName.setText("Hi," + user.getNick());
    }

    private void initViews() {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new UserPrepareCategoryListAdapter(this, getCategories());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, UserPrepareCategory category) {
        if (category != null) {
            category.setSelected(!category.isSelected());
            adapter.notifyItemChanged(position);
        }
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        onBackPressed();
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        List<UserPrepareCategory> categories = adapter.getCategories();
        if (CommonUtil.isCollectionEmpty(categories)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UserPrepareCategory category : categories) {
            if (category.isSelected()) {
                sb.append(category.getId())
                        .append(",");
            }
        }
        if (TextUtils.isEmpty(sb)) {
            ToastUtil.showToast(this, "请在选择后提交", 0);
            return;
        }
        if (sb.lastIndexOf(",") > 0) {
            sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
        }
        CommonUtil.unSubscribeSubs(saveSub);
        saveSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        NotePrefUtil.getInstance(UserPrepareCategoryListActivity.this)
                                .setUserPrepareSaved(true);
                        onBackPressed();
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        FinderApi.saveUserPrepareObb(sb.toString())
                .subscribe(saveSub);
    }

    private List<UserPrepareCategory> getCategories() {
        InputStream in = getResources().openRawResource(R.raw.user_prepare_categories);
        String json = CommonUtil.readStreamToString(in);
        return GsonUtil.getGsonInstance()
                .fromJson(json, new TypeToken<List<UserPrepareCategory>>() {}.getType());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(saveSub);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(saveSub);
        }
    }
}
