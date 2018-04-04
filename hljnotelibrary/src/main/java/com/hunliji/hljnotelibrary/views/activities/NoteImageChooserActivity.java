package com.hunliji.hljnotelibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.models.NotebookType;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记选图
 * Created by wangtao on 2017/7/22.
 */
public class NoteImageChooserActivity extends ImageChooserActivity {
    private Note note;
    private List<Photo> onlineItems;
    private List<String> noteImagePaths;

    public final static String ARG_NOTE = "note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initValues();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        super.initActionBarView(actionParent);
        ((ImageButton) actionParent.findViewById(R.id.btn_back)).setImageResource(R.mipmap
                .icon_cross_close_gray_32_32);
    }

    private void initValues() {
        limit = 9;
        note = getIntent().getParcelableExtra(ARG_NOTE);
        initNoteImages(note.getInspirations());
    }

    private void initNoteImages(List<NoteInspiration> inspirations) {
        noteImagePaths = new ArrayList<>();
        onlineItems = new ArrayList<>();
        for (NoteInspiration inspiration : inspirations) {
            String url = null;
            try {
                url = inspiration.getNoteMedia()
                        .getPhoto()
                        .getImagePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(url)) {
                continue;
            }
            if (CommonUtil.isHttpUrl(url)) {
                onlineItems.add(inspiration.getNoteMedia()
                        .getPhoto());
            }
            noteImagePaths.add(url);
        }
    }

    @Override
    public ArrayList<String> getSelectedPaths() {
        return (ArrayList<String>) noteImagePaths;
    }

    @Override
    protected View initHeaderView() {
        if (note.getId() > 0 || note.getNotebookType() == NotebookType.TYPE_WEDDING_PERSON) {
            return super.initHeaderView();
        }
        View view = View.inflate(this, R.layout.note_choose_photo_header___note, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSubTitle = view.findViewById(R.id.tv_sub_title);
        tvTitle.setText("与百万新人晒幸福、抢头条");
        tvSubTitle.setText("要想上头条，美照少不了");
        return view;
    }

    @Override
    public void onSelectedCountChange(int selectedCount) {
        super.onSelectedCountChange(selectedCount);
        if (selectedCount > limit) {
            showNotice(getString(R.string.label_note_update_image_limit_hint___note));
        }
    }

    @Override
    public int getSelectedMode() {
        return SelectedMode.COUNT;
    }

    @Override
    public boolean isTakeAble() {
        return false;
    }

    @Override
    public List<Photo> getExtraMedias() {
        return onlineItems;
    }

    @Override
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        if (CommonUtil.isCollectionEmpty(selectedPhotos)) {
            return;
        }
        List<NoteInspiration> inspirations = new ArrayList<>();
        List<NoteInspiration> tempInspirations = new ArrayList<>();
        tempInspirations.addAll(note.getInspirations());
        for (Photo photo : selectedPhotos) {
            boolean match = false;
            for (NoteInspiration tempInspiration : tempInspirations) {
                if (photo.getImagePath()
                        .equalsIgnoreCase(tempInspiration.getNoteMedia()
                                .getPhoto()
                                .getImagePath())) {
                    match = true;
                    inspirations.add(tempInspiration);
                    break;
                }
            }
            if (!match) {
                NoteInspiration inspiration = new NoteInspiration();
                inspiration.getNoteMedia()
                        .setType(Media.TYPE_PHOTO);
                inspiration.getNoteMedia()
                        .setPhoto(photo);
                inspirations.add(inspiration);
            }
        }
        note.setInspirations(inspirations);
        Intent intent = getIntent();
        intent.putExtra(ARG_NOTE, note);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}