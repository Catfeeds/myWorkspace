package com.hunliji.hljnotelibrary.views.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.models.NotebookType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 商家动态引导页
 * Created by chen_bin on 2016/7/15 0015.
 */
public class NoteEduActivity extends HljBaseNoBarActivity {
    @BindView(R2.id.img_header)
    ImageView imgHeader;
    @BindView(R2.id.tv_note_edu2_content4)
    TextView tvNoteEdu2Content4;
    @BindView(R2.id.tv_note_edu2_content5)
    TextView tvNoteEdu2Content5;
    @BindView(R2.id.tv_note_edu2_content6)
    TextView tvNoteEdu2Content6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edu___note);
        ButterKnife.bind(this);
        initValues();
    }

    private void initValues() {
        Point point = CommonUtil.getDeviceSize(this);
        imgHeader.getLayoutParams().width = point.x;
        imgHeader.getLayoutParams().height = Math.round(point.x / (750.0f / 314.0f));
        tvNoteEdu2Content4.setText(CommonUtil.fromHtml(this,
                getString(R.string.label_note_edu2_content4___note)));
        tvNoteEdu2Content5.setText(CommonUtil.fromHtml(this,
                getString(R.string.label_note_edu2_content5___note)));
        tvNoteEdu2Content6.setText(CommonUtil.fromHtml(this,
                getString(R.string.label_note_edu2_content6___note)));
    }

    @OnClick(R2.id.btn_confirm)
    void onConfirm() {
        int noteType = getIntent().getIntExtra("note_type", Note.TYPE_NORMAL);
        switch (noteType) {
            case Note.TYPE_VIDEO:
                setResult(RESULT_OK, getIntent());
                onBackPressed();
                break;
            case Note.TYPE_NORMAL:
                Intent intent = new Intent(this, CreatePhotoNoteActivity.class);
                intent.putExtra(CreatePhotoNoteActivity.ARG_NOTEBOOK_TYPE,
                        NotebookType.TYPE_WEDDING_PERSON);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                finish();
                break;
        }
    }
}