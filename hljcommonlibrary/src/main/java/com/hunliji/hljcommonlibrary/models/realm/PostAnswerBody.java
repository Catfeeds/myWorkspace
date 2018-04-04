package com.hunliji.hljcommonlibrary.models.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by Suncloud on 2016/8/31.
 */
@RealmClass
public class PostAnswerBody extends RealmObject {

    @SerializedName("answer_id")
    private Long answerId;
    @SerializedName("question_id")
    private Long questionId;
    private String content;
    private Long userId;

    public PostAnswerBody(){}

    public PostAnswerBody(long userId,long questionId,String content) {
        this.questionId = questionId;
        this.userId=userId;
        this.content = content;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
