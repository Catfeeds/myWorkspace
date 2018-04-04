package com.hunliji.marrybiz.view.comment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.comment.CommentListFragment;
import com.hunliji.marrybiz.fragment.comment.CommentQuestionFragment;
import com.hunliji.marrybiz.fragment.comment.CommentaryFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/4/14.
 * 评论管理
 */

public class CommentManagerActivity extends HljBaseActivity {

    private CommentaryFragment commentaryFragment;//评价概述
    private CommentListFragment commentListFragment;//评价列表
    private CommentQuestionFragment commentQuestionFragment;//问大家
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_manager);
        ButterKnife.bind(this);
        setSelect(0);
    }

    private void setSelect(int position) {
        FragmentManager manager = getSupportFragmentManager();
        commentaryFragment = (CommentaryFragment) manager.findFragmentByTag("commentaryFragment");
        commentListFragment = (CommentListFragment) manager.findFragmentByTag
                ("commentListFragment");
        commentQuestionFragment = (CommentQuestionFragment) manager.findFragmentByTag(
                "commentQuestionFragment");
        ft = manager.beginTransaction();
        hideFragment();
        switch (position) {
            case 0:
                if (commentaryFragment == null) {
                    ft.add(R.id.fl_container,
                            CommentaryFragment.newInstance(),
                            "commentaryFragment");
                } else {
                    ft.show(commentaryFragment);
                }
                setTitle(R.string.title_activity_comment_manager);
                hideDividerView();
                break;
            case 1:
                if (commentListFragment == null) {
                    ft.add(R.id.fl_container,
                            CommentListFragment.newInstance(),
                            "commentListFragment");
                } else {
                    ft.show(commentListFragment);
                }
                setTitle(R.string.title_activity_comment_manager);
                hideDividerView();
                break;
            case 2:
                if (commentQuestionFragment == null) {
                    ft.add(R.id.fl_container,
                            CommentQuestionFragment.newInstance(),
                            "commentQuestionFragment");
                } else {
                    ft.show(commentQuestionFragment);
                }
                setTitle(R.string.label_comment_question);
                showDividerView();
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideFragment() {
        if (ft != null) {
            if (commentaryFragment != null && !commentaryFragment.isHidden()) {
                ft.hide(commentaryFragment);
            }
            if (commentListFragment != null && !commentListFragment.isHidden()) {
                ft.hide(commentListFragment);
            }
            if (commentQuestionFragment != null && !commentQuestionFragment.isHidden()) {
                ft.hide(commentQuestionFragment);
            }
        }
    }

    @OnClick({R.id.cb_commentary, R.id.cb_comment_list, R.id.cb_comment_question})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_commentary:
                setSelect(0);
                break;
            case R.id.cb_comment_list:
                setSelect(1);
                break;
            case R.id.cb_comment_question:
                setSelect(2);
                break;
            default:
                break;
        }
    }
}
