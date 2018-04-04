/**
 *
 */
package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class Subject implements Identifiable {
    /**
     *
     */
    private static final long serialVersionUID = 1947316611840706284L;

    private Long id;
    private String title;
    private String cover;
    private String description;
    private String background;
    private ShareInfo shareInfo;

    /**
     *
     */
    public Subject(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id");
            this.title = JSONUtil.getString(json, "title");
            this.cover = JSONUtil.getString(json, "cover_path");
            this.description = JSONUtil.getString(json, "description");
            this.background = JSONUtil.getString(json, "bkg_path");
            if (!json.isNull("share")) {
                ShareInfo share = new ShareInfo(json.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }
        }
    }

    public String getDescription() {
        return description;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the cover
     */
    public String getCover() {
        return cover;
    }

    /**
     * @param cover the cover to set
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.suncloud.marrymemo.model.Identifiable#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getBackground() {
        return background;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }
}
