package me.suncloud.marrymemo.widget.finder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.hunliji.hljcommonlibrary.animators.BaseItemAnimator;

public class FinderItemAnimator extends BaseItemAnimator {

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        view.setAlpha(0);
        view.setTranslationY(-view.getHeight() * 0.25f);
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        animation.alpha(1)
                .translationY(0)
                .setStartDelay(120)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .start();
    }
}
