package com.hunliji.marrybiz.api.chat;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.chat.FastReply;
import com.hunliji.marrybiz.model.easychat.EasyChat;
import com.hunliji.marrybiz.model.easychat.EditGreetBody;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/7/7.
 */

public class ChatApi {


    /**
     * 编辑问候语
     *
     * @param body
     * @return
     */
    public static Observable<EditGreetBody> postEditGreet(EditGreetBody body) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .postEditGreet(body)
                .map(new HljHttpResultFunc<EditGreetBody>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 检查是否已开通
     *
     * @return
     */
    public static Observable<EasyChat> getCheck() {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getCheck()
                .map(new HljHttpResultFunc<EasyChat>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取模板
     *
     * @return
     */
    public static Observable<HljHttpData<ArrayList<String>>> getTemplate() {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getTemplate()
                .map(new HljHttpResultFunc<HljHttpData<ArrayList<String>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 开通
     *
     * @return
     */
    public static Observable<HljHttpResult> postActive() {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .postActive()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 私信快捷回复列表
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<FastReply>>> getFastReplies(
            int page) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getFastReplies(page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<FastReply>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
