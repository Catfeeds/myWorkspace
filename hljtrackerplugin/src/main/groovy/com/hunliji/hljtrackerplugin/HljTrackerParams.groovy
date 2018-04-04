package com.hunliji.hljtrackerplugin

import groovy.transform.CompileStatic

@CompileStatic
public class HljTrackerParams{

    boolean showLog
    boolean watchTime
    List<String> modifyClasses = []
    Map<String, List<Map<String,Object>>> modifyInterfaceMethods = [:]


}