package com.hunliji.hljtrackerlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.Tracker;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Suncloud on 2016/8/10.
 */
public class HljTracker {

    public static final String SITE = "site";

    private String trackerStr;

    private HljTracker(String trackerStr) {
        this.trackerStr = trackerStr;
    }

    public void add() {
        insertTracker(false);
    }

    public void send() {
        insertTracker(true);
    }

    private void insertTracker(boolean send) {
        if (TextUtils.isEmpty(trackerStr)) {
            return;
        }
        if(HljCommon.debug){
            Log.d("HljViewTracker",trackerStr);
        }
        RxBus.getDefault()
                .post(new Tracker(trackerStr, 1));
    }

    public static class Builder {

        private Context context;
        private Long eventableId;
        private String eventableType;
        private String screen;
        private String action;
        private String additional;
        private JSONObject site;
        private String sid;
        private String desc;
        private int pos;

        private String tracker;

        public Builder(@Nullable Context context) {
            this.context = context;
        }

        public Builder eventableId(Long eventableId) {
            this.eventableId = eventableId;
            return this;
        }

        public Builder eventableType(String eventableType) {
            this.eventableType = eventableType;
            return this;
        }

        public Builder screen(String screen) {
            this.screen = screen;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder additional(String additional) {
            this.additional = additional;
            return this;
        }

        public Builder site(JSONObject site) {
            this.site = site;
            return this;
        }

        public Builder site(String siteStr) {
            if (!TextUtils.isEmpty(siteStr)) {
                try {
                    site = new JSONObject(siteStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public Builder sid(String sid) {
            this.sid = sid;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder pos(int pos) {
            this.pos = pos;
            return this;
        }

        public Builder tracker(String tracker) {
            this.tracker = tracker;
            return this;
        }

        public HljTracker build() {
            if (!TextUtils.isEmpty(tracker)) {
                return new HljTracker(tracker);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("eventable_id", eventableId);
                jsonObject.put("eventable_type", eventableType);
                jsonObject.put("screen", screen);
                jsonObject.put("action", action);
                jsonObject.put("additional", additional);
                jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
                if (site == null) {
                    site = TrackerUtil.getSiteJson(sid, pos, desc);
                }
                if (site != null) {
                    jsonObject.put("site", site);
                }
                User user = UserSession.getInstance()
                        .getUser(context);
                if (user != null && !TextUtils.isEmpty(user.getToken())) {
                    jsonObject.put("user_token", user.getToken());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new HljTracker(jsonObject.toString());
        }
    }
}
