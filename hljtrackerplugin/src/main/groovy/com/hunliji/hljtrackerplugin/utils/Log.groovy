package com.hunliji.hljtrackerplugin.utils

import groovy.transform.CompileStatic

import java.lang.reflect.Array

@CompileStatic
public class Log {

    static void setShowLog(boolean show) {
        Log.@show = show
    }

    def static boolean show = false


    def static info(Object msg) {
        if (!show) return
        try {
            println "${msg}"
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def static logEach(Object... msg) {
        if (!show) return
        msg.each {
            Object m ->
                try {
                    if (m != null) {
                        if (m.class.isArray()) {
                            print "["
                            def length = Array.getLength(m);
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    def get = Array.get(m, i);
                                    if (get != null) {
                                        print "${get}\t"
                                    } else {
                                        print "null\t"
                                    }
                                }
                            }
                            print "]\t"
                        } else {
                            print "${m}\t"
                        }
                    } else {
                        print "null\t"
                    }
                } catch (Exception e) {
                }
        }
        println ""

    }
}