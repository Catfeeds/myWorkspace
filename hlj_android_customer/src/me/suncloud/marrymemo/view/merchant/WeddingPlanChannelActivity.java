package me.suncloud.marrymemo.view.merchant;

import android.os.Bundle;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.fragment.work_case.WeddingPlanWorkListFragment;

/**
 * 婚礼策划频道页
 * Created by chen_bin on 2017/7/28 0028.
 */
public class WeddingPlanChannelActivity extends BaseMerchantServiceChannelActivity {
    private HljHttpSubscriber initSubscriber;

    @Override
    public String pageTrackTagName() {
        return "婚礼策划二级页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CategoryMark>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CategoryMark>> listHljHttpData) {
                            CategoryMark categoryMark = new CategoryMark();
                            Mark mark = new Mark();
                            mark.setName("精选");
                            mark.setDescribe("为你推荐");
                            categoryMark.setMark(mark);
                            categoryMarks.add(categoryMark);
                            categoryMarks.addAll(listHljHttpData.getData());
                            setFragments();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantApi.getMarkCategoryServiceHeadObb(getPropertyId())
                    .subscribe(initSubscriber);
        }
    }

    @Override
    public long getPropertyId() {
        return Merchant.PROPERTY_WEDDING_PLAN;
    }

    @Override
    public ScrollAbleFragment getFragment(int position) {
        return WeddingPlanWorkListFragment.newInstance(getPropertyId(),
                categoryMarks.get(position)
                        .getId(),
                categoryMarks.get(position)
                        .getMark()
                        .getName());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}