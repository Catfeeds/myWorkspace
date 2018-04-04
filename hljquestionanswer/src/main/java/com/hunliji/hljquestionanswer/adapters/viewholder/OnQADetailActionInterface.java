package com.hunliji.hljquestionanswer.adapters.viewholder;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;

/**
 * Created by hua_rong on 2017/9/28
 */

public interface OnQADetailActionInterface {

    void onFollowQuestion(Question question, int position);


    void onPraiseClickListener(CheckableLinearLayout btnPraise, TextView upCount, Answer answer);

    void onMoreOption(@Nullable Answer answer, int position);

}
