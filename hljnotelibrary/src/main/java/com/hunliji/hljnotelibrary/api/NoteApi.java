package com.hunliji.hljnotelibrary.api;


import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResultList;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljcommonlibrary.models.search.WorksSearchResult;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpDayLimitData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljnotelibrary.models.FollowBody;
import com.hunliji.hljnotelibrary.models.NoteBookEditBody;
import com.hunliji.hljnotelibrary.models.NoteCategoryMark;
import com.hunliji.hljnotelibrary.models.NotePoster;
import com.hunliji.hljnotelibrary.models.NoteSearchResult;
import com.hunliji.hljnotelibrary.models.NoteSearchResultList;
import com.hunliji.hljnotelibrary.models.Notebook;
import com.hunliji.hljnotelibrary.models.wrappers.CreateNoteResponse;
import com.hunliji.hljnotelibrary.models.wrappers.HljHttpOrderedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 跟商家相关的网络请求操作
 * Created by chen_bin on 2017/06/24 0011.
 */
public class NoteApi {

    /**
     * 已下单数据
     *
     * @return
     */
    public static Observable<HljHttpOrderedData> getOrderedObb() {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getOrdered()
                .map(new HljHttpResultFunc<HljHttpOrderedData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取笔记详情
     *
     * @param id 笔记id
     * @return
     */
    public static Observable<Note> getNoteDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteDetail(id)
                .map(new HljHttpResultFunc<Note>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取笔记本详情
     *
     * @param id 笔记id
     * @return
     */
    public static Observable<Notebook> getNoteBookDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNotebookDetail(id)
                .map(new HljHttpResultFunc<Notebook>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记详情中相关婚品
     *
     * @param noteId 笔记id
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getNoteProductsObb(
            long noteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("note_id", noteId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteProducts(map)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记本中相关婚品
     *
     * @param noteBookId 笔记本id
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getNoteBookProductsObb(
            long noteBookId) {
        Map<String, Object> map = new HashMap<>();
        map.put("note_book_id", noteBookId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteProducts(map)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记详情中相关商家
     *
     * @param noteId 笔记id
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getNoteMerchantsObb(
            long noteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("note_id", noteId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteMerchants(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记本中相关商家
     *
     * @param noteBookId 笔记本id
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getNoteBookMerchantsObb(
            long noteBookId) {
        Map<String, Object> map = new HashMap<>();
        map.put("note_book_id", noteBookId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteMerchants(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记列表（我的笔记本或他人笔记本中的笔记列表）
     *
     * @param noteBookId 为0表示全部笔记
     * @param userId     不为0表示他人笔记本中的的笔记列表
     * @return
     */
    public static Observable<HljHttpData<List<Note>>> getNoteBookNotesObb(
            long noteBookId, long userId, String sort, int page, int perPage) {
        Map<String, Object> map = new HashMap<>();
        if (userId != 0) {
            map.put("user_id", userId);
        }
        if (noteBookId != 0) {
            map.put("note_book_id", noteBookId);
        }
        if (!TextUtils.isEmpty(sort)) {
            map.put("sort", sort);
        }
        map.put("page", page);
        map.put("per_page", perPage);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNoteBookNotes(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建笔记
     *
     * @param note
     * @return
     */
    public static Observable<CreateNoteResponse> createNoteObb(Note note) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .createNote(note)
                .map(new HljHttpResultFunc<CreateNoteResponse>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑笔记
     *
     * @param note
     * @return
     */
    public static Observable<CreateNoteResponse> editNoteObb(Note note) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .editNote(note)
                .map(new HljHttpResultFunc<CreateNoteResponse>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 生成海报详情页
     *
     * @param id
     * @return
     */
    public static Observable<NotePoster> getNotePoster(long id) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNotePoster(id)
                .map(new HljHttpResultFunc<NotePoster>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 标签详情
     *
     * @param markId
     * @return
     */
    public static Observable<NoteMark> markDetail(long markId) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .markDetail(markId)
                .map(new HljHttpResultFunc<NoteMark>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的或者其他人笔记本列表
     *
     * @param userId
     * @return
     */
    public static Observable<HljHttpData<List<Notebook>>> myNoteBookList(long userId, int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .myNoteBookList(userId, page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Notebook>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的或者其他人的笔记列表
     *
     * @param userId
     * @return
     */
    public static Observable<HljHttpData<List<Note>>> myNoteList(long userId, int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .myNoteList(userId, page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 相关笔记（看了又看）
     *
     * @param noteId 笔记id
     * @return
     */
    public static Observable<HljHttpData<List<Note>>> getRelativeNotesObb(
            long noteId, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("per_page", HljCommon.PER_PAGE);
        map.put("note_id", noteId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getRelativeNotes(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 灵感收藏列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<NoteInspiration>>> getFollowNoteMedia(int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getFollowNoteMedia("NoteMedia", page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<NoteInspiration>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 标签下的笔记
     *
     * @return
     */
    public static Observable<NoteSearchResultList> getMarkNoteList(
            long tags, String sort, int page) {
        Map<String, String> map = new HashMap<>();
        map.put("filter[tags]", String.valueOf(tags));
        map.put("type", "41");
        map.put("sort", sort);
        map.put("page", String.valueOf(page));
        map.put("per_page", String.valueOf(HljCommon.PER_PAGE));
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getMarkNoteList(map)
                .map(new Func1<HljHttpResult<NoteSearchResult>, NoteSearchResultList>() {
                    @Override
                    public NoteSearchResultList call(HljHttpResult<NoteSearchResult> result) {
                        if (result == null || result.getData() == null) {
                            return null;
                        }
                        return result.getData()
                                .getNoteResult();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取标签通用接口
     *
     * @param entity(对应分类的model)
     * @param type(层级,0标签组(全部层级结构),1标签默认是1)
     * @param level(分类)
     * @return
     */
    public static Observable<HljHttpData<List<CategoryMark>>> getSimpleCategoryMarksObb(
            String entity, int type, int level) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getSimpleCategoryMarks(entity, type, level)
                .map(new HljHttpResultFunc<HljHttpData<List<CategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家笔记标签
     */
    public static Observable<HljHttpData<List<NoteCategoryMark>>> getCategoryMarksObb() {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getCategoryMarks()
                .map(new HljHttpResultFunc<HljHttpData<List<NoteCategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记本中相关笔记（看了又看）
     *
     * @param noteBookId 笔记本id
     * @return
     */
    public static Observable<HljHttpData<List<Note>>> getNotebookRelativeNotesObb(
            long noteBookId, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("per_page", HljCommon.PER_PAGE);
        map.put("note_book_id", noteBookId);
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getRelativeNotes(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签下的笔记
     *
     * @return
     */
    public static Observable<WorksSearchResult> getMarkWorkList(
            long tags, String sort, int page) {
        Map<String, String> map = new HashMap<>();
        map.put("filter[tags]", String.valueOf(tags));
        map.put("type", "42");
        map.put("sort", sort);
        map.put("page", String.valueOf(page));
        map.put("per_page", String.valueOf(HljCommon.PER_PAGE));

        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getMarkWorkList(map)
                .map(new Func1<HljHttpResult<ServiceSearchResult>, WorksSearchResult>() {
                    @Override
                    public WorksSearchResult call(
                            HljHttpResult<ServiceSearchResult> serviceSearchResultHljHttpResult) {
                        if (serviceSearchResultHljHttpResult == null ||
                                serviceSearchResultHljHttpResult.getData() == null) {
                            return null;
                        }
                        return serviceSearchResultHljHttpResult.getData()
                                .getWorksSearchResult();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签下的笔记
     *
     * @return
     */
    public static Observable<ProductSearchResultList> getMarkProductList(
            long tags, String sort, int page) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "43");
        map.put("sort", sort);
        map.put("page", String.valueOf(page));
        map.put("per_page", String.valueOf(HljCommon.PER_PAGE));
        map.put("filter[tags]", String.valueOf(tags));

        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getMarkProductList(map)
                .map(new Func1<HljHttpResult<ProductSearchResult>, ProductSearchResultList>() {

                    @Override
                    public ProductSearchResultList call(HljHttpResult<ProductSearchResult> result) {
                        return result == null ? null : result.getData()
                                .getProductList();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消关注标签
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> unFollowNoteMark(FollowBody body) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .unFollowNoteMark(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关注标签
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> followNoteMark(FollowBody body) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .followNoteMark(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑笔记本
     * 没有编辑封面图片 不需要传photo
     *
     * @return
     */
    public static Observable<HljHttpResult> editNoteBook(
            NoteBookEditBody editBody) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .editNoteBook(editBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家端笔记列表
     *
     * @return
     */
    public static Observable<HljHttpDayLimitData<List<Note>>> getMerchantNoteBookNotesObb(int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getMerchantNoteBookNotes(page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpDayLimitData<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 笔记本评论
     *
     * @param notebookId
     * @return
     */
    public static Observable<HljHttpData<List<RepliedComment>>> getNotebookCommentsObb(
            long notebookId, String type, int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getNotebookComments(notebookId, type, page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<RepliedComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签收藏列表（我的收藏）
     * <p>
     * 标签为Mark
     *
     * @return
     */
    public static Observable<HljHttpData<List<Mark>>> getFollowMark(int page) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getFollowMark("Mark", page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Mark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家主页获得merchant
     *
     * @param id
     * @return
     */
    public static Observable<Merchant> getMerchantInfoV2(long id) {
        return HljHttp.getRetrofit()
                .create(NoteService.class)
                .getMerchantInfoV2(id)
                .map(new HljHttpResultFunc<Merchant>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
