package com.hunliji.hljtrackerplugin.utils

import groovy.transform.CompileStatic

@CompileStatic
public interface Contents {

    String trackerConfig="trackerConfig"
    // 原方法名
    String methodName="method"
    // 原方法参数描述 “(参数列表)返回值类型”
    String methodParams="paramsDesc"
    // 替换的方法所在类
    String modifyClassName="modifyClass"
    // 替换的方法名
    String modifyMethodName="modifyMethod"
    // 替换的方法参数描述
    String modifyMethodParams="modifyParamsDesc"
    // 参数类型对应的ASM指令和位置，加载不同类型的参数需要不同的指令
    String modifyMethodParamOpcodes="modifyParamOpcodes"


    String opcode="opcode"
    String opcodeIndex="opcodeIndex"
}