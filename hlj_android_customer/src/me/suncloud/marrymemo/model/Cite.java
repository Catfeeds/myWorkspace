package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/4/20.
 */
public class Cite implements Identifiable {

    private long id;
    private long citeId;
    private long authorId;
    private long targetId;
    private String des;
    private String url;
    private String type;
    private String title;
    private String coverPath;
    private String targetUrl;
    private int hitsCount;
    private int fansCount;
    private int postsCount;
    private int worksCount;
    private int casesCount;
    private int addedCount;
    private int praisedCount;
    private int collectorsCount;
    private int targetType;
    private int favoritesCount;
    private Date createdTime;
    private double actualPrice;
    private double marketPrice;


    public Cite (JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id",0);
            citeId=jsonObject.optLong("cite_ident",0);
            authorId=jsonObject.optLong("author_id",0);
            targetId=jsonObject.optLong("target_id",0);
            targetType = jsonObject.optInt("target_type", 0);
            hitsCount=jsonObject.optInt("hits", 0);
            fansCount=jsonObject.optInt("fans_count", 0);
            postsCount=jsonObject.optInt("post_count", 0);
            worksCount=jsonObject.optInt("active_works_pcount", 0);
            casesCount=jsonObject.optInt("active_cases_pcount", 0);
            addedCount=jsonObject.optInt("fake_added", 0);
            favoritesCount=jsonObject.optInt("favorites_count", 0);
            praisedCount=jsonObject.optInt("praised_sum",0);
            collectorsCount=jsonObject.optInt("collectors_count",0);
            createdTime=JSONUtil.getDate(jsonObject, "created_at");
            actualPrice=jsonObject.optDouble("actual_price", 0);
            marketPrice=jsonObject.optDouble("market_price",0);
            des= JSONUtil.getString(jsonObject, "des");
            url=JSONUtil.getString(jsonObject,"url");
            type=JSONUtil.getString(jsonObject,"type");
            if (JSONUtil.isEmpty(type)) {
                type=JSONUtil.getString(jsonObject,"citable_type");
            }
            title=JSONUtil.getString(jsonObject,"title");
            coverPath=JSONUtil.getString(jsonObject, "cover_path");
            targetUrl=JSONUtil.getString(jsonObject, "target_url");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public int getCasesCount() {
        return casesCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public int getHitsCount() {
        return hitsCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public int getWorksCount() {
        return worksCount;
    }

    public long getAuthorId() {
        return authorId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getDes() {
        return des;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getTargetType() {
        return targetType;
    }

    public long getTargetId() {
        return targetId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public long getCiteId() {
        return citeId;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }

    public int getPraisedCount() {
        return praisedCount;
    }
}
