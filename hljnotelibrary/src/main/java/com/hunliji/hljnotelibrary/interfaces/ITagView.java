package com.hunliji.hljnotelibrary.interfaces;

import com.hunliji.hljnotelibrary.models.Direction;

public interface ITagView {

    void setMaxWidth(int maxWidth);

    void setDirection(Direction direction);

    Direction getDirection();

    int getMeasuredWidth();

    int getMeasuredHeight();

    int getTop();

    int getLeft();

    int getRight();

    int getBottom();

    void layout(int left, int top, int right, int bottom);

}
