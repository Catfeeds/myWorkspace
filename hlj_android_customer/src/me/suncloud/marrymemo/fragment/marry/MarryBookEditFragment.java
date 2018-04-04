package me.suncloud.marrymemo.fragment.marry;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CloneUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.RecordBookAdapter;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.model.marry.RecordBook;
import me.suncloud.marrymemo.view.marry.IOnMarryBookEdit;
import me.suncloud.marrymemo.view.marry.MarryBookEditActivity;
import me.suncloud.marrymemo.widget.ExpandableLayout;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

/**
 * Created by hua_rong on 2017/12/8
 * 结婚记账编辑页面
 */
@RuntimePermissions
public class MarryBookEditFragment extends RefreshFragment implements RecordBookAdapter
        .OnRecordBookListener, DraggableImgGridAdapter.OnItemAddListener,
        OnItemClickListener<Photo>, DraggableImgGridAdapter.OnItemDeleteListener {

    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_view_child)
    RecyclerView recyclerViewChild;
    @BindView(R.id.expandable_layout)
    ExpandableLayout expandableLayout;
    @BindView(R.id.line_book)
    View lineBook;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.rv_image)
    RecyclerView rvImage;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.tv_take_photo)
    TextView tvTakePhoto;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    private RecordBookAdapter recordBookAdapter;
    private RecordBookAdapter childAdapter;
    private List<RecordBook> recordBookList;
    private HljHttpSubscriber subscriber;
    private List<RecordBook> recordBookCopy;//当前界面备份的列表数据
    private MarryBook marryBook;
    private long id;
    private long typeId;
    private int prePosition;
    private Unbinder unbinder;
    private IOnMarryBookEdit onMarryBookListener;

    public GridLayoutManager layoutManager;
    public RecyclerViewDragDropManager dragDropManager;
    public RecyclerView.Adapter imageAdapter;
    public ArrayList<Photo> photos;
    public HljRoundProgressDialog progressDialog;
    private static final int LIMIT_SIZE = 9;
    public int imageSize;
    private long itemId;
    private SubscriptionList uploadSubscriptions;
    private Dialog dialog;
    private static final int TYPE_CAMERA = 2000;
    private static final String TEMP_JPG = "temp.jpg";

    public static MarryBookEditFragment newInstance(MarryBook marryBook) {
        Bundle args = new Bundle();
        MarryBookEditFragment fragment = new MarryBookEditFragment();
        args.putParcelable(MarryBookEditActivity.ARG_MARRY_BOOK, marryBook);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_marry_book, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initChildView();
        initError();
        onLoad();
        initDraggedImageView();
    }

    private void initValue() {
        recordBookList = new ArrayList<>();
        recordBookCopy = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            marryBook = bundle.getParcelable(MarryBookEditActivity.ARG_MARRY_BOOK);
        }
        if (marryBook != null) {
            id = marryBook.getId();
            typeId = marryBook.getType()
                    .getId();
            photos = marryBook.getImages();
            for (Photo photo : photos) {
                photo.setId(++itemId);
            }
        } else {
            photos = new ArrayList<>();
        }
        imageSize = (CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                70)) / 4;
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    private void initView() {
        if (marryBook != null) {
            String remark = marryBook.getRemark();
            etRemark.setText(remark);
            etAmount.setText(String.format(Locale.getDefault(), "%.2f", marryBook.getMoney()));
            etAmount.setSelection(etAmount.length());
        }
        recyclerView.getLayoutParams().width = CommonUtil.getDeviceSize(getContext()).x;
        llDelete.setVisibility(id > 0 ? View.VISIBLE : View.GONE);
        recordBookAdapter = new RecordBookAdapter(getContext());
        recordBookAdapter.setRecordBookListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recordBookAdapter);
    }

    private void initDraggedImageView() {
        ((SimpleItemAnimator) rvImage.getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new GridLayoutManager(getContext(), 4);
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                getContext(),
                R.drawable.sp_dragged_shadow));
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(500);
        DraggableImgGridAdapter gridAdapter = new DraggableImgGridAdapter(getContext(),
                photos,
                imageSize,
                LIMIT_SIZE);
        gridAdapter.setOnItemAddListener(this);
        gridAdapter.setOnItemClickListener(this);
        gridAdapter.setOnItemDeleteListener(this);
        imageAdapter = dragDropManager.createWrappedAdapter(gridAdapter);
        rvImage.setLayoutManager(layoutManager);
        rvImage.setAdapter(imageAdapter);
        dragDropManager.attachRecyclerView(rvImage);
        addNewButtonAndRefresh();
    }

    private void initChildView() {
        childAdapter = new RecordBookAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewChild.setLayoutManager(linearLayoutManager);
        recyclerViewChild.setAdapter(childAdapter);
        childAdapter.setRecordBookListener(this);
    }

    public void addNewButtonAndRefresh() {
        if (photos.size() > 0) {
            tvTakePhoto.setVisibility(View.GONE);
            rvImage.setVisibility(View.VISIBLE);
        } else {
            rvImage.setVisibility(View.GONE);
            tvTakePhoto.setVisibility(View.VISIBLE);
        }
        int size = imageAdapter.getItemCount();
        int rows = 1;
        if (size > 8) {
            rows = 3;
        } else if (size > 4) {
            rows = 2;
        }
        rvImage.getLayoutParams().height = imageSize * rows + CommonUtil.dp2px(getContext(),
                rows * 10);
        imageAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_delete)
    void onDelete() {
        if (onMarryBookListener != null) {
            onMarryBookListener.onDelete(id);
        }
    }

    @SuppressWarnings("unchecked")
    private void onLoad() {
        CommonUtil.unSubscribeSubs(subscriber);
        subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecordBook>>>() {
                    @Override
                    public void onNext(HljHttpData<List<RecordBook>> listHljHttpData) {
                        List<RecordBook> httpData = listHljHttpData.getData();
                        recordBookList.clear();
                        recordBookList.addAll(httpData);
                        recordBookCopy = getClone(httpData, recordBookCopy);
                        for (RecordBook recordBook : recordBookList) {
                            List<RecordBook> childBooks = recordBook.getChildren();
                            Iterator<RecordBook> iterator = childBooks.iterator();
                            while (iterator.hasNext()) {
                                RecordBook record = iterator.next();
                                if (marryBook != null) {
                                    if (record.getId() > 0 && marryBook.getType()
                                            .getId() == record.getId()) {
                                        record.setSelect(true);
                                        recordBook.setSelect(true);
                                        recordBook.setTitle(marryBook.getType()
                                                .getTitle());
                                        recordBook.setSelectedImagePath(marryBook.getType()
                                                .getSelectedImagePath());
                                    }
                                }
                                if (record.isHidden()) {
                                    iterator.remove();
                                }
                            }
                        }
                        if (marryBook == null && !CommonUtil.isCollectionEmpty(recordBookList)) {
                            recordBookList.get(0)
                                    .setSelect(true);
                            typeId = recordBookCopy.get(0)
                                    .getChildren()
                                    .get(0)
                                    .getId();
                        }
                        recordBookAdapter.setRecordBookList(recordBookList,
                                RecordBookAdapter.PARENT);
                    }
                })
                .setContentView(scrollView)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .build();
        MarryApi.getBookSortTypes()
                .subscribe(subscriber);
    }

    @OnClick(R.id.btn_save)
    public void onSubmit() {
        if (etAmount.length() == 0) {
            ToastUtil.showToast(getContext(), "金额必须大于0", 0);
            return;
        }
        if (typeId == 0) {
            return;
        }
        if (photos != null && !photos.isEmpty()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = DialogUtil.getRoundProgress(getContext());
            progressDialog.show();
            if (uploadSubscriptions != null) {
                uploadSubscriptions.clear();
            }
            uploadSubscriptions = new SubscriptionList();
            new PhotoListUploadUtil(getContext(),
                    photos,
                    progressDialog,
                    uploadSubscriptions,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            onSave();
                        }
                    }).startUpload();
        } else {
            onSave();
        }
    }

    private void onSave() {
        double money = Double.valueOf(etAmount.getText()
                .toString()
                .trim());
        String remark = etRemark.getText()
                .toString()
                .trim();
        if (onMarryBookListener != null) {
            onMarryBookListener.onSave(money, remark, typeId, id, null, photos);
        }
    }

    public List<RecordBook> getClone(List<RecordBook> input, List<RecordBook> result) {
        result.clear();
        for (RecordBook recordBook : input) {
            try {
                result.add((RecordBook) CloneUtil.deepClone(recordBook));
            } catch (Exception e) {
                e.printStackTrace();
                return input;
            }
        }
        return result;
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        if (onMarryBookListener != null) {
            onMarryBookListener.onCancel();
        }
    }

    @Override
    public void onItemClick(List<RecordBook> recordBooks, int position) {
        hideKeyboard();
        updateRecordBookList();
        int childPosition = getSelectChildPosition(recordBooks);
        setRecordBook(recordBooks.get(childPosition), position);
        if (position != prePosition) {
            prePosition = position;
            if (!expandableLayout.isExpanded()) {
                setExpandStatus(true);
            }
            recyclerViewChild.scrollToPosition(0);
        } else {
            if (!expandableLayout.isExpanded()) {
                setExpandStatus(true);
            } else {
                setExpandStatus(false);
            }
        }
        recordBookAdapter.setRecordBookList(recordBookList, RecordBookAdapter.PARENT);
        childAdapter.setFatherPosition(position);
        childAdapter.setRecordBookList(recordBooks, RecordBookAdapter.CHILD);
    }

    @Override
    public void onItemSelect(
            RecordBook recordBook, int fatherPosition, int recyclerType, int position) {
        hideKeyboard();
        updateRecordBookList();
        switch (recyclerType) {
            case RecordBookAdapter.CHILD:
                if (recordBookList.size() > fatherPosition) {
                    setRecordBook(recordBook, fatherPosition);
                    childAdapter.notifyDataSetChanged();
                }
                break;
            case RecordBookAdapter.PARENT:
                if (recordBookCopy.size() > position) {
                    RecordBook book = recordBookCopy.get(position);
                    if (book != null && !CommonUtil.isCollectionEmpty(book.getChildren())) {
                        //为第二级 child的type_id
                        typeId = book.getChildren()
                                .get(0)
                                .getId();
                    }
                    recordBookList.get(position)
                            .setSelect(true);
                    prePosition = position;
                }
                break;
        }
        if (expandableLayout.isExpanded()) {
            setExpandStatus(false);
        }
        recordBookAdapter.setRecordBookList(recordBookList, RecordBookAdapter.PARENT);
    }

    public void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = ((InputMethodManager) getActivity().getSystemService
                    (Activity.INPUT_METHOD_SERVICE));
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setRecordBook(RecordBook recordBook, int fatherPosition) {
        RecordBook record = recordBookList.get(fatherPosition);
        record.setTitle(recordBook.getTitle());
        record.setSelectedImagePath(recordBook.getSelectedImagePath());
        record.setSelect(true);
        recordBook.setSelect(true);
        typeId = recordBook.getId();
    }

    private void setExpandStatus(boolean expand) {
        recordBookAdapter.setExpand(expand);
        lineBook.setVisibility(expand ? View.GONE : View.VISIBLE);
        if (expand) {
            expandableLayout.expand();
        } else {
            expandableLayout.collapse();
        }
    }

    /**
     * 更新记账列表
     */
    private void updateRecordBookList() {
        recordBookList = getClone(recordBookCopy, recordBookList);
        for (RecordBook book : recordBookList) {
            List<RecordBook> childBooks = book.getChildren();
            Iterator<RecordBook> iterator = childBooks.iterator();
            while (iterator.hasNext()) {
                RecordBook record = iterator.next();
                if (record.isHidden()) {
                    iterator.remove();
                }
            }
        }
    }

    private int getSelectChildPosition(List<RecordBook> recordBooks) {
        for (RecordBook recordBook : recordBooks) {
            if (recordBook.getId() == typeId) {
                return recordBooks.indexOf(recordBook);
            }
        }
        return 0;
    }

    @OnClick(R.id.tv_take_photo)
    void onTakePhoto() {
        showPopup();
    }

    @Override
    public void onItemAdd(Object... objects) {
        showPopup();
    }

    @Override
    public void onItemClick(int position, Photo photo) {
        if (photos != null && !photos.isEmpty()) {
            Intent intent = new Intent(getContext(), PicsPageViewActivity.class);
            intent.putExtra("photos", photos);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    @Override
    public void onItemDelete(int position) {
        photos.remove(position);
        addNewButtonAndRefresh();
    }

    public void showPopup() {
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.hlj_dialog_add_menu___cm);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.findViewById(R.id.action_gallery)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (photos.size() == LIMIT_SIZE) {
                                ToastUtil.showToast(getContext(),
                                        null,
                                        R.string.hint_choose_photo_limit_out);
                                return;
                            }
                            Intent intent = new Intent(getContext(), ImageChooserActivity.class);
                            intent.putExtra("limit", LIMIT_SIZE - photos.size());
                            startActivityForResult(intent,
                                    Constants.RequestCode.PHOTO_FROM_GALLERY);
                            dialog.dismiss();
                        }
                    });
            dialog.findViewById(R.id.action_camera_photo)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (photos.size() == LIMIT_SIZE) {
                                ToastUtil.showToast(getContext(),
                                        null,
                                        R.string.hint_choose_photo_limit_out);
                                return;
                            }
                            MarryBookEditFragmentPermissionsDispatcher.onTakePhotosWithCheck(
                                    MarryBookEditFragment.this);

                        }
                    });
            Window win = dialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(getContext());
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        dialog.show();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(getContext(),
                request,
                getString(R.string.msg_permission_r_for_camera___cm));
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getContext(),
                    getContext().getPackageName(),
                    new File(Environment.getExternalStorageDirectory(), TEMP_JPG));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), TEMP_JPG));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TYPE_CAMERA);
        dialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MarryBookEditFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCode.PHOTO_FROM_GALLERY && data != null) {
                ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra
                        ("selectedPhotos");
                if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
                    for (Photo photo : selectedPhotos) {
                        itemId++;
                        photo.setId(itemId);
                        photos.add(photo);
                    }
                    addNewButtonAndRefresh();
                }
            }
            if (requestCode == TYPE_CAMERA) {
                String path = Environment.getExternalStorageDirectory() + File.separator + TEMP_JPG;
                File file = new File(path);
                Photo photo = new Photo();
                itemId++;
                photo.setImagePath(file.getPath());
                photo.setId(itemId);
                photos.add(photo);
                addNewButtonAndRefresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setOnMarryBookListener(IOnMarryBookEdit onMarryBookListener) {
        this.onMarryBookListener = onMarryBookListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (imageAdapter != null) {
            WrapperAdapterUtils.releaseAll(imageAdapter);
            imageAdapter = null;
        }
        layoutManager = null;
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(subscriber, uploadSubscriptions);
    }

}
