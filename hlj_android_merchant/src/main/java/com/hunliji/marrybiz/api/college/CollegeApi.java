package com.hunliji.marrybiz.api.college;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.college.CollegeItem;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 2017/11/22.
 */

public class CollegeApi {

    /**
     * 获取课程banners
     *
     * @return
     */
    public static Observable<List<Poster>> getCollegeBanners() {
        return HljHttp.getRetrofit()
                .create(CollegeService.class)
                .getCollegeBanners()
                .map(new HljHttpResultFunc<HljHttpData<List<CollegeItem>>>())
                .map(new Func1<HljHttpData<List<CollegeItem>>, List<Poster>>() {
                    @Override
                    public List<Poster> call(HljHttpData<List<CollegeItem>> listHljHttpData) {
                        List<Poster> posters = new ArrayList<>();
                        for (CollegeItem banner : listHljHttpData.getData()) {
                            Poster poster = new Poster();
                            poster.setId(Long.valueOf(banner.getId()));
                            poster.setPath(banner.getImgPath());
                            poster.setUrl(banner.getUrl());
                            poster.setTargetType(9);
                            posters.add(poster);
                        }
                        return posters;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpData<List<CollegeItem>>> getCollegeItems(
            int type,
            int page) {
        return HljHttp.getRetrofit()
                .create(CollegeService.class)
                .getCollegeItems(type, page, 15)
                .map(new HljHttpResultFunc<HljHttpData<List<CollegeItem>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请求是否有新课程
     *
     * @return
     */
    public static Observable<Boolean> hasNewLessons() {
        return HljHttp.getRetrofit()
                .create(CollegeService.class)
                .getNewLessonCount()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        return CommonUtil.getAsLong(jsonElement, "max_id") > 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
