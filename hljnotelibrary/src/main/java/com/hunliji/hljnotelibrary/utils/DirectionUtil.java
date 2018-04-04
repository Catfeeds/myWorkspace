package com.hunliji.hljnotelibrary.utils;

import com.hunliji.hljnotelibrary.models.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_bin on 2017/7/14 0014.
 */
public class DirectionUtil {
    private static DirectionUtil mInstance;
    private List<Direction[][]> mDirections = new ArrayList<>();

    public static DirectionUtil getInstance() {
        if (mInstance == null) {
            mInstance = new DirectionUtil();
        }
        return mInstance;
    }
    // 每种标签数都对应四种变换模式
    private DirectionUtil() {
        mDirections.clear();
        Direction[][] oneTagMode = new Direction[][]{
                {Direction.LEFT_CENTER},
                {Direction.RIGHT_CENTER},
                {Direction.LEFT_BOTTOM_TILT},
                {Direction.RIGHT_BOTTOM_TILT}
        };
        Direction[][] twoTagMode = new Direction[][]{
                {Direction.LEFT_TOP, Direction.LEFT_BOTTOM},
                {Direction.RIGHT_TOP, Direction.RIGHT_BOTTOM},
                {Direction.LEFT_TOP_TILT, Direction.LEFT_BOTTOM_TILT},
                {Direction.RIGHT_TOP_TILT, Direction.RIGHT_BOTTOM_TILT}
        };
        Direction[][] threeTagMode = new Direction[][]{
                {Direction.LEFT_TOP, Direction.LEFT_CENTER, Direction.LEFT_BOTTOM},
                {Direction.RIGHT_TOP, Direction.RIGHT_CENTER, Direction.RIGHT_BOTTOM},
                {Direction.LEFT_TOP_TILT, Direction.RIGHT_BOTTOM_TILT, Direction.LEFT_BOTTOM_TILT},
                {Direction.RIGHT_TOP_TILT, Direction.LEFT_BOTTOM_TILT, Direction.RIGHT_BOTTOM_TILT}
        };
        mDirections.add(oneTagMode);
        mDirections.add(twoTagMode);
        mDirections.add(threeTagMode);
    }

    public List<Direction[][]> getDirections() {
        return mDirections;
    }

}
