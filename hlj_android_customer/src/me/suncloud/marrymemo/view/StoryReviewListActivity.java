package me.suncloud.marrymemo.view;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Reply;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/10/10.
 */
public class StoryReviewListActivity extends BaseReviewListActivity {

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        count = getIntent().getIntExtra("count", 0);
        titleStr = getString(R.string.title_story_review);
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getUrl(int currentPage) {
        return Constants.getAbsUrl(Constants.HttpPath.GET_STORY_REPLY_URL, id, currentPage);
    }

    @Override
    public void deleteReply(Reply reply, StatusRequestListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", reply.getId());
            new StatusHttpPostTask(this, listener).execute(Constants.getAbsUrl(Constants.HttpPath
                    .STORY_REPLY_DELETE_URL), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postReply(String content, Reply reply, StatusRequestListener listener) {
        hideKeyboard(null);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("story_id", id);
            jsonObject.put("content", content);
            if (reply != null) {
                jsonObject.put("reply_id", reply.getId());
            }
            if (progressDialog == null) {
                progressDialog = JSONUtil.getRoundProgress(this);
            } else if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            progressDialog.onLoadComplate();
            progressDialog.setCancelable(false);
            new StatusHttpPostTask(this, listener, progressDialog).execute(Constants.getAbsUrl
                    (Constants.HttpPath.STORY_REPLY_URL), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
