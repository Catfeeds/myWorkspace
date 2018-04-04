package com.hunliji.hljnotelibrary.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.SelectedNoteInspirationAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.models.wrappers.CreateNoteResponse;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.fragments.SelectedNoteInspirationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

/**
 * 发布-完善信息
 * Created by chen_bin on 2017/6/23 0023.
 */
@RuntimePermissions
public class CreatePhotoNoteActivity extends HljBaseNoBarActivity implements
        OnItemClickListener<NoteInspiration> {

    @Override
    public String pageTrackTagName() {
        return "笔记发布";
    }

    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.btn_create)
    Button btnCreate;
    @BindView(R2.id.divider_view)
    View dividerView;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.empty_view)
    CardView emptyView;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.tv_notebook_type)
    TextView tvNotebookType;
    @BindView(R2.id.notebook_type_layout)
    LinearLayout notebookTypeLayout;
    @BindView(R2.id.scroll_view)
    NestedScrollView scrollView;

    private Dialog exitDialog;
    private CheckableLinearGroup cgGroup;
    private SelectedNoteInspirationAdapter adapter;
    private SectionsPagerAdapter pagerAdapter;

    private PopupWindow bookTypePopupWin;
    public HljRoundProgressDialog progressDialog;
    private GestureDetector gestureDetector;

    private SparseArray<SelectedNoteInspirationFragment> fragments;
    private int[] imageHeights;
    private Note note;
    private int imageWidth;
    private int currentCheckedId;

    public SubscriptionList uploadSubs;
    private HljHttpSubscriber createSub;

    public final static String ARG_NOTE = "note";
    public final static String ARG_MARK_ID = "mark_id";
    public final static String ARG_NOTEBOOK_TYPE = "notebook_type";
    private int oldVerticalOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_create_photo_note___note);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
    }

    private void initValues() {
        imageWidth = CommonUtil.getDeviceSize(this).x;
        fragments = new SparseArray<>();
        note = getIntent().getParcelableExtra(ARG_NOTE);
        if (note == null) {
            note = NotePrefUtil.getInstance(this)
                    .getNoteDraft();
            if (note == null) {
                note = new Note();
                note.setNoteType(Note.TYPE_NORMAL);
                note.setNotebookType(getIntent().getIntExtra(ARG_NOTEBOOK_TYPE,
                        NotebookType.TYPE_WEDDING_PHOTO));
            }
        }
        long markId = getIntent().getLongExtra(ARG_MARK_ID, 0); //标签页会传标签id
        if (markId > 0) {
            note.getNoteMarks()
                    .clear();
            NoteMark noteMark = new NoteMark();
            noteMark.setId(markId);
            note.getNoteMarks()
                    .add(noteMark);
        }
        etTitle.setText(note.getTitle());
        etTitle.setSelection(etTitle.length());
        etContent.setText(note.getContent());
        String str = null;
        switch (note.getNotebookType()) {
            case NotebookType.TYPE_WEDDING_PHOTO:
                currentCheckedId = R.id.cb_wedding_photos;
                str = getString(R.string.label_wedding_photos___note);
                break;
            case NotebookType.TYPE_WEDDING_PLAN:
                currentCheckedId = R.id.cb_wedding_plan;
                str = getString(R.string.label_wedding_plan___note);
                break;
            case NotebookType.TYPE_PRODUCT_PLAN:
                currentCheckedId = R.id.cb_products_plan;
                str = getString(R.string.label_products_plan___note);
                break;
            default:
                break;
        }
        tvNotebookType.setText(str);
        notebookTypeLayout.setVisibility(HljNote.isMerchant(this) ? View.GONE : View.VISIBLE);
    }

    private void initViews() {
        View footerView = View.inflate(this,
                R.layout.selected_note_inspiration_footer_item___note,
                null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyView.performClick();
            }
        });
        recyclerView.addItemDecoration(new SpacesItemDecoration());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SelectedNoteInspirationAdapter(this, note.getInspirations());
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                setExpanded(false, true);
                return false;
            }
        });
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean b = !CommonUtil.isCollectionEmpty(note.getInspirations()) || Math.abs(
                        verticalOffset) == appBarLayout.getTotalScrollRange();
                dividerView.setVisibility(b ? View.VISIBLE : View.GONE);
                if (oldVerticalOffset - verticalOffset < 0) {
                    hideKeyboard(null);
                }
                oldVerticalOffset = verticalOffset;
            }
        });
        setFragments();
        setShowEmptyView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.CHOOSE_PHOTO:
                    if (data == null) {
                        return;
                    }
                    Note tempNote = data.getParcelableExtra(NoteImageChooserActivity.ARG_NOTE);
                    if (tempNote == null) {
                        return;
                    }
                    adapter.setSelectedPosition(0);
                    adapter.setInspirations(tempNote.getInspirations());
                    setFragments();
                    setShowEmptyView();
                    setExpanded(true, true);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setFragments() {
        fragments.clear();
        List<NoteInspiration> inspirations = note.getInspirations();
        imageHeights = new int[inspirations.size()];
        for (int i = 0, size = inspirations.size(); i < size; i++) {
            NoteInspiration inspiration = inspirations.get(i);
            imageHeights[i] = Math.round(imageWidth * inspiration.getNoteMedia()
                    .getRatio());
        }
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        } else {
            pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(
                        int position, float positionOffset, int positionOffsetPixels) {
                    if (position == imageHeights.length - 1) {
                        return;
                    }
                    int height = (int) (imageHeights[position] * (1 - positionOffset) +
                            imageHeights[position + 1] * positionOffset);
                    ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                    params.height = height;
                    viewPager.setLayoutParams(params);
                }

                @Override
                public void onPageSelected(int position) {
                    adapter.setSelectedPosition(position);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        if (imageHeights.length > 0) {
            viewPager.getLayoutParams().height = imageHeights[0];
        }
        viewPager.setCurrentItem(0);
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SelectedNoteInspirationFragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = SelectedNoteInspirationFragment.newInstance(note.getInspirations()
                        .get(position));
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return note.getInspirations()
                    .size();
        }
    }

    private void setShowEmptyView() {
        if (CommonUtil.isCollectionEmpty(note.getInspirations())) {
            tvTitle.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int position, NoteInspiration inspiration) {
        hideKeyboard(null);
        setExpanded(true, true);
        viewPager.setCurrentItem(position);
    }

    @OnTextChanged(R2.id.et_title)
    void afterTitleTextChanged(Editable s) {
        note.setTitle(s.toString());
    }

    @OnTextChanged(R2.id.et_content)
    void afterContentTextChanged(Editable s) {
        note.setContent(s.toString());
    }

    @OnTouch({R2.id.et_title, R2.id.et_content})
    boolean onEditTextTouched(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        if (note.getId() > 0 || (CommonUtil.isCollectionEmpty(note.getInspirations()) &&
                TextUtils.isEmpty(
                note.getTitle()) && TextUtils.isEmpty(note.getContent()))) {
            super.onBackPressed();
            return;
        }
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }
        if (exitDialog == null) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_save_note_draft_title___note),
                    getString(R.string.label_save_note_draft_msg___note),
                    getString(R.string.label_save_to_local___note),
                    getString(R.string.label_exit___note),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CreatePhotoNoteActivity.super.onBackPressed();
                        }
                    });
        }
        exitDialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NotePrefUtil.getInstance(CreatePhotoNoteActivity.this)
                                .setNoteDraft(note);
                        CreatePhotoNoteActivity.super.onBackPressed();
                    }
                });
        exitDialog.show();
    }

    @OnClick(R2.id.empty_view)
    void onAddImages() {
        Intent intent = new Intent(this, NoteImageChooserActivity.class);
        intent.putExtra(NoteImageChooserActivity.ARG_NOTE, note);
        startActivityForResult(intent, HljNote.RequestCode.CHOOSE_PHOTO);
    }

    @OnClick(R2.id.notebook_type_layout)
    void onSelectNotebookType() {
        if (bookTypePopupWin != null && bookTypePopupWin.isShowing()) {
            bookTypePopupWin.dismiss();
            return;
        }
        if (bookTypePopupWin == null) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_select_notebook_type___note, null);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            bookTypePopupWin = new PopupWindow(view,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight(),
                    true);
            bookTypePopupWin.setContentView(view);
            bookTypePopupWin.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                    android.R.color.transparent)));
            bookTypePopupWin.setOutsideTouchable(true);
            bookTypePopupWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                }
            });
            cgGroup = view.findViewById(R.id.cg_group);
            cgGroup.setOnCheckedChangeListener(new CheckableLinearGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                    bookTypePopupWin.dismiss();
                    currentCheckedId = checkedId;
                    String str = null;
                    if (checkedId == R.id.cb_wedding_photos) {
                        str = getString(R.string.label_wedding_photos___note);
                        note.setNotebookType(NotebookType.TYPE_WEDDING_PHOTO);
                    } else if (checkedId == R.id.cb_wedding_plan) {
                        str = getString(R.string.label_wedding_plan___note);
                        note.setNotebookType(NotebookType.TYPE_WEDDING_PLAN);
                    } else if (checkedId == R.id.cb_products_plan) {
                        str = getString(R.string.label_products_plan___note);
                        note.setNotebookType(NotebookType.TYPE_PRODUCT_PLAN);
                    }
                    tvNotebookType.setText(str);
                }
            });
        }
        cgGroup.check(currentCheckedId);
        setBackgroundAlpha(0.94f);
        bookTypePopupWin.showAsDropDown(tvNotebookType,
                -bookTypePopupWin.getContentView()
                        .getMeasuredWidth() + tvNotebookType.getMeasuredWidth() - CommonUtil.dp2px(
                        this,
                        12),
                -tvNotebookType.getMeasuredHeight() + CommonUtil.dp2px(this,
                        12) - bookTypePopupWin.getContentView()
                        .getMeasuredHeight());
    }

    @OnClick(R2.id.btn_create)
    public void onCreate() {
        CreatePhotoNoteActivityPermissionsDispatcher.uploadPhotosWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void uploadPhotos() {
        if (CommonUtil.isCollectionEmpty(note.getInspirations())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_photos___note);
            return;
        }
        if (TextUtils.isEmpty(note.getTitle())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_title___note);
            return;
        }
        if (TextUtils.isEmpty(note.getContent())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_content___note);
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = DialogUtil.getRoundProgress(this);
        progressDialog.show();
        ArrayList<Photo> photos = new ArrayList<>();
        for (NoteInspiration inspiration : note.getInspirations()) {
            photos.add(inspiration.getNoteMedia()
                    .getPhoto());
        }
        if (uploadSubs != null) {
            uploadSubs.clear();
        }
        uploadSubs = new SubscriptionList();
        new PhotoListUploadUtil(this, photos, progressDialog, uploadSubs, new OnFinishedListener() {
            @Override
            public void onFinished(Object... objects) {
                createNote();
            }
        }).startUpload();
    }

    private void createNote() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        CommonUtil.unSubscribeSubs(createSub);
        createSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CreateNoteResponse>() {
                    @Override
                    public void onNext(final CreateNoteResponse response) {
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            createSucceed(response);
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                createSucceed(response);
                            }
                        });
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                })
                .build();
        if (note.getId() > 0) {
            NoteApi.editNoteObb(note)
                    .subscribe(createSub);
        } else {
            NoteApi.createNoteObb(note)
                    .subscribe(createSub);
        }
    }

    private void createSucceed(CreateNoteResponse response) {
        if (note.getId() > 0) {
            Intent intent = getIntent();
            intent.putExtra("is_edit", true);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra("note_id", response.getId());
            intent.putExtra("sync_success", response.isSyncSuccess());
            intent.putExtra("create_type", note.getNotebookType());
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CreatePhotoNoteActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setBackgroundAlpha(float bgAlpha) {
        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.alpha = bgAlpha;
            win.setAttributes(lp);
        }
    }

    public void setExpanded(boolean expanded, boolean animate) {
        appbar.setExpanded(expanded, animate);
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(CreatePhotoNoteActivity.this, 16);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.set(position > 0 ? space : 0, 0, 0, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bookTypePopupWin != null && bookTypePopupWin.isShowing()) {
            bookTypePopupWin.dismiss();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(createSub, uploadSubs);
    }
}