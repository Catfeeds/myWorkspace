package com.hunliji.hljtrackerplugin.utils

import groovy.transform.CompileStatic
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by bryansharp(bsp0911932@163.com) on 2016/5/10.
 * Modified by nailperry on 2017/3/2.
 *
 */
@CompileStatic
public class ModifyClassUtil {

    public
    static byte[] modifyClasses(
            String className,
            byte[] srcByteCode,
            Map<String, List<Map<String, Object>>> interfaceMethods) {
        byte[] classBytesCode = null;
        try {
            Log.info("====start modifying ${className}====");
            classBytesCode = modifyClass(srcByteCode, interfaceMethods);
            Log.info("====revisit modified ${className}====");
            onlyVisitClassMethod(classBytesCode);
            Log.info("====finish modifying ${className}====");
            return classBytesCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (classBytesCode == null) {
            Log.info("====srcByteCode ${className}====");
            classBytesCode = srcByteCode;
        }
        return classBytesCode;
    }


    private
    static byte[] modifyClass(
            byte[] srcClass,
            Map<String, List<Map<String, Object>>> interfaceMethods)
            throws IOException {
        ClassReader classReader = new ClassReader(srcClass)
        ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
        MethodFilterClassVisitor methodFilterCV = new MethodFilterClassVisitor(classWriter)
        methodFilterCV.interfaceMethods = interfaceMethods
        classReader.accept(methodFilterCV, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    private
    static void onlyVisitClassMethod(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodFilterClassVisitor methodFilterCV = new MethodFilterClassVisitor(classWriter);
        methodFilterCV.onlyVisit = true;
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.EXPAND_FRAMES);
    }
    /**
     *
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     * {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param start 方法参数起始索引（ 0：this，1+：普通参数 ）
     *
     * @param count 方法参数个数
     *
     * @param paramOpcodes 参数类型对应的ASM指令
     *
     */
    private
    static void visitMethodWithLoadedParams(
            MethodVisitor methodVisitor,
            int opcode,
            String owner,
            String methodName,
            String methodDesc,
            List<Map<String, Integer>> paramOpcodes) {
        if (paramOpcodes != null && paramOpcodes.size() > 0) {
            paramOpcodes.each {
                Map<String, Integer> paramOpcode
                    ->
                    methodVisitor.visitVarInsn(
                            paramOpcode.get(Contents.opcode),
                            paramOpcode.get(Contents.opcodeIndex))
            }
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false);
    }

    static class MethodFilterClassVisitor extends ClassVisitor {
        public boolean onlyVisit = false;
        private String superName
        private String[] interfaces
        private ClassVisitor classVisitor
        private Map<String, List<Map<String, Object>>> interfaceMethods

        public MethodFilterClassVisitor(
                final ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
            this.classVisitor = cv
        }

        @Override
        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            this.superName = superName
            this.interfaces = interfaces
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name,
                                         String desc, String signature, String[] exceptions) {
            MethodVisitor myMv = null;
            if (interfaces != null && interfaces.length > 0 && interfaceMethods != null &&
                    !interfaceMethods.isEmpty()) {
                interfaceMethods.keySet().each {
                    String interfaceName ->
                        if (interfaces.contains(interfaceName)) {
                            List<Map<String, Object>> modifyMethods = interfaceMethods.
                                    get(interfaceName);
                            modifyMethods.each { Map<String, Object> modifyMethod ->
                                if (name == modifyMethod.get(Contents.methodName) && desc ==
                                        modifyMethod.get(Contents.methodParams)) {
                                    MethodVisitor methodVisitor = cv.
                                            visitMethod(access, name, desc, signature, exceptions);
                                    try {
                                        myMv = new ModifyAdvice(modifyMethod, methodVisitor, access, name, desc)
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        myMv = null
                                    }

                                }
                            }

                        }
                }
            }
            if (myMv != null) {
                return myMv
            } else {
                return cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    static class ModifyAdvice extends AdviceAdapter {
        Map<String, Object> modifyMethod

        protected ModifyAdvice(Map<String, Object> modifyMethod, MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
            this.modifyMethod = modifyMethod;
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter()
            String className = modifyMethod.get(Contents.modifyClassName)
            String methodName = modifyMethod.get(Contents.modifyMethodName)
            String methodParams = modifyMethod.get(Contents.modifyMethodParams)
            visitMethodWithLoadedParams(
                    mv,
                    INVOKESTATIC,
                    className,
                    methodName,
                    methodParams,
                    ((List<Map<String, Integer>>) modifyMethod.
                            get(Contents.modifyMethodParamOpcodes)))
        }
    }
}