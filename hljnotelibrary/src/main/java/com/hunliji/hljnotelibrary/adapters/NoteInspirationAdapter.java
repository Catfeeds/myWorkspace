package com.hunliji.hljnotelibrary.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.models.note.NoteSpotEntity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.NoteCirclePageIndicator;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.hunliji.hljnotelibrary.views.widgets.NoteInspirationView;
import com.hunliji.hljnotelibrary.views.widgets.TagViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteInspirationAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private List<NoteInspiration> mDate;
    private Context mContext;
    private int imageWidth;
    private int imageHeight;
    private ViewPager mViewPager;
    private ImageView mImgInspirationCollect;
    private int[] imgHeights;
    private OnCollectClickListener onCollectClickListener;
    private ImageView imgBottomCollect;
    private NoteCirclePageIndicator mFlowIndicator;
    private AnimatorSet animSet;

    public NoteInspirationAdapter(
            Context context,
            List<NoteInspiration> list,
            ViewPager viewPager,
            NoteCirclePageIndicator flowIndicator,
            ImageView imgInspirationCollect) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mViewPager = viewPager;
        mFlowIndicator = flowIndicator;
        mImgInspirationCollect = imgInspirationCollect;
        mDate = new ArrayList<>();
        if (list != null) {
            mDate.addAll(list);
        }
        imageWidth = CommonUtil.getDeviceSize(mContext).x;
        initViewpager();
    }

    public void setDate(List<NoteInspiration> noteInspirations) {
        if (noteInspirations != null) {
            mDate.clear();
            mDate.addAll(noteInspirations);
            initViewpager();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (imgHeights == null || imgHeights.length != mDate.size()) {
            imgHeights = null;
            imgHeights = new int[mDate.size()];
        }
        return mDate == null ? 0 : mDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View convertView = mInflater.inflate(R.layout.note_inspiration_item___note, null);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final NoteInspiration item = mDate.get(position);
        if (holder == null) {
            holder = new ViewHolder(convertView, item, position);
            convertView.setTag(holder);
        }
        if (item != null) {
            holder.noteInspirationView.loadImage(item.getNoteMedia()
                    .getPhoto()
                    .getImagePath(), imageWidth, imageHeight);
            holder.noteInspirationView.setDate(item.getNoteSpots());
            final ViewHolder finalHolder = holder;
            final GestureDetector gestureDetector = new GestureDetector(mContext,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            finalHolder.noteInspirationView.executeTagsAnimation();
                            return super.onSingleTapUp(e);
                        }

                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            if (onCollectClickListener != null) {
                                onCollectClickListener.onCollect(position,
                                        item,
                                        mImgInspirationCollect);
                            }
                            return super.onDoubleTapEvent(e);
                        }

                    });
            holder.noteInspirationView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        if (!HljNote.isMerchant(mContext) && position == 0 && mDate.size() > 1) {
            showScrollHint(holder);
        }
        container.addView(convertView);
        return convertView;
    }

    private void initViewpager() {
        if (CommonUtil.isCollectionEmpty(mDate)) {
            return;
        }
        if (HljNote.isMerchant(mContext)) {
            mImgInspirationCollect.setVisibility(View.GONE);
        } else {
            mImgInspirationCollect.setVisibility(View.VISIBLE);
            final NoteInspiration noteInspiration = mDate.get(0);
            mImgInspirationCollect.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onCollectClickListener != null) {
                        onCollectClickListener.onCollect(0,
                                noteInspiration,
                                mImgInspirationCollect);
                    }
                }
            });
            if (noteInspiration.isFollowed()) {
                mImgInspirationCollect.setImageResource(R.mipmap.icon_collect_primary_72_72);

            } else {
                mImgInspirationCollect.setImageResource(R.mipmap.icon_collect_white_72_72);
            }
        }
        //为ViewPager设置高度
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = Math.round(imageWidth * mDate.get(0)
                .getNoteMedia()
                .getRatio());
        mViewPager.setLayoutParams(params);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {
                if (position == imgHeights.length - 1) {
                    return;
                }
                //计算ViewPager现在应该的高度,heights[]表示页面高度的数组。
                final int height = (int) ((imgHeights[position] == 0 ? imageWidth :
                        imgHeights[position]) * (1 - positionOffset) + (imgHeights[position + 1]
                        == 0 ? imageWidth : imgHeights[position + 1]) * positionOffset);
                //为ViewPager设置高度
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mViewPager == null) {
                            return;
                        }
                        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
                        params.height = height;
                        mViewPager.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onPageSelected(final int position) {
                mFlowIndicator.setCurrentItem(position);
                if (mContext instanceof NoteDetailActivity) {
                    if (imgBottomCollect != null) {
                        imgBottomCollect.setImageResource(mDate.get(position)
                                .isFollowed() ? R.mipmap.icon_collect_primary_40_40 : R.mipmap
                                .icon_collect_white_40_40);
                    }
                    mImgInspirationCollect.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (onCollectClickListener != null) {
                                onCollectClickListener.onCollect(position,
                                        mDate.get(position),
                                        mImgInspirationCollect);
                            }
                        }
                    });
                    if (mDate.get(position)
                            .isFollowed()) {
                        mImgInspirationCollect.setImageResource(R.mipmap
                                .icon_collect_primary_72_72);
                    } else {
                        mImgInspirationCollect.setImageResource(R.mipmap.icon_collect_white_72_72);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class ViewHolder {
        @BindView(R2.id.note_inspiration_view)
        NoteInspirationView noteInspirationView;
        @BindView(R2.id.scroll_hint_layout)
        View scrollHintLayout;
        @BindView(R2.id.scroll_hint_circle_view)
        View scrollHintCircleView;
        @BindView(R2.id.img_scroll_hint)
        ImageView imgScrollHint;

        ViewHolder(View view, final NoteInspiration noteInspiration, int position) {
            ButterKnife.bind(this, view);
            imageHeight = Math.round(imageWidth * noteInspiration.getNoteMedia()
                    .getRatio());
            imgHeights[position] = imageHeight;
            noteInspirationView.setOnTagGroupClickListener(new TagViewGroup
                    .OnTagGroupClickListener() {
                @Override
                public void onCircleClick(TagViewGroup container) {

                }

                @Override
                public void onTagClick(TagViewGroup container, ITagView tag, int position) {
                    //商家端标签不可点击
                    if (!HljNote.isMerchant(mContext)) {
                        NoteSpot noteSpot = noteInspiration.getNoteSpots()
                                .get(noteInspirationView.getNoteSpotPosition(container));
                        onTagItemClick(noteSpot, position);
                    }
                }

                @Override
                public void onLongPress(TagViewGroup container) {

                }
            });
        }
    }

    public void onTagItemClick(NoteSpot noteSpot, int position) {
        NoteSpotEntity noteSpotEntity = noteSpot.getNoteSpotEntity();
        NoteMark noteMark = noteSpot.getNoteMark();
        switch (position) {
            case 0:
                if (!TextUtils.isEmpty(noteSpotEntity.getTitle())) {
                    if (noteSpotEntity.getId() == 0) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.NEW_SEARCH_RESULT_ACTIVITY)
                                .withString(NewSearchApi.ARG_KEY_WORD, noteSpotEntity.getTitle())
                                .withSerializable(NewSearchApi.ARG_SEARCH_TYPE,
                                        NewSearchApi.SearchType.SEARCH_TYPE_NOTE)
                                .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                                .navigation(mContext);
                    } else if (noteSpotEntity.getType()
                            .equals(NoteSpotEntity.TYPE_MERCHANT)) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                .withLong("id", noteSpotEntity.getId())
                                .navigation(mContext);
                    } else if (noteSpotEntity.getType()
                            .equals(NoteSpotEntity.TYPE_SHOP_PRODUCT)) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                                .withLong("id", noteSpotEntity.getId())
                                .navigation(mContext);
                    }
                } else if (!TextUtils.isEmpty(noteMark.getName())) {
                    goNoteMarkDetail(noteMark);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(noteSpotEntity.getTitle()) && !TextUtils.isEmpty(noteMark
                        .getName())) {
                    goNoteMarkDetail(noteMark);
                }
                break;
        }
    }

    public void goNoteMarkDetail(NoteMark noteMark) {
        if (noteMark.getId() != 0) {
            Intent intent = new Intent(mContext, NoteMarkDetailActivity.class);
            intent.putExtra(NoteMarkDetailActivity.ARG_MARK_ID, noteMark.getId());
            mContext.startActivity(intent);
        } else {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.NEW_SEARCH_RESULT_ACTIVITY)
                    .withString(NewSearchApi.ARG_KEY_WORD, noteMark.getName())
                    .withSerializable(NewSearchApi.ARG_SEARCH_TYPE,
                            NewSearchApi.SearchType.SEARCH_TYPE_NOTE)
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(mContext);
        }
    }

    public void setImgBottomCollect(ImageView imgBottomCollect) {
        this.imgBottomCollect = imgBottomCollect;
    }

    public interface OnCollectClickListener {
        void onCollect(int position, NoteInspiration noteInspiration, ImageView collectView);
    }

    public void setOnCollectClickListener(OnCollectClickListener onCollectClickListener) {
        this.onCollectClickListener = onCollectClickListener;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private void showScrollHint(final ViewHolder holder) {
        boolean isCollectHintClicked = NotePrefUtil.getInstance(mContext)
                .isNoteCollectHintClicked();
        boolean isScrollHintClicked = NotePrefUtil.getInstance(mContext)
                .isNoteScrollHintClicked();
        if (isCollectHintClicked && !isScrollHintClicked) {
            holder.scrollHintLayout.setVisibility(View.VISIBLE);
            holder.scrollHintLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    NotePrefUtil.getInstance(mContext)
                            .setNoteScrollHintClicked(true);
                    holder.scrollHintLayout.setVisibility(View.GONE);
                    if (animSet != null) {
                        animSet.end();
                    }
                    return true;
                }
            });
            holder.scrollHintCircleView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (holder.scrollHintCircleView == null || holder.imgScrollHint == null) {
                        return;
                    }
                    startHintScrollAnimator(holder.scrollHintCircleView,
                            holder.imgScrollHint.getMeasuredWidth());
                }
            }, 500);
        } else {
            holder.scrollHintLayout.setVisibility(View.GONE);
        }
    }

    public void startHintScrollAnimator(View view, float width) {
        float startX = view.getLeft();
        float endX = view.getLeft() - width;
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "x", startX, endX);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f);
        anim1.setRepeatCount(1000);
        anim2.setRepeatCount(1000);
        animSet = new AnimatorSet();
        animSet.setDuration(1500);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.play(anim1)
                .with(anim2);
        animSet.start();
    }

}