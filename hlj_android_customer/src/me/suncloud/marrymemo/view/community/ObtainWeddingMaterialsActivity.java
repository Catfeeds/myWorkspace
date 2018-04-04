package me.suncloud.marrymemo.view.community;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityMaterial;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.ObtainWeddingMaterialAdapter;
import me.suncloud.marrymemo.adpter.community.viewholder.ObtainMaterialViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.community.ObtainCommunityMaterial;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import rx.Observable;
import rx.functions.Func2;

/**
 * 领取备婚资料activity
 * Created by jinxin on 2018/3/15 0015.
 */

public class ObtainWeddingMaterialsActivity extends HljBaseNoBarActivity implements
        PullToRefreshBase.OnRefreshListener, ObtainMaterialViewHolder.OnObtainClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;

    private ObtainWeddingMaterialAdapter materialAdapter;
    private HljHttpSubscriber refreshSub;
    private ShareInfo shareInfo;
    private View footerView;
    private City city;
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_wedding_material);
        ButterKnife.bind(this);

        initValues();
        initWidget();
        initLoad();
        initTracker();
    }

    private void initValues() {
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        city = Session.getInstance()
                .getMyCity(this);
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null, false);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        materialAdapter = new ObtainWeddingMaterialAdapter(this);
        materialAdapter.setFooterView(footerView);
        materialAdapter.setOnObtainClickListener(this);
    }

    private void initWidget() {
        setDefaultStatusBarPadding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(materialAdapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        onRefresh(null);
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnShare2)
                .tagName(HljTaggerName.BTN_SHARE)
                .hitTag();
    }

    @OnClick(R.id.btn_share2)
    void onShare() {
        ShareDialogUtil.onCommonShare(this, shareInfo);
    }

    @OnClick(R.id.btn_back2)
    void onBack() {
        onBackPressed();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(this)
                .setEmptyView(emptyView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<MaterialResultZip>() {
                    @Override
                    public void onNext(MaterialResultZip materialResultZip) {
                        setResultZip(materialResultZip);
                    }
                })
                .build();
        Observable<PosterData> posterObb = CommonApi.getBanner(this,
                HljCommon.BLOCK_ID.COMMUNITY_OBTAIN_MATERIAL,
                city == null ? 0 : city.getId());
        Observable<ObtainCommunityMaterial> materialObb = CommunityApi.ObtainMaterialList();
        Observable.zip(posterObb,
                materialObb,
                new Func2<PosterData, ObtainCommunityMaterial, Object>() {
                    @Override
                    public Object call(
                            PosterData posterData,
                            ObtainCommunityMaterial obtainCommunityMaterial) {
                        MaterialResultZip zip = new MaterialResultZip();
                        zip.poster = posterData;
                        zip.material = obtainCommunityMaterial;
                        return zip;
                    }
                })
                .subscribe(refreshSub);

    }

    private void setResultZip(MaterialResultZip resultZip) {
        setMaterial(resultZip.material);
        setPoster(resultZip.poster);
    }

    private void setPoster(PosterData posterData) {
        if (posterData != null && posterData.getFloors() != null) {
            List<Poster> posterList = PosterUtil.getPosterList(posterData.getFloors(),
                    HljCommon.POST_SITES.GET_WEDDING_MATERIAL_BANNER,
                    false);
            if (!CommonUtil.isCollectionEmpty(posterList)) {
                materialAdapter.setPoster(posterList.get(0));
            }
        }
    }

    private void setMaterial(ObtainCommunityMaterial obtainCommunityMaterial) {
        if (obtainCommunityMaterial == null || CommonUtil.isCollectionEmpty
                (obtainCommunityMaterial.getList())) {
            recyclerView.setVisibility(View.GONE);
            emptyView.showEmptyView();
            return;
        }
        emptyView.hideEmptyView();
        recyclerView.setVisibility(View.VISIBLE);
        shareInfo = obtainCommunityMaterial.getShareInfo();
        List<CommunityMaterial> materialList = obtainCommunityMaterial.getList();
        materialAdapter.setMaterialList(materialList);
        materialAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }

    @Override
    public void onObtain(final CommunityMaterial material) {
        if (material == null || material.getStatus() != CommunityMaterial.COMPLETED) {
            return;
        }
        String content = getResources().getString(R.string.label_material_content,
                material.getLink(),
                material.getPassword());
        DialogUtil.createDoubleButtonDialog(this,
                "请输入领取地址",
                content,
                "复制",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string
                                        .app_name),
                                material.getLink()));
                        Util.showToast(ObtainWeddingMaterialsActivity.this,
                                getString(R.string.hint_code_copy, material.getLink()),
                                0);
                    }
                },
                null)
                .show();
    }

    class MaterialResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData poster;
        @HljRZField
        ObtainCommunityMaterial material;
    }
}
