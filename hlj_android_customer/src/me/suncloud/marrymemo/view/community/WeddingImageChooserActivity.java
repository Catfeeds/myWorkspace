package me.suncloud.marrymemo.view.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/5/10.
 */

public class WeddingImageChooserActivity extends ImageChooserActivity {


    private boolean isFirstChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstChoose = getIntent().getBooleanExtra("isFirstChoose", false);
    }

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        super.initActionBarView(actionParent);
        try {
            TextView item = (TextView) actionView.findViewById(R.id.item);
            item.setText(R.string.btn_delete_wedding_photo);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.createDoubleButtonDialog(WeddingImageChooserActivity.this,
                            getString(R.string.label_delete_wedding_photo_hint),
                            getString(R.string.msg_delete_wedding_photo_hint),
                            getString(R.string.label_delete),
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = getIntent();
                                    intent.putExtra("is_deleted", true);
                                    setResult(RESULT_OK, intent);
                                    onBackPressed();
                                }
                            },
                            null)
                            .show();
                }
            });
            item.setVisibility(CommonUtil.isCollectionEmpty(getSelectedPaths()) ? View.GONE :
                    View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View initHeaderView() {
        boolean isFirstChoose = getIntent().getBooleanExtra("isFirstChoose", false);
        if (!CommonUtil.isCollectionEmpty(getSelectedPaths())) {
            return super.initHeaderView();
        }
        View view = View.inflate(this, R.layout.wedding_choose_photo_header, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_header_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_header_sub_title);
        if (isFirstChoose) {
            tvTitle.setText("选择第一组婚纱照");
            tvSubTitle.setText("分享你最美的瞬间");
        } else {
            tvTitle.setText("请选择一组婚纱照");
            tvSubTitle.setText("继续选择");
        }
        return view;
    }

    @Override
    protected boolean onBackPressedCheck() {
        if (isFirstChoose) {
            Intent intent = getIntent();
            intent.putExtra("isBackCreate", true);
            setResult(RESULT_OK, intent);
        }
        return true;
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
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("selectedPhotos", selectedPhotos);
        intent.putExtra("isFirstChoose", isFirstChoose);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0,R.anim.slide_out_right);
    }
}
