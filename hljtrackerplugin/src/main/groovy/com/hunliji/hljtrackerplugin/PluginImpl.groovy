package com.hunliji.hljtrackerplugin

import com.android.build.gradle.BaseExtension
import com.hunliji.hljtrackerplugin.utils.Contents
import com.hunliji.hljtrackerplugin.utils.DataHelper
import com.hunliji.hljtrackerplugin.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginImpl implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println ":applied hljtrackergradleplugin"
        HljTrackerParams params = project.extensions.
                create(Contents.trackerConfig, HljTrackerParams)
        registerTransform(project)
        initDir(project);
        project.afterEvaluate {
            Log.setShowLog(params.showLog)
            if (params.watchTime) {
                project.gradle.addListener(new TimeListener())
            }
        }
    }


    def static registerTransform(Project project) {
        BaseExtension android = project.extensions.getByType(BaseExtension)
        InjectTransform transform = new InjectTransform(project)
        android.registerTransform(transform)
    }


    static void initDir(Project project) {
        File pluginTmpDir = new File(project.buildDir, 'hunliji')
        if (!pluginTmpDir.exists()) {
            pluginTmpDir.mkdir()
        }
        DataHelper.ext.pluginTmpDir = pluginTmpDir
    }

}