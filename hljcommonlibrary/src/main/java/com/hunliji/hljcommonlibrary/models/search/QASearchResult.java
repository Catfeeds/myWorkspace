package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;

import java.util.List;

/**
 * Created by werther on 16/12/7.
 */
public class QASearchResult extends BaseSearchResult {
    @SerializedName("list")
    List<Answer> answerList;

    public List<Answer> getQuestionList() {
        return answerList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || answerList == null || answerList.isEmpty();
    }
}
