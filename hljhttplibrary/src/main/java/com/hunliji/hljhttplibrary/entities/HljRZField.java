package com.hunliji.hljhttplibrary.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by werther on 16/11/7.
 * HljResultZip的成员注解，用于反射处理的时候识别自定义成员属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface HljRZField {}
