package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.utils.Lunar;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PageTextMenuAdapter;
import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.TransInfo;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.model.V2.ImageInfoV2;
import me.suncloud.marrymemo.model.V2.TemplateV2;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.CardImageUtil;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;
import me.suncloud.marrymemo.widget.DraggableImageView;
import me.suncloud.marrymemo.widget.PageTextView;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import me.suncloud.marrymemo.widget.TextViewContainerView;

/**
 * Created by Suncloud on 2016/5/4.
 */
public class PageV2EditActivity extends HljBaseNoBarActivity implements TextViewContainerView
        .TextActionListener, CheckableLinearLayoutGroup.OnCheckedChangeListener {

    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @BindView(R.id.page_view)
    FrameLayout pageView;
    @BindView(R.id.iv_drag)
    ImageView ivDrag;
    @BindView(R.id.images_layout)
    FrameLayout imagesLayout;
    @BindView(R.id.strokes_layout)
    FrameLayout strokesLayout;
    @BindView(R.id.texts_layout)
    TextViewContainerView textsLayout;
    @BindView(R.id.btn_hide)
    CheckBox btnHide;
    @BindView(R.id.btn_drag)
    ImageButton btnDrag;
    @BindView(R.id.et_text_content)
    EditText etTextContent;
    @BindView(R.id.btn_menu)
    ImageButton btnMenu;
    @BindView(R.id.edit_layout)
    RelativeLayout editLayout;
    @BindView(R.id.menu_page)
    ViewPager menuPage;
    @BindView(R.id.menu_layout)
    LinearLayout menuLayout;
    @BindView(R.id.menus)
    CheckableLinearLayoutGroup menus;

    private CardV2 card;
    private CardPageV2 cardPage;
    private TemplateV2 template;
    private float scale;
    private int width;
    private int pageWidth;
    private int pageHeight;
    private DisplayMetrics dm;
    private ImageViewsContainer imageViewsContainer;
    private ArrayList<TextHoleV2> textHoles;
    private ArrayList<PageTextView> textViews;
    private ArrayList<DraggableImageView> imageViews;
    private PopupWindow popupWindow;
    private boolean isChange;
    private RoundProgressDialog progressDialog;
    private PageTextMenuAdapter menuAdapter;

    private boolean immIsShow;
    private boolean showMenu;
    private InputMethodManager imm;
    private int Oheight;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Point point = JSONUtil.getDeviceSize(this);
        dm = getResources().getDisplayMetrics();
        card = (CardV2) getIntent().getSerializableExtra("card");
        cardPage = (CardPageV2) getIntent().getSerializableExtra("cardPage");
        imageViews = new ArrayList<>();
        textViews = new ArrayList<>();
        template = cardPage.getTemplate();
        textHoles = cardPage.getTexts();
        ArrayList<ImageHoleV2> imageHoles = cardPage.getImageHoles();
        pageWidth = template.getWidth();
        pageHeight = template.getHeight();
        width = Math.round(point.x - 20 * dm.density);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_v2_edit);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);

        for (ImageHoleV2 imageHole : imageHoles) {
            DraggableImageView imageView = new DraggableImageView(this);
            imageView.setVisibility(View.GONE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectImagePopup((DraggableImageView) v);
                }
            });
            imagesLayout.addView(imageView);
            for (ImageInfoV2 imageInfo : cardPage.getImages()) {
                if (imageInfo.getHoleId() != imageHole.getId()) {
                    continue;
                }
                if (!JSONUtil.isEmpty(imageInfo.getImagePath())) {
                    imageHole.setPath(imageInfo.getImagePath());
                }
                if (!JSONUtil.isEmpty(imageInfo.getH5ImagePath())) {
                    imageHole.setH5ImagePath(imageInfo.getH5ImagePath());
                }
                String path = imageInfo.getImagePath();
                TransInfo transInfo = imageInfo.getTransInfo();
                if (!JSONUtil.isEmpty(imageHole.getMaskImagePath()) || !JSONUtil.isEmpty(path)) {
                    new ImageHoldLoadTask(this,
                            imageView,
                            transInfo).executeOnExecutor(Constants.THEADPOOL,
                            imageHole.getMaskImagePath(),
                            path);
                }
                break;
            }
            imageView.setImageHoleV2(imageHole);
            imageViews.add(imageView);
        }

        new BackgroundDownTask().executeOnExecutor(Constants.THEADPOOL, template.getBackground());
        menuAdapter = new PageTextMenuAdapter(this);
        menus.setOnCheckedChangeListener(this);
        menuPage.setAdapter(menuAdapter);
        menuPage.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((CheckableLinearLayoutButton) menus.getChildAt(0)).setChecked(true);
                } else if (position == 1) {
                    ((CheckableLinearLayoutButton) menus.getChildAt(2)).setChecked(true);
                }
                super.onPageSelected(position);
            }
        });
        etTextContent.addTextChangedListener(textsLayout);
        findViewById(R.id.layout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                boolean visible = (double) (bottom - top) / height < 0.8;
                if (visible) {
                    immIsShow = true;
                    showMenu = false;
                    menuLayout.setVisibility(View.GONE);
                    btnMenu.setImageResource(R.drawable.icon_text_menu_primary);
                    btnMenu.requestLayout();
                } else {
                    immIsShow = false;
                    if (showMenu) {
                        menuLayout.setVisibility(View.VISIBLE);
                        btnMenu.setImageResource(R.drawable.icon_keyboard_primary);
                        btnMenu.requestLayout();
                        etTextContent.requestFocus();
                    }
                }
            }
        });
        if (cardPage.isSpeech()) {
            findViewById(R.id.ok_btn).setVisibility(View.GONE);
        }
    }

    private boolean isBackPressed;

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            return;
        }
        if (onTextEditDone(null)) {
            return;
        }
        if (changeCheck() && !cardPage.isSpeech()) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (dialog == null) {
                dialog = DialogUtil.createDoubleButtonDialog(dialog,
                        this,
                        getString(R.string.hint_page_edit_back),
                        null,
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
            }
            dialog.show();
            return;
        }
        finish();
        isBackPressed = true;
        overridePendingTransition(0, 0);
    }

    private boolean changeCheck() {
        if (isChange || textsLayout.isChange()) {
            textsLayout.setChange(false);
            return true;
        }
        for (DraggableImageView imageView : imageViews) {
            ImageHoleV2 imageHole = imageView.getImageHoleV2();
            if (imageView.isChange() || (imageHole != null && JSONUtil.isEmpty(imageHole
                    .getH5ImagePath()))) {
                isChange = true;
                return true;
            }
        }
        for (PageTextView textView : textViews) {
            TextHoleV2 textHole = textView.getTextHole();
            if (textView.isChange() || (textHole != null && textHole.isShowText() && JSONUtil
                    .isEmpty(
                    textHole.getH5TextPath()))) {
                isChange = true;
                return true;
            }
        }
        return false;
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void onOkPressed(View view) {
        onTextEditDone(null);
        for (DraggableImageView imageView : imageViews) {
            ImageHoleV2 imageHole = imageView.getImageHoleV2();
            if (imageHole == null || JSONUtil.isEmpty(imageHole.getPath())) {
                Util.showToast(this, null, R.string.msg_err_image_hole_empty);
                showSelectImagePopup(imageView);
                return;
            }
        }
        if (!changeCheck()) {
            onBackPressed();
            return;
        }
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        upLoadText();
    }

    @Override
    public void onTextEdit(PageTextView textView) {
        editLayout.setVisibility(View.VISIBLE);
        menuAdapter.setTextView(textsLayout);
        etTextContent.setText(textView.getText());
        TextHoleV2 textHole = textView.getTextHole();
        if (textHole != null && !JSONUtil.isEmpty(textHole.getType())) {
            etTextContent.requestFocus();
            if (getCurrentFocus() != null && !immIsShow) {
                imm.toggleSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public boolean onTextEditDone(PageTextView textView) {
        boolean b = false;
        if (immIsShow && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            b = true;
        }
        if (menuLayout.getVisibility() == View.VISIBLE) {
            menuLayout.setVisibility(View.GONE);
            btnMenu.setImageResource(R.drawable.icon_text_menu_primary);
            b = true;
        }
        if (editLayout.getVisibility() == View.VISIBLE) {
            b = true;
            editLayout.setVisibility(View.GONE);
        }
        if (textsLayout.getTextView() != null) {
            b = true;
            textsLayout.setTextView(null);
        }
        return b;
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cb_fonts:
                menuPage.setCurrentItem(0);
                break;
            case R.id.cb_colors:
                menuPage.setCurrentItem(1);
                break;
        }
    }

    public void onShowMenu(View view) {
        if (menuLayout.getVisibility() == View.GONE && !immIsShow) {
            menuLayout.setVisibility(View.VISIBLE);
            btnMenu.setImageResource(R.drawable.icon_keyboard_primary);
        } else {
            showMenu = true;
            if (getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private class BackgroundDownTask extends AsyncTask<String, Object, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String backgroundPath = params[0];
            File file = FileUtil.createThemeFile(PageV2EditActivity.this, backgroundPath);
            return JSONUtil.getImageFromUrl(PageV2EditActivity.this,
                    backgroundPath,
                    file,
                    template.getWidth(),
                    template.getWidth());
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            if (bitmap != null) {
                if (pageWidth == 0 || pageHeight == 0) {
                    pageWidth = bitmap.getWidth();
                    pageHeight = bitmap.getHeight();
                }
                if (ivBackground.getMeasuredHeight() > 0) {
                    onPreview(bitmap);
                } else {
                    ivBackground.getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver
                                    .OnGlobalLayoutListener() {


                                @Override
                                public void onGlobalLayout() {
                                    onPreview(bitmap);
                                    ivBackground.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                }
                            });
                }
            }
            super.onPostExecute(bitmap);
        }
    }

    private void onPreview(Bitmap bitmap) {
        scale = (float) width / pageWidth;
        int h = Math.round(scale * pageHeight);
        int viewH = ivBackground.getMeasuredHeight();
        findViewById(R.id.page_frame).setVisibility(View.VISIBLE);
        Oheight = findViewById(R.id.page_frame).getMeasuredHeight();
        int pageViewH;
        int pageViewW;
        if (h > viewH) {
            scale = (float) viewH / pageHeight;
            width = Math.round(scale * pageWidth);
            pageViewH = Math.round(viewH + 10 * dm.density);
            pageViewW = Math.round(width + 10 * dm.density);
        } else {
            pageViewH = Math.round(h + 10 * dm.density);
            pageViewW = Math.round(width + 10 * dm.density);
        }
        findViewById(R.id.page_frame).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (top == oldTop && bottom == oldBottom) {
                    return;
                }
                TextView textView = textsLayout.getTextView();
                if (textView != null) {
                    double angle = textView.getRotation() * Math.PI / 180;
                    double b = textView.getTop() + textView.getHeight() / 2 + (Math.abs(Math.sin(
                            angle)) * textView.getWidth() * textView.getScaleX() + Math.abs(Math
                            .cos(
                            angle)) * textView.getHeight() * textView.getScaleY()) / 2 + pageView
                            .getPaddingTop() + (Oheight - pageView.getHeight()) / 2;
                    if (bottom - top >= b) {
                        pageView.setY((Oheight - pageView.getHeight()) / 2);
                    } else {
                        pageView.setY((float) Math.max((bottom - top - b),
                                (bottom - top - pageView.getHeight())));
                    }
                } else {
                    Oheight = bottom - top;
                    pageView.setY((Oheight - pageView.getHeight()) / 2);
                }
                pageView.requestLayout();
            }
        });
        if (!imageViews.isEmpty()) {
            LinkedHashMap<View, DraggableImageView> imageViewHashMap = new LinkedHashMap<>();
            for (DraggableImageView imageView : imageViews) {
                imageView.setVisibility(View.VISIBLE);
                ImageHoleV2 imageHole = imageView.getImageHoleV2();
                int top = Math.round(imageHole.getY() * scale);
                int left = Math.round(imageHole.getX() * scale);
                int width = Math.round(imageHole.getW() * scale);
                int height = Math.round(imageHole.getH() * scale);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                layoutParams.topMargin = top;
                layoutParams.leftMargin = left;
                imageView.setLayoutParams(layoutParams);
                View strokeView = new View(this);
                strokesLayout.addView(strokeView, layoutParams);
                imageViewHashMap.put(strokeView, imageView);
                if (imageView.getBitmap() != null) {
                    for (ImageInfoV2 imageInfo : cardPage.getImages()) {
                        if (imageInfo.getHoleId() != imageHole.getId()) {
                            continue;
                        }
                        TransInfo transInfo = imageInfo.getTransInfo();
                        imageView.setScaleType(ImageView.ScaleType.MATRIX);
                        imageView.setMatrix(getCoverMatrix(width,
                                height,
                                imageView.getBitmap()
                                        .getWidth(),
                                imageView.getBitmap()
                                        .getHeight(),
                                scale,
                                transInfo));
                        break;
                    }
                }

            }
            imageViewsContainer = new ImageViewsContainer(imageViewHashMap);
            imageViewsContainer.setDragHolder(ivDrag);
        }
        if (!textHoles.isEmpty()) {
            textsLayout.init(btnHide, btnDrag, this);
            for (TextHoleV2 textHole : textHoles) {
                PageTextView textView = new PageTextView(this);
                textView.setPageScale(scale);
                setTextView(textView, textHole);
                textView.setTextHole(textHole);
                textViews.add(textView);
            }
        }
        textsLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (TextView textView : textViews) {
                    textView.setBackgroundResource(R.drawable.bg_card_page_v2_text);
                }
            }
        }, 1000);
        pageView.getLayoutParams().height = pageViewH;
        pageView.getLayoutParams().width = pageViewW;
        ivBackground.setImageBitmap(bitmap);
    }

    private void setTextView(final PageTextView textView, TextHoleV2 textHole) {
        if (!JSONUtil.isEmpty(textHole.getFontName())) {
            for (Font font : CardResourceUtil.getInstance()
                    .getFonts(this)) {
                if (textHole.getFontName()
                        .equals(font.getName())) {
                    if (!font.isUnSaved(this)) {
                        try {
                            textView.setTypeface(Typeface.createFromFile(FileUtil.createFontFile(
                                    this,
                                    font.getUrl())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
        textView.setTextColor(textHole.getColor());
        textView.setIncludeFontPadding(false);
        textView.setBackgroundResource(R.drawable.bg_card_page_v2_text_drag);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textHole.getFontSize());
        int width = textHole.getW();
        int height = textHole.getH();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageTextView textView = (PageTextView) v;
                TextHoleV2 textHole = textView.getTextHole();
                if (JSONUtil.isEmpty(textHole.getType()) || "speech".equals(textHole.getType())) {
                    textsLayout.setTextView((PageTextView) v);
                } else {
                    Intent intent = new Intent(PageV2EditActivity.this,
                            CardV2InfoEditActivity.class);
                    intent.putExtra("card", card);
                    startActivityForResult(intent, Constants.RequestCode.CARD_EDIT);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
        if (JSONUtil.isEmpty(textHole.getType())) {
            String content = textHole.getContent();
            width = (int) textView.getPaint()
                    .measureText("最小");
            height = new StaticLayout("最小",
                    textView.getPaint(),
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0F,
                    0.0F,
                    false).getHeight();
            textView.setMyMinHeight(height);
            textView.setMyMinWidth(width);
            if (!JSONUtil.isEmpty(content)) {
                String[] texts = textHole.getContent()
                        .split("\\n");
                if (texts.length > 1) {
                    for (String lineText : texts) {
                        width = Math.max((int) textView.getPaint()
                                .measureText(lineText), width);
                    }
                } else {
                    width = Math.max((int) textView.getPaint()
                            .measureText(textHole.getContent()), width);
                }
                StaticLayout layout = new StaticLayout(textHole.getContent(),
                        textView.getPaint(),
                        width,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0F,
                        0.0F,
                        false);
                height = Math.max(height, layout.getHeight());
            }
        }
        int top = Math.round((textHole.getY() - (height - textHole.getH()) / 2) * scale + height
                * (scale - 1) / 2);
        int left = Math.round((textHole.getX() - (width - textHole.getW()) / 2) * scale + width *
                (scale - 1) / 2);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.topMargin = top;
        params.leftMargin = left;
        textsLayout.addView(textView, params);
        if (textHole.getAlignment() == 2) {
            textView.setGravity(Gravity.RIGHT | Gravity.TOP);
        } else if (textHole.getAlignment() == 1) {
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        } else {
            textView.setGravity(Gravity.LEFT | Gravity.TOP);
        }
        textView.setText(textHole.getContent());
        textView.setPivotX(width / 2);
        textView.setPivotY(height / 2);
        textView.setRotation(textHole.getAngle());
        textView.setScaleX(textHole.getScale() * scale);
        textView.setScaleY(textHole.getScale() * scale);
        textView.setHide(!textHole.isShowText());
    }

    private void showSelectImagePopup(final DraggableImageView draggableImageView) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (popupWindow == null) {
            popupWindow = new PopupWindow(this);
            View mView = View.inflate(this, R.layout.cover_menu, null);
            mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return false;
                }
            });
            popupWindow.setContentView(mView);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R
                    .color.transparent)));
            popupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageViewsContainer.setHolderShadow(draggableImageView, false);
            }
        });
        int h = Math.round(Util.dp2px(this, 20));
        popupWindow.setHeight(draggableImageView.getHeight() + h);
        popupWindow.setWidth(draggableImageView.getWidth());
        popupWindow.getContentView()
                .findViewById(R.id.select_btn)
                .setOnClickListener(new OnSelectClickListener(imageViews.indexOf
                        (draggableImageView)));
        imageViewsContainer.setHolderShadow(draggableImageView, true);
        try {
            popupWindow.showAsDropDown(draggableImageView, 0, -draggableImageView.getHeight() - h);
        } catch (Exception ignored) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void upLoadText() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        for (PageTextView textView : textViews) {
            TextHoleV2 textHole = textView.getTextHole();
            if (textHole != null) {
                textHole.setText(textView, scale);
                if (textView.isChange() || JSONUtil.isEmpty(textHole.getH5TextPath())) {
                    textView.setChange(false);
                    if (textHole.getWidth() > 0 && textHole.getHeight() > 0 && textHole
                            .isShowText()) {
                        textHole.setH5TextPath(null);
                        new CreateTextTask(textHole).executeOnExecutor(Constants.THEADPOOL);
                        return;
                    }
                }
            }
        }
        if (card.getId() == 0 && card.getSpeechPage() != null) {
            for (TextHoleV2 textHole : card.getSpeechPage()
                    .getTexts()) {
                if (JSONUtil.isEmpty(textHole.getH5TextPath())) {
                    if (textHole.getWidth() > 0 && textHole.getHeight() > 0) {
                        new CreateTextTask(textHole).executeOnExecutor(Constants.THEADPOOL);
                        return;
                    }
                }
            }
        }
        upLoadImage();
    }

    private void upLoadImage() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        for (DraggableImageView imageView : imageViews) {
            ImageHoleV2 imageHole = imageView.getImageHoleV2();
            if (imageHole != null) {
                imageHole.setTransInfoStr(getMatrix(imageView));
                if (imageView.isChange() || JSONUtil.isEmpty(imageHole.getH5ImagePath())) {
                    imageHole.setH5ImagePath(null);
                    imageView.setIsChange(false);
                    if (!imageHole.getPath()
                            .startsWith("http://")) {
                        File file = new File(imageHole.getPath());
                        new QiNiuUploadTask(this,
                                new ImageFileUploadListener(imageHole, imageView, file),
                                progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath
                                        .QINIU_IMAGE_URL),
                                file);
                    } else {
                        new CreateImageTask(imageHole,
                                imageView).executeOnExecutor(Constants.INFOTHEADPOOL);
                    }
                    return;
                }
            }
        }
        upLoadPageInfo();
    }

    private String getMatrix(DraggableImageView imageView) {
        float[] m = new float[9];
        Matrix matrix = new Matrix();
        Bitmap bitmap = imageView.getBitmap();
        if (bitmap != null) {
            float w = (float) bitmap.getWidth() / 2;
            float h = (float) bitmap.getHeight() / 2;
            float nw = (float) imageView.getMeasuredWidth() / 2;
            float nh = (float) imageView.getMeasuredHeight() / 2;
            matrix.set(imageView.getImageMatrix());
            float maxScale = Math.max(nw / w, nh / h);
            matrix.postScale(1 / maxScale, 1 / maxScale);
            matrix.getValues(m);
            float mScale = (float) Math.sqrt(Math.pow(m[0], 2) + Math.pow(m[3], 2));
            float angle = (float) Math.atan2(m[3], m[0]);
            float m0 = (float) Math.cos(angle);
            float m3 = (float) Math.sin(angle);
            float x = w * maxScale * mScale * m0 - h * maxScale * mScale * m3 - nw + m[2] *
                    maxScale;
            float y = h * maxScale * mScale * m0 + w * maxScale * mScale * m3 - nh + m[5] *
                    maxScale;
            x /= scale;
            y /= scale;
            m[2] = x;
            m[5] = y;
        }
        return m[0] + "," + m[3] + "," + m[1] + "," + m[4] + "," + m[2] + "," + m[5];
    }

    private void upLoadPageInfo() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject pageObject = getPageJson(cardPage);
            if (cardPage.isFront()) {
                jsonObject.put("front_page", pageObject);
            } else if (cardPage.isSpeech()) {
                jsonObject.put("speech_page", pageObject);
            } else {
                JSONArray pageArray = new JSONArray();
                pageArray.put(pageObject);
                jsonObject.put("pages", pageArray);
            }
            if (card.getId() > 0) {
                jsonObject.put("id", card.getId());
            } else if (card.getSpeechPage() != null) {
                jsonObject.put("speech_page", getPageJson(card.getSpeechPage()));
            }
            jsonObject.put("theme_id", card.getThemeId());
            jsonObject.put("title", card.getTitle());
            jsonObject.put("groom_name", card.getGroomName());
            jsonObject.put("bride_name", card.getBrideName());
            jsonObject.put("time", TimeUtil.getCardDateStr(card.getTime()));
            jsonObject.put("place", card.getPlace());
            jsonObject.put("latitude", card.getLatitude());
            jsonObject.put("longtitude", card.getLongitude());
            jsonObject.put("map_type", card.getMapType());
            String speech = card.getSpeech();
            CardPageV2 speechPage = cardPage.isSpeech() ? cardPage : card.getSpeechPage();
            if (speechPage != null) {
                for (TextHoleV2 textHole : card.getSpeechPage()
                        .getTexts()) {
                    if ("speech".equals(textHole.getType())) {
                        speech = textHole.getContent();
                    }
                }
            }
            jsonObject.put("speech", JSONUtil.isEmpty(speech) ? " " : speech);
        } catch (JSONException ignored) {
        }
        progressDialog.onLoadComplate();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                final boolean createCard = card.getId() == 0;
                card = new CardV2((JSONObject) object);
                if (cardPage.getId() > 0) {
                    if (cardPage.isFront()) {
                        cardPage.deletePageFile(PageV2EditActivity.this, card.getFrontPage());
                    } else if (cardPage.isSpeech()) {
                        cardPage.deletePageFile(PageV2EditActivity.this, card.getSpeechPage());
                    } else {
                        for (CardPageV2 page : card.getPages()) {
                            if (cardPage.getId()
                                    .equals(page.getId())) {
                                cardPage.deletePageFile(PageV2EditActivity.this, page);
                            }
                        }
                    }
                }
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.EventType.CARD_UPDATE_FLAG, card));
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setCancelable(false);
                    progressDialog.onComplate(new RoundProgressDialog.OnUpLoadComplate() {
                        @Override
                        public void onUpLoadCompleted() {
                            onSaveCompleted(createCard);
                        }
                    });
                } else {
                    onSaveCompleted(createCard);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressDialog.dismiss();
                Util.postFailToast(PageV2EditActivity.this,
                        returnStatus,
                        R.string.msg_err_page_edit,
                        network);
            }
        }, progressDialog).executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.CARD_V2_SAVE),
                jsonObject.toString());
    }

    private void onSaveCompleted(boolean createCard) {
        if (!createCard || card == null) {
            isChange = false;
            onBackPressed();
        } else {
            Intent intent = new Intent(PageV2EditActivity.this, CardV2EditActivity.class);
            intent.putExtra("card", card);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private JSONObject getPageJson(CardPageV2 cardPage) {
        JSONObject pageObject = new JSONObject();
        try {
            if (cardPage.getId() > 0) {
                pageObject.put("id", cardPage.getId());
            }
            pageObject.put("page_template_id",
                    cardPage.getTemplate()
                            .getId());
            JSONArray textArray = new JSONArray();
            if (cardPage.isSpeech()) {
                pageObject.put("hidden", cardPage.isHidden() ? 1 : 0);
            }
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

    private class CreateTextTask extends AsyncTask<String, Object, File> {

        private TextHoleV2 textHole;

        private CreateTextTask(TextHoleV2 textHole) {
            this.textHole = textHole;
        }

        @Override
        protected File doInBackground(String... params) {
            return CardImageUtil.createTextImage(PageV2EditActivity.this, textHole);
        }

        @Override
        protected void onPostExecute(File file) {
            if (file != null && file.length() > 0) {
                new QiNiuUploadTask(PageV2EditActivity.this,
                        new TextImageFileUploadListener(textHole, file),
                        progressDialog,
                        true).executeOnExecutor(Constants.THEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                        file);
            }
            super.onPostExecute(file);
        }
    }

    private class CreateImageTask extends AsyncTask<String, Object, File> {

        private ImageHoleV2 imageHole;
        private DraggableImageView imageView;

        private CreateImageTask(ImageHoleV2 imageHole, DraggableImageView imageView) {
            this.imageHole = imageHole;
            this.imageView = imageView;
        }

        @Override
        protected File doInBackground(String... params) {
            return CardImageUtil.createHoleImage(PageV2EditActivity.this,
                    imageHole,
                    imageView,
                    scale);
        }

        @Override
        protected void onPostExecute(File file) {
            if (file != null && file.length() > 0) {
                new QiNiuUploadTask(PageV2EditActivity.this,
                        new HoleImageFileUploadListener(imageHole, file),
                        progressDialog,
                        true).executeOnExecutor(Constants.THEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                        file);
            }
            super.onPostExecute(file);
        }
    }

    private class ImageHoldLoadTask extends AsyncTask<String, Bitmap, Bitmap> {

        private DraggableImageView imageView;
        private Context mContext;
        private TransInfo transInfo;

        private ImageHoldLoadTask(
                Context context, DraggableImageView imageView, TransInfo transInfo) {
            this.mContext = context;
            this.imageView = imageView;
            this.transInfo = transInfo;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String maskPath = params[0];
            if (!JSONUtil.isEmpty(maskPath)) {
                File file = FileUtil.createThemeFile(mContext, maskPath);
                publishProgress(JSONUtil.getImageFromUrl(mContext,
                        maskPath,
                        file,
                        pageWidth,
                        pageHeight,
                        Bitmap.Config.ALPHA_8));
            }
            String imagePath = params[1];
            if (!JSONUtil.isEmpty(imagePath)) {
                if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                    File file = FileUtil.createPageFile(mContext, imagePath);
                    return JSONUtil.getImageFromUrl(mContext,
                            imagePath,
                            file,
                            pageWidth,
                            pageHeight);
                } else {
                    try {
                        return JSONUtil.getImageFromPath(mContext.getContentResolver(),
                                imagePath,
                                pageWidth,
                                pageHeight,
                                Bitmap.Config.ARGB_8888);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                if (imageView.getVisibility() == View.VISIBLE) {
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    imageView.setMatrix(getCoverMatrix(imageView.getLayoutParams().width,
                            imageView.getLayoutParams().height,
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            scale,
                            transInfo));
                }
            }
            super.onPostExecute(bitmap);
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            if (values != null && values.length > 0) {
                imageView.setMask(values[0]);
            }
            super.onProgressUpdate(values);
        }
    }

    private Matrix getCoverMatrix(
            int coverWidth,
            int coverHeight,
            int bitmapWidth,
            int bitmapHeight,
            float scale,
            TransInfo transInfo) {
        float bitmapScale = Math.max((coverWidth / (float) bitmapWidth),
                (coverHeight / (float) bitmapHeight));
        Matrix matrix = new Matrix();
        if (transInfo == null) {
            transInfo = new TransInfo(new JSONObject());
        }
        float h = (float) bitmapHeight * bitmapScale / 2;
        float w = (float) bitmapWidth * bitmapScale / 2;
        float mScale = (float) Math.sqrt(Math.pow(transInfo.getA(), 2) + Math.pow(transInfo.getB(),
                2));
        float angle = (float) Math.atan2(transInfo.getB(), transInfo.getA());
        float m0 = (float) Math.cos(angle);
        float m3 = (float) Math.sin(angle);
        float nh = (float) coverHeight / 2;
        float nw = (float) coverWidth / 2;
        float[] m = new float[]{(float) transInfo.getA(), (float) transInfo.getC(), (float)
                (transInfo.getTx() * scale - w * mScale * m0 + h * mScale * m3 + nw) /
                bitmapScale, (float) transInfo.getB(), (float) transInfo.getD(), (float)
                (transInfo.getTy() * scale - h * mScale * m0 - w * mScale * m3 + nh) /
                bitmapScale, 0, 0, 1};
        matrix.setValues(m);
        matrix.postScale(bitmapScale, bitmapScale);
        return matrix;
    }


    private class OnSelectClickListener implements View.OnClickListener {

        private int position;

        private OnSelectClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,
                    Constants.RequestCode.PAGE_PHOTO_FROM_GALLERY + position);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            int position = requestCode - Constants.RequestCode.PAGE_PHOTO_FROM_GALLERY;
            if (position >= 0 && position < imageViews.size()) {
                final DraggableImageView imageView = imageViews.get(position);
                String path = JSONUtil.getImagePathForUri(data.getData(), this);
                if (!JSONUtil.isEmpty(path)) {
                    isChange = true;
                    ImageHoleV2 imageHole = imageView.getImageHoleV2();
                    if (imageHole != null) {
                        imageHole.setPath(path);
                        imageHole.setH5ImagePath(null);
                    }
                    imageView.setIsChange(true);
                    new ImageHoldLoadTask(this,
                            imageView,
                            null).executeOnExecutor(Constants.THEADPOOL, null, path);
                }
            } else if (requestCode == Constants.RequestCode.CARD_EDIT) {
                card = (CardV2) data.getSerializableExtra("card");
                isChange = true;
                for (PageTextView textView : textViews) {
                    TextHoleV2 textHole = textView.getTextHole();
                    if (textHole != null) {
                        switch (textHole.getType()) {
                            case "groom":
                                textView.setText(card.getGroomName());
                                break;
                            case "bride":
                                textView.setText(card.getBrideName());
                                break;
                            case "time":
                                if (card.getTime() != null) {
                                    textView.setText(Util.getCardTimeString(this, card.getTime()));
                                }
                                break;
                            case "location":
                                textView.setText(card.getPlace());
                                break;
                            case "lunar":
                                if (card.getTime() != null) {
                                    textView.setText(new Lunar(card.getTime()).toString());
                                }
                                break;
                        }
                    }
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        super.onPause();
    }

    private class TextImageFileUploadListener implements OnHttpRequestListener {

        private TextHoleV2 textHole;
        private File file;

        private TextImageFileUploadListener(TextHoleV2 textHoleV2, File file) {
            this.textHole = textHoleV2;
            this.file = file;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            JSONObject json = (JSONObject) obj;
            if (json != null) {
                String path = null;
                String domain = JSONUtil.getString(json, "domain");
                String key = JSONUtil.getString(json, "image_path");
                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                    path = domain + key;
                }
                if (!JSONUtil.isEmpty(path)) {
                    FileUtil.deleteFile(file);
                    textHole.setH5TextPath(path);
                    upLoadText();
                    return;
                }
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (isFinishing()) {
                return;
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }
    }

    private class HoleImageFileUploadListener implements OnHttpRequestListener {

        private ImageHoleV2 imageHole;
        private File file;

        private HoleImageFileUploadListener(ImageHoleV2 imageHole, File file) {
            this.imageHole = imageHole;
            this.file = file;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            JSONObject json = (JSONObject) obj;
            if (json != null) {
                String path = null;
                String domain = JSONUtil.getString(json, "domain");
                String key = JSONUtil.getString(json, "image_path");
                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                    path = domain + key;
                }
                if (!JSONUtil.isEmpty(path)) {
                    if (!file.renameTo(FileUtil.createPageFile(PageV2EditActivity.this, path))) {
                        FileUtil.deleteFile(file);
                    }
                    imageHole.setH5ImagePath(path);
                    upLoadImage();
                    return;
                }
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (isFinishing()) {
                return;
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }
    }

    private class ImageFileUploadListener implements OnHttpRequestListener {

        private ImageHoleV2 imageHole;
        private DraggableImageView imageView;
        private File file;

        private ImageFileUploadListener(
                ImageHoleV2 imageHole, DraggableImageView imageView, File file) {
            this.imageHole = imageHole;
            this.imageView = imageView;
            this.file = file;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            JSONObject json = (JSONObject) obj;
            if (json != null) {
                String domain = JSONUtil.getString(json, "domain");
                String key = JSONUtil.getString(json, "image_path");
                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                    final String path = domain + key;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FileUtil.copyFile(file.getAbsolutePath(),
                                    FileUtil.createPageFile(PageV2EditActivity.this, path)
                                            .getAbsolutePath());
                        }
                    }).start();
                    imageHole.setPath(path);
                    new CreateImageTask(imageHole,
                            imageView).executeOnExecutor(Constants.INFOTHEADPOOL);
                    return;
                }
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (isFinishing()) {
                return;
            }
            progressDialog.dismiss();
            Util.showToast(PageV2EditActivity.this, null, R.string.msg_err_image_upload);
        }
    }

    @Override
    protected void onFinish() {
        CardResourceUtil.getInstance()
                .removeFontAdapter(menuAdapter);
        for (DraggableImageView imageView : imageViews) {
            imageView.recycle();
        }
        super.onFinish();
    }
}
