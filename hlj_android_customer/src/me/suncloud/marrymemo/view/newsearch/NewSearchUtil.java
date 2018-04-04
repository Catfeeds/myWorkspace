package me.suncloud.marrymemo.view.newsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import me.suncloud.marrymemo.R;

/**
 * Created by hua_rong on 2018/1/11
 * 新搜索工具类
 */

public class NewSearchUtil {

    public static final String CARD = "电子请帖";

    /**
     * 发起搜索前的筛选器
     *
     * @param activity Context Activity
     * @param keyword  关键词
     * @return
     */
    public static boolean beforeSearchFilter(Activity activity, String keyword) {
        if (CARD.equals(keyword)) {
            // 电子请帖，直接跳转电子请帖首页
            Intent intent = new Intent(activity, CardListActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            return true;
        }
        return false;
    }

    /**
     * @param context
     * @param keyword
     * @param category
     */
    public static void performSearchResultJump(
            Context context, String keyword, String category) {
        if (context != null && !TextUtils.isEmpty(keyword) && NewSearchApi.getSearchType
                (category) != null) {
            Intent intent = new Intent(context, NewSearchResultActivity.class);
            intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE, NewSearchApi.getSearchType(category));
            intent.putExtra(NewSearchApi.ARG_KEY_WORD, keyword);
            context.startActivity(intent);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

}
