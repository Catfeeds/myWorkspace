package me.suncloud.marrymemo.model.user.wrappers;

import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

import me.suncloud.marrymemo.model.tools.WeddingGuest;

/**
 * Created by chen_bin on 2017/11/24 0024.
 */
public class HljWeddingGuestsData extends HljHttpData<List<WeddingGuest>> {

    private transient List<String> firstChars;

    public List<String> getFirstChars() {
        return firstChars;
    }

    public void setFirstChars(List<String> firstChars) {
        this.firstChars = firstChars;
    }
}
