package me.suncloud.marrymemo.api.finder;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.subpage.MarkedKeyword;
import com.hunliji.hljcommonlibrary.models.subpage.SubPageCategoryMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljnotelibrary.models.NotebookType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.finder.CPMFeed;
import me.suncloud.marrymemo.model.finder.CaseCategories;
import me.suncloud.marrymemo.model.finder.EntityComment;
import me.suncloud.marrymemo.model.finder.FindTabConfig;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.model.finder.FinderFeedHistory;
import me.suncloud.marrymemo.model.wrappers.HljHttpMarksData;
import me.suncloud.marrymemo.model.wrappers.HljHttpSubPageCategoryMarksData;
import me.suncloud.marrymemo.model.wrappers.HljHttpTopicsData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public class FinderApi {

    /**
     * 获取发现页的8个标签
     *
     * @return
     */
    public static Observable<List<MarkedKeyword>> getMarkedKeywordsObb(
            int category, int type) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getMarkedKeywords(category, type)
                .map(new HljHttpResultFunc<HljHttpData<List<MarkedKeyword>>>())
                .map(new Func1<HljHttpData<List<MarkedKeyword>>, List<MarkedKeyword>>() {
                    @Override
                    public List<MarkedKeyword> call(
                            HljHttpData<List<MarkedKeyword>> listHljHttpData) {
                        return listHljHttpData == null ? null : listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 首页专题列表
     *
     * @param categoryId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpTopicsData> getSubPageListObb(
            long categoryId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageList(categoryId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpTopicsData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签专题列表
     *
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpMarksData<List<TopicUrl>>> getListByMarkIdObb(
            long markId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getListByMarkId(markId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpMarksData<List<TopicUrl>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签组专题列表
     *
     * @param id
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpMarksData<List<TopicUrl>>> getListByMarkGroupIdObb(
            long id, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getListByMarkGroupId(id, page, perPage)
                .map(new HljHttpResultFunc<HljHttpMarksData<List<TopicUrl>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 发现页-专题详情
     *
     * @param id
     * @return
     */
    public static Observable<TopicUrl> getSubPageDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageDetail(id)
                .map(new HljHttpResultFunc<TopicUrl>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 专题收藏列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<TopicUrl>>> getSubPageCollectListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageCollectList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<TopicUrl>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 专题热门评论列表
     *
     * @param entityId
     * @param entityType
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EntityComment>>> getSubPageHotCommentsObb(
            long entityId, String entityType, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageHotComments(entityId, entityType, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EntityComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 专题最新评论列表
     *
     * @param entityId
     * @param entityType
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EntityComment>>> getSubPageNewCommentsObb(
            long entityId, String entityType, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageNewComments(entityId, entityType, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EntityComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 推荐标签组列表
     *
     * @return
     */
    public static Observable<HljHttpSubPageCategoryMarksData> getSubPageCategoryMarksObb() {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageCategoryMarks()
                .map(new HljHttpResultFunc<HljHttpSubPageCategoryMarksData>())
                .map(new Func1<HljHttpSubPageCategoryMarksData, HljHttpSubPageCategoryMarksData>() {
                    @Override
                    public HljHttpSubPageCategoryMarksData call(
                            HljHttpSubPageCategoryMarksData categoryMarksData) {
                        if (categoryMarksData != null && !categoryMarksData.isEmpty()) {
                            List<SubPageCategoryMark> categoryMarks = categoryMarksData.getData();
                            SubPageCategoryMark categoryMark = new SubPageCategoryMark();
                            categoryMark.setImg(categoryMarksData.getRankingImg());
                            categoryMarks.add(0, categoryMark);
                        }
                        return categoryMarksData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 专栏排行榜
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<TopicUrl>>> getSubPageRanksObb(
            int type, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSubPageRanks(type, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<TopicUrl>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 推荐 1-3广告位
     *
     * @param num
     * @return
     */
    public static Observable<List<CPMFeed>> getFinderCPMsObb(int num) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getFinderCPMs(num)
                .map(new HljHttpResultFunc<List<CPMFeed>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存备婚信息
     *
     * @param option
     * @return
     */
    public static Observable saveUserPrepareObb(String option) {
        Map<String, Object> map = new HashMap<>();
        map.put("option", option);
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .saveUserPrepare(map)
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发现页-发现tab-精选/好评（笔记推荐列表）
     *
     * @param lastId
     * @param tab    (精选choice 好评favor)
     * @return
     */
    public static Observable<List<FinderFeed>> getFinderRecommendFeedsObb(long lastId, String tab) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getFinderRecommendFeeds(lastId, tab)
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, List<FinderFeed>>() {
                    @Override
                    public List<FinderFeed> call(HljHttpData<List<FinderFeed>> listHljHttpData) {
                        return listHljHttpData == null ? null : listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新数据
     *
     * @return
     */
    public static Observable<List<FinderFeed>> syncFinderRecommendFeedsObb(
            final List<FinderFeed> oFeeds, final List<FinderFeedHistory> histories) {
        StringBuilder sb = new StringBuilder();
        if (!CommonUtil.isCollectionEmpty(histories)) {
            for (int i = 0, size = histories.size(); i < size; i++) {
                FinderFeedHistory history = histories.get(i);
                sb.append(history.getType())
                        .append(":")
                        .append(history.getId());
                if (i < size - 1) {
                    sb.append(",");
                }
            }
        }
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .syncFinderRecommendFeeds(sb.toString())
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, List<FinderFeed>>() {
                    @Override
                    public List<FinderFeed> call(HljHttpData<List<FinderFeed>> listHljHttpData) {
                        List<FinderFeed> nFeeds = listHljHttpData == null ? null :
                                listHljHttpData.getData();
                        if (!CommonUtil.isCollectionEmpty(nFeeds)) {
                            if (!CommonUtil.isCollectionEmpty(oFeeds)) {
                                for (FinderFeed oFeed : oFeeds) {
                                    for (FinderFeed nFeed : nFeeds) {
                                        if (oFeed.getEntityObjId() == nFeed.getEntityObjId() &&
                                                TextUtils.equals(
                                                oFeed.getType(),
                                                nFeed.getType())) {
                                            oFeed.convertToEntity(nFeed);
                                            break;
                                        }
                                    }
                                }
                            } else if (!CommonUtil.isCollectionEmpty(histories)) {
                                for (FinderFeed nFeed : nFeeds) {
                                    for (FinderFeedHistory history : histories) {
                                        if (nFeed.getEntityObjId() == history.getId() &&
                                                TextUtils.equals(
                                                nFeed.getType(),
                                                history.getType())) {
                                            nFeed.setShowSimilarIcon(history.isShowSimilarIcon());
                                            nFeed.setShowRelevantHint(history
                                                    .isShowSimilarRelevantHint());
                                        }
                                    }
                                }
                            }
                        }
                        return !CommonUtil.isCollectionEmpty(oFeeds) ? oFeeds : nFeeds;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 发现页feeds流找相似
     *
     * @param position
     * @param data
     * @param id
     * @param type
     * @return
     */
    public static Observable<List<FinderFeed>> getFinderSimilarFeedsObb(
            final int position, final List<Object> data, long id, String type) {
        final StringBuilder sb = new StringBuilder();
        int start = position - 50;
        int end = position + 50;
        start = start < 0 ? 0 : start;
        end = end > data.size() ? data.size() : end;
        for (int i = start; i < end; i++) {
            Object obj = data.get(i);
            if (obj instanceof FinderFeed) {
                FinderFeed f = (FinderFeed) obj;
                sb.append(f.getType())
                        .append(":")
                        .append(f.getEntityObjId())
                        .append(",");
            }
        }
        if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
            sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
        }
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getFinderSimilarFeeds(sb.toString(), id, type)
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, List<FinderFeed>>() {
                    @Override
                    public List<FinderFeed> call(HljHttpData<List<FinderFeed>> listHljHttpData) {
                        List<FinderFeed> feeds = listHljHttpData == null ? null : listHljHttpData
                                .getData();
                        if (!CommonUtil.isCollectionEmpty(feeds)) {
                            Object obj = data.get(position);
                            Object o = null;
                            if (obj instanceof FinderFeed) {
                                o = ((FinderFeed) obj).getEntityObj();
                            } else if (obj instanceof CPMFeed) {
                                o = ((CPMFeed) obj).getEntityObj();
                            }
                            boolean isShowRelevantHint = o instanceof Merchant || o instanceof Note;
                            if (isShowRelevantHint) {
                                for (FinderFeed feed : feeds) {
                                    feed.setShowRelevantHint(true);
                                }
                            }
                        }
                        return feeds;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 发现页案例找相似
     *
     * @param position
     * @param data
     * @param id
     * @param type
     * @return
     */
    public static Observable<List<Work>> getFinderCaseSamesObb(
            int position, List<Work> data, long id, String type, String attrType) {
        StringBuilder sb = new StringBuilder();
        int start = position - 50;
        int end = position + 50;
        start = start < 0 ? 0 : start;
        end = end > data.size() ? data.size() : end;
        for (int i = start; i < end; i++) {
            Work work = data.get(i);
            sb.append(FinderFeed.TYPE_CASE + ":")
                    .append(work.getId())
                    .append(",");
        }
        if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
            sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("exclude_ids", sb.toString());
        map.put("attr[type]", attrType);
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSameCases(map)
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, List<Work>>() {
                    @Override
                    public List<Work> call(HljHttpData<List<FinderFeed>> listHljHttpData) {
                        if (listHljHttpData == null || listHljHttpData.isEmpty()) {
                            return null;
                        }
                        List<Work> works = new ArrayList<>();
                        for (FinderFeed finderFeed : listHljHttpData.getData()) {
                            if (finderFeed.getType()
                                    .equals(FinderFeed.TYPE_CASE)) {
                                Work work = (Work) finderFeed.getEntityObj();
                                if (work != null) {
                                    works.add(work);
                                }
                            }
                        }
                        return works;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发现页案例找相似
     *
     * @param id
     * @param type
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getSameCasesObb(
            long id, String type, int perPage, String attrType) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("per_page", perPage);
        if (!CommonUtil.isEmpty(attrType)) {
            map.put("attr[type]", attrType);
        }
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getSameCases(map)
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, HljHttpData<List<Work>>>() {
                    @Override
                    public HljHttpData<List<Work>> call(
                            HljHttpData<List<FinderFeed>> listHljHttpData) {
                        if (listHljHttpData == null) {
                            return null;
                        }
                        List<Work> works = new ArrayList<>();
                        for (FinderFeed finderFeed : listHljHttpData.getData()) {
                            if (finderFeed.getType()
                                    .equals(FinderFeed.TYPE_CASE)) {
                                Work work = (Work) finderFeed.getEntityObj();
                                if (work != null && !CommonUtil.isCollectionEmpty(work
                                        .getMediaItems())) {
                                    works.add(work);
                                }
                            }
                        }
                        HljHttpData<List<Work>> data = new HljHttpData<>();
                        data.setData(works);
                        data.setPageCount(listHljHttpData.getPageCount());
                        data.setTotalCount(listHljHttpData.getTotalCount());
                        return data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 相关热门案例
     *
     * @param id
     * @param kind
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getHotCasesObb(
            long id, String kind, String sort, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getHotCases(id, kind, sort, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 推荐笔记列表是否有新数据
     *
     * @param lastId
     * @param timestamp
     * @return
     */
    public static Observable<Boolean> hasNewRecommendsObb(long lastId, long timestamp) {
        timestamp = timestamp / 1000;
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .hasNewRecommendNotes(lastId, timestamp)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        return CommonUtil.getAsInt(jsonElement, "has_new") > 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 案例类别列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<CaseCategories>>> getCaseCategoriesObb() {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getCaseCategories()
                .map(new HljHttpResultFunc<HljHttpData<List<CaseCategories>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发现页笔记列表
     *
     * @param lastPostAt
     * @param notebookType
     * @param perPage
     * @return
     */
    public static Observable<List<FinderFeed>> getFinderNotesObb(
            String lastPostAt, final int notebookType, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getFinderNotes(lastPostAt, notebookType, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<FinderFeed>>>())
                .map(new Func1<HljHttpData<List<FinderFeed>>, List<FinderFeed>>() {
                    @Override
                    public List<FinderFeed> call(
                            HljHttpData<List<FinderFeed>> listHljHttpData) {
                        List<FinderFeed> feeds = listHljHttpData == null ? null : listHljHttpData
                                .getData();

                        //不是婚礼人的笔记列表都没有"找相似"功能
                        if (notebookType != NotebookType.TYPE_WEDDING_PERSON && !CommonUtil
                                .isCollectionEmpty(
                                feeds)) {
                            for (FinderFeed feed : feeds) {
                                feed.setShowSimilarIcon(false);
                            }
                        }
                        return feeds;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 案例类别列表
     *
     * @param ids
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getFinderCasesObb(
            String ids, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getFinderCases(ids, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏案例
     *
     * @param id 案例id
     * @return
     */
    public static Observable postCaseCollectObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("set_meal_id", id);
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .postCaseCollect(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消案例收藏
     *
     * @param id 案例id
     * @return
     */
    public static Observable deleteCaseCollectObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("set_meal_id", id);
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .deleteCaseCollect(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取德芙活动配置信息
     * @return
     */
    public static Observable<FindTabConfig> getDefuConfig() {
        return HljHttp.getRetrofit()
                .create(FinderService.class)
                .getDeFuConf()
                .map(new HljHttpResultFunc<FindTabConfig>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}