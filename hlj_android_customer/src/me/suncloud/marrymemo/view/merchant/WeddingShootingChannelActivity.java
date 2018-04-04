package me.suncloud.marrymemo.view.merchant;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;

import me.suncloud.marrymemo.fragment.work_case.WeddingShootingFragment;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼摄像
 */
public class WeddingShootingChannelActivity extends BaseMerchantServiceChannelActivity {
    private String titles[] = {"精选", "单机位", "双机位", "多机位", "航拍", "摄影摄像"};
    private String keywords[] = {"", "单机位", "双机位", "多机位", "航拍", "摄影"};//婚礼摄像中的摄影
    private String subTitles[] = {"精品推荐", "性价比高", "细节丰富", "精致完美", "梦幻/大气", "省心/优惠"};


    @Override
    public String pageTrackTagName() {
        return "婚礼摄像二级页";
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
        return Merchant.PROPERTY_WEDDING_SHOOTING;
    }

    @Override
    public ScrollAbleFragment getFragment(int position) {
        return WeddingShootingFragment.newInstance(keywords[position]);
    }

}

