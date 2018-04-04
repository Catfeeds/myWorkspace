package com.hunliji.hljhttplibrary.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangtao on 2018/3/21.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PosterSite {

    String name();

    boolean emptyVerify() default true;

    int index() default -1;
}
