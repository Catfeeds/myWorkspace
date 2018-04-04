package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 套餐案例列表接口未换，列表数据中转类
 * Created by wangtao on 2016/12/9.
 */

public class WorksData extends HljHttpData<List<Work>> {

    @SerializedName("works")
    private List<Work> works;

    private int currentPage;
    private int perPage;


    /**
     * 设置当前页用于修改pageCount判断分页，必须参数
     * @param currentPage 当前页
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @param perPage 每页返回数量
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    @Override
    public List<Work> getData() {
        return works;
    }

    /**
     * 返回数量小于每页数量，当前页为最后一页
     * @return end currentPage；more currentPage+1
     */
    @Override
    public int getPageCount() {
        if(works.size()<perPage){
            return currentPage;
        }
        return currentPage+1;
    }
}
