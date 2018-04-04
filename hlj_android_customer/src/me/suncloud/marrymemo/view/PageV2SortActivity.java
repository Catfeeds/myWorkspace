package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PageV2DraggableAdapter;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2016/5/19.
 */

public class PageV2SortActivity extends HljBaseActivity implements PageV2DraggableAdapter
        .OnPageActionListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private CardV2 card;
    private Dialog dialog;
    private Dialog progressDialog;
    private PageV2DraggableAdapter adapter;
    private boolean isChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        card = (CardV2) getIntent().getSerializableExtra("card");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_v2_sort);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = Math.round((point.x - 10 * dm.density) / 3);
        int height = Math.round((width - dm.density * 14) * 122 / 75 + 20 * dm.density);

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);

        adapter = new PageV2DraggableAdapter(card, width, height);
        adapter.setOnPageActionListener(this);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(adapter);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    @Override
    public void onBackPressed() {
        if (cardPagePositionChange()) {
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(this);
            }
            progressDialog.show();
            JSONObject jsonObject = new JSONObject();
            try {
                JSONArray array = new JSONArray();
                for (int i = 0, size = card.getPages()
                        .size(); i < size; i++) {
                    CardPageV2 page = card.getPages()
                            .get(i);
                    JSONObject pageOrder = new JSONObject();
                    pageOrder.put("id", page.getId());
                    pageOrder.put("position", i + 1);
                    array.put(pageOrder);
                }
                jsonObject.put("pageOrders", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new StatusHttpPostTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType.CARD_UPDATE_FLAG, card));
                    isChange = false;
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    PageV2SortActivity.super.onBackPressed();
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Util.postFailToast(PageV2SortActivity.this,
                            returnStatus,
                            R.string.msg_err_change_position,
                            network);
                }
            }).executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(Constants.HttpPath.PAGE_V2_CHANGE_POSITION),
                    jsonObject.toString());
            return;
        }
        super.onBackPressed();
    }

    private boolean cardPagePositionChange() {
        List<CardPageV2> pages = adapter.getPages();
        if (pages.isEmpty() || card.getPages()
                .isEmpty()) {
            isChange = false;
        } else {
            int size = card.getPages()
                    .size();
            int changeIndex = 0;
            for (CardPageV2 page1 : pages) {
                for (int j = changeIndex; j < size; j++) {
                    CardPageV2 page2 = card.getPages()
                            .get(j);
                    if (page1.getId()
                            .equals(page2.getId())) {
                        if (changeIndex != j) {
                            card.getPages()
                                    .add(changeIndex,
                                            card.getPages()
                                                    .remove(j));
                            isChange = true;
                        }
                        if (++changeIndex >= size) {
                            return isChange;
                        }
                        break;
                    }
                }
            }
        }
        return isChange;
    }

    @Override
    protected void onFinish() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mLayoutManager = null;
        super.onFinish();
    }

    @Override
    public void upLoadItem(final CardPageV2 cardPage) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                this,
                getString(cardPage.isDelete() ? R.string.msg_page_delete_confirm : R.string
                        .msg_speech_hide_confirm),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        upLoadPageInfo(cardPage);
                    }
                });
        dialog.show();
    }

    private void upLoadPageInfo(final CardPageV2 cardPage) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject pageObject = getPageJson(cardPage);
            if (cardPage.isSpeech()) {
                jsonObject.put("speech_page", pageObject);
            } else {
                JSONArray pageArray = new JSONArray();
                pageArray.put(pageObject);
                jsonObject.put("pages", pageArray);
            }
            jsonObject.put("id", card.getId());
            jsonObject.put("title", card.getTitle());
            jsonObject.put("theme_id", card.getThemeId());
            jsonObject.put("groom_name", card.getGroomName());
            jsonObject.put("bride_name", card.getBrideName());
            jsonObject.put("time", TimeUtil.getCardDateStr(card.getTime()));
            jsonObject.put("place", card.getPlace());
            jsonObject.put("latitude", card.getLatitude());
            jsonObject.put("longtitude", card.getLongitude());
            jsonObject.put("map_type", card.getMapType());
            jsonObject.put("speech", card.getSpeech());
        } catch (JSONException ignored) {
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                card = new CardV2((JSONObject) object);
                if (cardPage.isDelete()) {
                    adapter.removePage(cardPage);
                    cardPage.deletePageFile(PageV2SortActivity.this);
                } else {
                    adapter.hidePageChanged();
                }
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.EventType.CARD_UPDATE_FLAG, card));
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(PageV2SortActivity.this,
                        returnStatus,
                        cardPage.isDelete() ? R.string.msg_err_page_delete : R.string
                                .msg_err_speech_hide,
                        network);
            }
        }).executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.CARD_V2_SAVE),
                jsonObject.toString());
    }

    private JSONObject getPageJson(CardPageV2 cardPage) {
        JSONObject pageObject = new JSONObject();
        try {
            if (cardPage.getId() > 0) {
                pageObject.put("id", cardPage.getId());
            }
            if (cardPage.isSpeech()) {
                pageObject.put("hidden", cardPage.isHidden() ? 1 : 0);
            } else {
                pageObject.put("deleted", 1);
            }
            pageObject.put("page_template_id",
                    cardPage.getTemplate()
                            .getId());
            JSONArray textArray = new JSONArray();
            for (TextHoleV2 textHole : cardPage.getTexts()) {
                JSONObject textObject = new JSONObject();
                textObject.put("id", textHole.getId());
                textObject.put("font_size", textHole.getFontSize());
                textObject.put("font_name", textHole.getFontName());
                textObject.put("frame", textHole.getFrame());
                textObject.put("h5_text_rotate_degree", textHole.getAngle());
                textObject.put("type", textHole.getType());
                textObject.put("color", textHole.getColorStr());
                textObject.put("alignment", textHole.getAlignment());
                textObject.put("trans_info", textHole.getTransInfoStr());
                textObject.put("show_text", textHole.isShowText() ? 1 : 0);
                textObject.put("h5_text_image_path", textHole.getH5TextPath());
                textObject.put("h5_text_image_frame", textHole.getH5TextFrame());
                textObject.put("content", textHole.getContent());
                textArray.put(textObject);
            }
            pageObject.put("texts", textArray);
            JSONArray imageArray = new JSONArray();
            for (ImageHoleV2 imageHole : cardPage.getImageHoles()) {
                JSONObject imageObject = new JSONObject();
                imageObject.put("image_hole_id", imageHole.getId());
                imageObject.put("trans_info", imageHole.getTransInfoStr());
                imageObject.put("h5_hole_image_path", imageHole.getH5ImagePath());
                imageObject.put("image_path", imageHole.getPath());
                imageObject.put("image_hole_id", imageHole.getId());
                imageArray.put(imageObject);
            }
            pageObject.put("images", imageArray);
        } catch (JSONException ignored) {
        }
        return pageObject;
    }

    @Override
    protected void onPause() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.cancelDrag();
        }
        super.onPause();
    }
}
