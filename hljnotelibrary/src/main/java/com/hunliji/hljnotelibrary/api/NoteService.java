package com.hunliji.hljnotelibrary.api;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpDayLimitData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljnotelibrary.models.FollowBody;
import com.hunliji.hljnotelibrary.models.NoteBookEditBody;
import com.hunliji.hljnotelibrary.models.NoteCategoryMark;
import com.hunliji.hljnotelibrary.models.NotePoster;
import com.hunliji.hljnotelibrary.models.NoteSearchResult;
import com.hunliji.hljnotelibrary.models.Notebook;
import com.hunliji.hljnotelibrary.models.wrappers.CreateNoteResponse;
import com.hunliji.hljnotelibrary.models.wrappers.HljHttpOrderedData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 跟笔记相关的网络请求操作
 * Created by chen_bin on 2017/06/24 0011.
 */

public interface NoteService {

    /**
     * 已下单数据
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINote/ordered")
    Observable<HljHttpResult<HljHttpOrderedData>> getOrdered();

    /**
     * 获取标签通用接口
     *
     * @param entity
     * @param type
     * @param level
     * @return
     */
    @GET("p/wedding/index.php/Home/APIMark/category_marks_simple")
    Observable<HljHttpResult<HljHttpData<List<CategoryMark>>>> getSimpleCategoryMarks(
            @Query("entity") String entity, @Query("type") int type, @Query("level") int level);

    /**
     * 商家笔记视频标签
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIMark/category_marks?type=15&entity_id=2&level=1")
    Observable<HljHttpResult<HljHttpData<List<NoteCategoryMark>>>> getCategoryMarks();

    /**
     * 获取笔记详情
     *
     * @param id 笔记id
     * @return
     */
    @GET("p/wedding/index.php/Home/APINote/detail")
    Observable<HljHttpResult<Note>> getNoteDetail(@Query("id") long id);

    /**
     * 获取笔记本详情
     *
     * @param id 笔记id
     * @return
     */
    @GET("p/wedding/index.php/home/APINoteBook/detail")
    Observable<HljHttpResult<Notebook>> getNotebookDetail(@Query("id") long id);

    /**
     * 笔记详情中相关婚品
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINote/relative_shop_product")
    Observable<HljHttpResult<HljHttpData<List<ShopProduct>>>> getNoteProducts(
            @QueryMap Map<String, Object> map);

    /**
     * 笔记详情当中相关商家
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINote/relative_merchant")
    Observable<HljHttpResult<HljHttpData<List<Merchant>>>> getNoteMerchants(
            @QueryMap Map<String, Object> map);

    /**
     * 用户端笔记列表（我的笔记本或他人笔记本中的笔记列表）
     */
    @GET("p/wedding/index.php/home/APINote/my_notes")
    Observable<HljHttpResult<HljHttpData<List<Note>>>> getNoteBookNotes(
            @QueryMap Map<String, Object> map);

    /**
     * 相关笔记（看了又看）
     */
    @GET("p/wedding/index.php/home/APINote/relative_notes")
    Observable<HljHttpResult<HljHttpData<List<Note>>>> getRelativeNotes(
            @QueryMap Map<String, Object> map);

    /**
     * 创建笔记
     *
     * @param note
     * @return
     */
    @POST("p/wedding/index.php/Home/APINote/create")
    Observable<HljHttpResult<CreateNoteResponse>> createNote(@Body Note note);

    /**
     * 编辑笔记
     *
     * @param note
     * @return
     */
    @POST("p/wedding/Home/APINote/modify")
    Observable<HljHttpResult<CreateNoteResponse>> editNote(@Body Note note);

    /**
     * 生成海报详情页
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Home/APINote/poster")
    Observable<HljHttpResult<NotePoster>> getNotePoster(@Query("id") long id);

    /**
     * 标签详情
     *
     * @param markId
     * @return
     */
    @GET("p/wedding/home/APIMark/mark_detail")
    Observable<HljHttpResult<NoteMark>> markDetail(@Query("mark_id") long markId);

    /**
     * 我的或者其他人笔记本列表
     *
     * @param userId
     * @return
     */
    @GET("p/wedding/index.php/home/APINoteBook/my_notebooks")
    Observable<HljHttpResult<HljHttpData<List<Notebook>>>> myNoteBookList(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 我的或者其他人的笔记列表
     *
     * @param userId
     * @return
     */
    @GET("p/wedding/index.php/home/APINote/my_notes")
    Observable<HljHttpResult<HljHttpData<List<Note>>>> myNoteList(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 灵感收藏列表（我的收藏）
     *
     * @param type 收藏类型  灵感为NoteMedia
     * @return
     */
    @GET("p/wedding/index.php/home/APIFollow/follows")
    Observable<HljHttpResult<HljHttpData<List<NoteInspiration>>>> getFollowNoteMedia(
            @Query("followable_type") String type,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 标签下的笔记
     * <p>
     * 笔记 type 34
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV2/list")
    Observable<HljHttpResult<NoteSearchResult>> getMarkNoteList(
            @QueryMap() Map<String, String> filterMap);

    /**
     * 标签下的套餐
     * <p>
     * 套餐 type 42
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV2/list")
    Observable<HljHttpResult<ServiceSearchResult>> getMarkWorkList(
            @QueryMap() Map<String, String> filterMap);

    /**
     * 标签下的笔记
     * <p>
     * 婚品 type  43
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV2/list")
    Observable<HljHttpResult<ProductSearchResult>> getMarkProductList(
            @QueryMap() Map<String, String> filterMap);


    /**
     * 取消关注标签
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APIFollow/unfollow")
    Observable<HljHttpResult> unFollowNoteMark(@Body FollowBody body);

    /**
     * 关注标签
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APIFollow/follow")
    Observable<HljHttpResult> followNoteMark(@Body FollowBody body);

    /**
     * 编辑笔记本
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APINoteBook/modify")
    Observable<HljHttpResult> editNoteBook(
            @Body NoteBookEditBody bookEditBody);

    /**
     * 商家端笔记列表
     */
    @GET("p/wedding/index.php/admin/APINote/my_notes")
    Observable<HljHttpResult<HljHttpDayLimitData<List<Note>>>> getMerchantNoteBookNotes(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 笔记本评论列表
     */
    @GET("p/wedding/index.php/home/APICommunityComment/note_book_comment_list")
    Observable<HljHttpResult<HljHttpData<List<RepliedComment>>>> getNotebookComments(
            @Query("note_book_id") long notebookId,
            @Query("type") String string,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 标签收藏列表（我的收藏）
     *
     * @param type 收藏类型  标签为Mark
     * @return
     */
    @GET("p/wedding/index.php/home/APIFollow/follows")
    Observable<HljHttpResult<HljHttpData<List<Mark>>>> getFollowMark(
            @Query("followable_type") String type,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 商家主页获得merchant
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/detailMerchantV2")
    Observable<HljHttpResult<Merchant>> getMerchantInfoV2(@Query("mer_id") long id);
}