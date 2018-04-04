package me.suncloud.marrymemo.view.experience;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.experienceshop.ExperienceImageAdapter;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by hua_rong on 2017/3/31.
 * 体验店全部照片
 */

public class ExperienceShopPhotoActivity extends HljBaseActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.back_top_btn)
    ImageButton backTopBtn;
    private boolean isHide;
    private Handler mHandler;
    private StaggeredGridLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_shop_photo);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String title = getIntent().getStringExtra("title");
        setTitle(JSONUtil.isEmpty(title) ? getString(R.string.label_experience_real_scene) : title);
        int padding = CommonUtil.dp2px(this, 10);
        int offset = CommonUtil.dp2px(this, 6);
        ArrayList<Photo> photos = getIntent().getParcelableArrayListExtra("photos");
        mHandler = new Handler();
        if (CommonUtil.isCollectionEmpty(photos)) {
            emptyView.showEmptyView();
        } else {
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations
                    (false);
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            layoutManager.setItemPrefetchEnabled(false);
            StaggeredSpaceItemDecoration itemDecoration = new StaggeredSpaceItemDecoration(padding,
                    offset);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setBackgroundResource(R.color.colorWhite);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new ExperienceImageAdapter(this, photos));
            setOkText(getString(R.string.label_more_pages, String.valueOf(photos.size())));
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (layoutManager != null) {
                int[] into = layoutManager.findFirstVisibleItemPositions(new int[]{0, 1});
                if (into != null && into.length > 0) {
                    if (into[0] < 15) {
                        if (!isHide) {
                            hideFiltrateAnimation();
                        }
                    } else if (isHide) {
                        if (backTopBtn.getVisibility() == View.GONE) {
                            backTopBtn.setVisibility(View.VISIBLE);
                        }
                        showFiltrateAnimation();
                    }
                }
            }
        }
    };

    private void showFiltrateAnimation() {
        if (backTopBtn == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this,
                    com.hunliji.hljquestionanswer.R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopBtn.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopBtn != null && (backTopBtn.getAnimation() == null || backTopBtn.getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backTopBtn == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this,
                    com.hunliji.hljquestionanswer.R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopBtn.startAnimation(animation);
        }
    }

    @OnClick(R.id.back_top_btn)
    void scrollTop() {
        if (layoutManager != null) {
            int[] into = layoutManager.findFirstVisibleItemPositions(new int[]{0, 1});
            if (into != null && into.length > 0) {
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private class StaggeredSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int padding;
        private int offset;

        StaggeredSpaceItemDecoration(int padding, int offset) {
            this.padding = padding;
            this.offset = offset;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            if (params == null) {
                return;
            }
            int position = parent.getChildLayoutPosition(view);
            int size = parent.getAdapter()
                    .getItemCount();
            if (position > 1) {
                outRect.top = offset;
            } else {
                outRect.top = padding;
            }

            int span = params.getSpanIndex();
            if (span == 0) {
                //第一列
                outRect.right = Math.round(offset / 2);
                outRect.left = padding;
            }
            if (span == 1) {
                //第二列
                outRect.left = Math.round(offset / 2);
                outRect.right = padding;
            }

            if (position >= size - 2) {
                outRect.bottom = padding;
            } else {
                outRect.bottom = 0;
            }
        }
    }


}
