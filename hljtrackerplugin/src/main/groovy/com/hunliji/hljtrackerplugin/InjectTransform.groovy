package com.hunliji.hljtrackerplugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.hunliji.hljtrackerplugin.utils.DataHelper
import com.hunliji.hljtrackerplugin.utils.Log
import com.hunliji.hljtrackerplugin.utils.ModifyClassUtil
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.security.MessageDigest
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class InjectTransform extends Transform {
    private static Project project;

    public InjectTransform(Project project) {
        InjectTransform.project = project
    }


    static HljTrackerParams getTrackerParams() {
        return project.trackerConfig
    }

    @Override
    String getName() {
        return "HljTracker"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        Log.info "==============transform enter=============="
        Collection<TransformInput> inputs = transformInvocation.getInputs();

        inputs.each {
            TransformInput input
                ->
                input.jarInputs.each { JarInput jarInput ->
                    String destName = jarInput.file.name
                    /** 重名名输出文件,因为可能同名,会覆盖*/
                    def hexName = md5(jarInput.file.absolutePath).substring(0, 8);
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4);
                    }
                    /** 获得输出文件*/
                    File dest = transformInvocation.getOutputProvider().getContentLocation(
                            destName + "_" + hexName,
                            jarInput.contentTypes,
                            jarInput.scopes,
                            Format.JAR)

                    Log.info("jarName:" + jarInput.file.absolutePath + ",dest:" + dest.absolutePath);
                    def modifiedJar = modifyJarFile(jarInput.file, transformInvocation.getContext().getTemporaryDir());
                    if (modifiedJar == null) {
                        modifiedJar = jarInput.file
                    } else {
                        saveModifiedFileForCheck(modifiedJar);
                    }
                    FileUtils.copyFile(modifiedJar, dest)


                }
                /**
                 * 遍历目录
                 */
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    File dest = transformInvocation.getOutputProvider().getContentLocation(
                            directoryInput.name,
                            directoryInput.contentTypes,
                            directoryInput.scopes,
                            Format.DIRECTORY);
                    File dir = directoryInput.file
                    if (dir) {
                        HashMap<String, File> modifyMap = new HashMap<>();
                        dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                            File classFile ->
                                File modified =
                                        modifyClassFile(dir, classFile, transformInvocation.getContext().getTemporaryDir());
                                if (modified != null) {
                                    //key为相对路径
                                    modifyMap.put(
                                            classFile.absolutePath.replace(dir.absolutePath, ""),
                                            modified);
                                }
                        }
                        FileUtils.copyDirectory(directoryInput.file, dest);
                        modifyMap.entrySet().each {
                            Map.Entry<String, File> en ->
                                File target = new File(dest.absolutePath + en.getKey());
                                Log.info(target.getAbsolutePath());
                                if (target.exists()) {
                                    target.delete();
                                }
                                FileUtils.copyFile(en.getValue(), target);
                                saveModifiedFileForCheck(en.getValue());
                                en.getValue().delete();
                        }
                    }
                }
        }
    }

    /**
     * 该jar文件是否包含需要修改的类
     * @param jarFile
     * @return
     */
    public static boolean isJarNeedModify(File jarFile) {
        boolean modified = false;
        if (jarFile) {
            /**
             * 读取原jar
             */
            def file = new JarFile(jarFile);
            Enumeration enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                String className
                if (entryName.endsWith(".class")) {
                    className = entryName.replace("/", ".").replace(".class", "")
                    if (shouldModifyClass(className)) {
                        modified = true;
                        break;
                    }
                }
            }
            file.close();
        }
        return modified;
    }


    static boolean shouldModifyClass(String className) {
        boolean modified = false
        if (trackerParams == null || trackerParams.modifyClasses == null) {
            return modified
        } else if ((!className.contains("R2\$") && !className.endsWith("R2") &&
                !className.contains("R\$") && !className.endsWith("R") &&
                !className.endsWith("BuildConfig"))) {
            trackerParams.modifyClasses.each { String modifyClassName ->
                if (className.contains(modifyClassName)) {
                    Log.info("===========Modify" + className)
                    modified = true
                    return modified
                }
            }
        }
        return modified
    }

    static String md5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static saveModifiedFileForCheck(File outFile) {
        File dir = DataHelper.ext.pluginTmpDir;
        File checkJarFile = new File(dir, outFile.getName());
        if (checkJarFile.exists()) {
            checkJarFile.delete();
        }
        FileUtils.copyFile(outFile, checkJarFile);
    }

    static String path2Classname(String entryName) {
        entryName.replace(File.separator, ".").replace(".class", "")
    }

    /**
     * 植入代码
     * @param buildDir 是项目的build class目录,就是我们需要注入的class所在地
     * @param lib 这个是hackdex的目录,就是AntilazyLoad类的class文件所在地
     */
    static File modifyJarFile(File jarFile, File tempDir) {
        if (jarFile && isJarNeedModify(jarFile)) {
            /** 设置输出到的jar */
            def hexName = md5(jarFile.absolutePath).substring(0, 8)
            def optJar = new File(tempDir, hexName + jarFile.name)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));
            /**
             * 读取原jar
             */
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                InputStream inputStream = file.getInputStream(jarEntry)

                String entryName = jarEntry.getName()
                String className

                ZipEntry zipEntry = new ZipEntry(entryName)

                jarOutputStream.putNextEntry(zipEntry)

                byte[] modifiedClassBytes = null
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
                if (entryName.endsWith(".class")) {
                    className = entryName.replace("/", ".").replace(".class", "")
                    if (shouldModifyClass(className)) {
                        modifiedClassBytes = ModifyClassUtil.
                                modifyClasses(
                                        className,
                                        sourceClassBytes,
                                        trackerParams.modifyInterfaceMethods)
                    }
                }
                if (modifiedClassBytes == null) {
                    jarOutputStream.write(sourceClassBytes)
                } else {
                    jarOutputStream.write(modifiedClassBytes)
                }
                jarOutputStream.closeEntry()
            }
            Log.info "${hexName} is modified"
            jarOutputStream.close()
            file.close()
            return optJar
        }
        return null
    }


    static File modifyClassFile(File dir, File classFile, File tempDir) {
        try {
            File modified;
            String className = path2Classname(
                    classFile.absolutePath.replace(dir.absolutePath + File.separator, ""));
            if (shouldModifyClass(className)) {
                FileInputStream inputStream=new FileInputStream(classFile);
                byte[] modifiedClassBytes = ModifyClassUtil.
                        modifyClasses(
                                className,
                                IOUtils.toByteArray(inputStream),
                                trackerParams.modifyInterfaceMethods)

                inputStream.close()
                if (modifiedClassBytes) {
                    modified = new File(tempDir, className.replace('.', '') + '.class')
                    if (modified.exists()) {
                        modified.delete();
                    }
                    modified.createNewFile()
                    FileOutputStream outputStream=new FileOutputStream(modified)
                    outputStream.write(modifiedClassBytes)
                    outputStream.close()
                    return modified;
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return null;

    }
}