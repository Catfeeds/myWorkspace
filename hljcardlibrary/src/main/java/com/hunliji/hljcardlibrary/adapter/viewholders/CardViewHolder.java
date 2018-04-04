package com.hunliji.hljcardlibrary.adapter.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcardlibrary.views.activities.CardPreviewActivity;
import com.hunliji.hljcardlibrary.views.activities.CardWebActivity;
import com.hunliji.hljcardlibrary.views.activities.PartnerCardPreviewActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/6/13.
 */

public class CardViewHolder extends BaseViewHolder<Card> {

    @BindView(R2.id.iv_cover)
    ImageView ivCover;
    @BindView(R2.id.tv_copy_hint)
    TextView tvCopyHint;
    @BindView(R2.id.tv_partner)
    TextView tvPartner;
    private int width;
    private int height;
    private long userId;

    private Subscription hideSubscription;

    public CardViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item___card, parent, false));
    }

    private CardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        User user = UserSession.getInstance()
                .getUser(itemView.getContext());
        if (user != null) {
            userId = user.getId();
        }
        width = Math.round(CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                30)) / 2;
        height = Math.round(width * 122 / 75);
        itemView.getLayoutParams().height = height;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Card card = getItem();
                Context context = view.getContext();
                if (card != null) {
                    if (card.getUserId() > 0 && card.getUserId() != userId && userId > 0) {
                        //来自另一半的请帖只能预览
                        intent = new Intent(context, PartnerCardPreviewActivity.class);
                        intent.putExtra("path", card.getPreviewOnlyLink());
                        intent.putExtra("id", card.getId());
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, CardWebActivity.class);
                        intent.putExtra("id", getItem().getId());
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, final Card item, int position, int viewType) {
        String coverPath = null;
        File cardFile = PageImageUtil.getCardThumbFile(ivCover.getContext(), item.getId());
        if (cardFile == null || !cardFile.exists() || cardFile.length() == 0) {
            coverPath = item.getTheme()
                    .getThumbPath();
        } else {
            coverPath = cardFile.getAbsolutePath();
        }
        if (item.isClosed()) {
            ivCover.setColorFilter(Color.argb(178, 255, 255, 255));
        } else {
            ivCover.clearColorFilter();
        }
        if (item.isCopyCard()) {
            tvCopyHint.setVisibility(View.VISIBLE);
            if (hideSubscription == null || hideSubscription.isUnsubscribed()) {
                hideSubscription = Observable.timer(5, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                item.setCopyCard(null);
                                tvCopyHint.setVisibility(View.GONE);
                            }
                        });
            }
        } else {
            tvCopyHint.setVisibility(View.GONE);
            CommonUtil.unSubscribeSubs(hideSubscription);
        }
        Glide.with(ivCover.getContext())
                .load(ImagePath.buildPath(coverPath)
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(ivCover);
        if (item.getUserId() > 0 && item.getUserId() != userId && userId > 0) {
            tvPartner.setVisibility(View.VISIBLE);
        } else {
            tvPartner.setVisibility(View.GONE);
        }
    }
}
