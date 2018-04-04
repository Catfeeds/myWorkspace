package com.hunliji.marrybiz.api.chat;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.chat.FastReply;
import com.hunliji.marrybiz.model.easychat.EasyChat;
import com.hunliji.marrybiz.model.easychat.EditGreetBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hua_rong on 2017/7/7.
 */

public interface ChatService {

    /**
     * 编辑问候语
     *
     * @param body
     * @return
     */
    @POST("/p/wedding/Admin/APIMerchantChatlink/edit")
    Observable<HljHttpResult<EditGreetBody>> postEditGreet(@Body EditGreetBody body);

    /**
     * 检查是否已开通
     *
     * @return
     */
    @GET("/p/wedding/Admin/APIMerchantChatlink/check")
    Observable<HljHttpResult<EasyChat>> getCheck();

    /**
     * 获取模板
     *
     * @return
     */
    @GET("/p/wedding/Admin/APIMerchantChatlink/template")
    Observable<HljHttpResult<HljHttpData<ArrayList<String>>>> getTemplate();

    /**
     * 开通
     *
     * @return
     */
    @POST("/p/wedding/Admin/APIMerchantChatlink/active")
    Observable<HljHttpResult> postActive();


    /**
     *<a href="http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#3330">私信快捷回复列表
     * @param page
     * @param perPage
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIFastReply/list")
    Observable<HljHttpResult<HljHttpData<List<FastReply>>>> getFastReplies(
            @Query("page") int page,
            @Query("per_page") int perPage);


}
