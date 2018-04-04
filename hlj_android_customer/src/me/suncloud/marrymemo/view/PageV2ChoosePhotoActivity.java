package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ImageInfoV2;
import me.suncloud.marrymemo.model.V2.TemplateV2;

/**
 * Created by Suncloud on 2016/4/27.
 */
public class PageV2ChoosePhotoActivity extends ImageChooserActivity {

    private TextView tvSelectedPics;
    private Button btnMaking;
    private CardV2 card;
    private TemplateV2 template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        template = (TemplateV2) getIntent().getSerializableExtra("template");
        card = (CardV2) getIntent().getSerializableExtra("card");
    }

    @Override
    public boolean isTakeAble() {
        return false;
    }

    @Override
    public void initBottomBarView(ViewGroup bottomParent) {
        bottomView = View.inflate(this, R.layout.page_choose_photo_activity_bottom, bottomParent);

        btnMaking = (Button) bottomView.findViewById(R.id.choose_ok);
        tvSelectedPics = (TextView) bottomView.findViewById(R.id.tv_selected_pics);
        tvSelectedPics.setText(getString(R.string.label_page_photo_limit2, limit));
        btnMaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseOk(adapter.getSelectedPhotos());
            }
        });
    }

    @Override
    public void onSelectedCountChange(int selectCount) {
        if (selectCount > 0) {
            btnMaking.setEnabled(true);
            tvSelectedPics.setText(getString(R.string.label_selected_pic,
                    selectCount + "/" + limit));
        } else {
            btnMaking.setEnabled(false);
            tvSelectedPics.setText(getString(R.string.label_page_photo_limit2, limit));
        }
    }

    @Override
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        CardPageV2 cardPage;
        if (card.getId() > 0 || template != null) {
            cardPage = new CardPageV2(new JSONObject());
            cardPage.setTemplate(template);
        } else {
            cardPage = card.getFrontPage();
        }
        for (int i = 0, size = selectedPhotos.size(); i < size; i++) {
            ImageInfoV2 imageInfo = new ImageInfoV2(new JSONObject());
            imageInfo.setHoleId(cardPage.getImageHoles()
                    .get(i)
                    .getId());
            imageInfo.setImagePath(selectedPhotos.get(i)
                    .getImagePath());
            cardPage.getImages()
                    .add(imageInfo);
        }
        Intent intent = new Intent(this, PageV2EditActivity.class);
        intent.putExtra("card", card);
        intent.putExtra("cardPage", cardPage);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        finish();
    }
}
