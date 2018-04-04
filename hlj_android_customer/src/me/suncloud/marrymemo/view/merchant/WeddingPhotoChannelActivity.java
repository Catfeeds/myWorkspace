package me.suncloud.marrymemo.view.merchant;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;

import me.suncloud.marrymemo.fragment.work_case.WeddingPhotoFragment;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼摄影
 */

public class WeddingPhotoChannelActivity extends BaseMerchantServiceChannelActivity {
    private String titles[] = {"精选", "单机位", "双机位", "多机位", "摄影摄像"};
    private String keywords[] = {"", "单机位", "双机位", "多机位", "摄像"};//婚礼摄影中的摄像
    private String subTitles[] = {"精品推荐", "性价比高", "细节丰富", "精致完美", "省心/优惠"};

    @Override
    public String pageTrackTagName() {
        return "婚礼摄影二级页";
    }

    @Override
    public void initValues() {
        super.initValues();
        for (int i = 0, size = titles.length; i < size; i++) {
            CategoryMark categoryMark = new CategoryMark();
            Mark mark = new Mark();
            mark.setName(titles[i]);
            mark.setDescribe(subTitles[i]);
            categoryMark.setMark(mark);
            categoryMarks.add(categoryMark);
        }
    }

    @Override
    public long getPropertyId() {
        return Merchant.PROPERTY_WEDDING_PHOTO;
    }

    @Override
    public ScrollAbleFragment getFragment(int position) {
        return WeddingPhotoFragment.newInstance(keywords[position]);
    }
}
