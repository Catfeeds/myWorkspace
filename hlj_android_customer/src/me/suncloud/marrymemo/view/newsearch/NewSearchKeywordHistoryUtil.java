package me.suncloud.marrymemo.view.newsearch;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmError;
import me.suncloud.marrymemo.model.realm.NewHistoryKeyWord;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by werther on 16/12/2.
 * 搜索关键字存储帮助类
 */

public class NewSearchKeywordHistoryUtil {

    private static final int HISTORY_LIMIT = 15;

    /**
     * 存储关键字历史
     *
     * @param realm          数据库实例
     * @param distinctSub    外部维护实例的去重数据异步请求Subscriber
     * @param deleteLimitSub 外部维护实例的删除Subscriber
     * @param keyword        关键字
     * @return 返回两个需要在外部下回的Subscriber，
     */
    public static Subscriber[] saveKeywordToHistory(
            final Realm realm,
            Subscriber distinctSub,
            Subscriber deleteLimitSub,
            String category,
            final String keyword,
            long merchantId) {
        final NewHistoryKeyWord newKeyWord = new NewHistoryKeyWord();
        newKeyWord.setCategory(category);
        newKeyWord.setKeyword(keyword);
        newKeyWord.setMerchantId(merchantId);
        // 去重，关键词都相同的历史
        CommonUtil.unSubscribeSubs(distinctSub);
        distinctSub = new Subscriber<RealmResults<NewHistoryKeyWord>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RealmResults<NewHistoryKeyWord> searchHistoryWords) {
                // 找到了对应重复的关键词历史
                realm.beginTransaction();
                searchHistoryWords.deleteAllFromRealm();
                realm.copyToRealm(newKeyWord);
                realm.commitTransaction();
            }
        };
        try {
            realm.where(NewHistoryKeyWord.class)
                    .equalTo("keyword", keyword)
                    .equalTo("category", category)
                    .equalTo("merchantId", merchantId)
                    .findAllAsync()
                    .asObservable()
                    .filter(new Func1<RealmResults<NewHistoryKeyWord>, Boolean>() {
                        @Override
                        public Boolean call(RealmResults<NewHistoryKeyWord> newHistoryKeyWords) {
                            return newHistoryKeyWords.isLoaded();
                        }
                    })
                    .first()
                    .subscribe(distinctSub);
        } catch (RealmError e) {
            e.printStackTrace();
        }

        deleteOverLimitHistory(realm, deleteLimitSub);

        return new Subscriber[]{distinctSub, deleteLimitSub};
    }

    /**
     * 删除超过数量限制的历史记录
     */
    private static void deleteOverLimitHistory(
            final Realm realm, Subscriber deleteLimitSub) {
        CommonUtil.unSubscribeSubs(deleteLimitSub);
        deleteLimitSub = new Subscriber<RealmResults<NewHistoryKeyWord>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(RealmResults<NewHistoryKeyWord> searchHistoryWords) {
                if (searchHistoryWords.size() > HISTORY_LIMIT) {
                    // 超过限制，删除最早的数据
                    long newLeastId = searchHistoryWords.get(HISTORY_LIMIT - 1)
                            .getId();
                    realm.beginTransaction();
                    realm.where(NewHistoryKeyWord.class)
                            .lessThan("id", newLeastId)
                            .findAll()
                            .deleteAllFromRealm();
                    realm.commitTransaction();
                }
            }
        };
        try {
            realm.where(NewHistoryKeyWord.class)
                    .findAllSortedAsync("id", Sort.DESCENDING)
                    .asObservable()
                    .filter(new Func1<RealmResults<NewHistoryKeyWord>, Boolean>() {
                        @Override
                        public Boolean call(
                                RealmResults<NewHistoryKeyWord> newHistoryKeyWords) {
                            return newHistoryKeyWords.isLoaded();
                        }
                    })
                    .first()
                    .subscribe(deleteLimitSub);
        } catch (RealmError e) {
            e.printStackTrace();
        }
    }
}
