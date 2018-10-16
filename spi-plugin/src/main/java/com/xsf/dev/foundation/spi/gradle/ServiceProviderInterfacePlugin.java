package com.xsf.dev.foundation.spi.gradle;

import com.android.build.gradle.AppExtension;
import com.xsf.dev.foundation.spi.gradle.task.ServiceRegistryGenerationTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;

import static android.databinding.tool.util.StringUtils.capitalize;

/**
 * The gradle plugin for SPI generation
 *
 * @author xsf
 */
public class ServiceProviderInterfacePlugin implements Plugin<Project> {

    @Override
    @SuppressWarnings("deprecation")
    public void apply(final Project project) {
        project.getDependencies().add("implementation", "com.xsfdev:spi-loader:" + project.getRootProject().getVersion());
        project.afterEvaluate(p -> {
            if (!project.getPlugins().hasPlugin("com.android.application")) {
                return;
            }

            final AppExtension android = project.getExtensions().getByType(AppExtension.class);

            android.getApplicationVariants().forEach(variant -> {
                final File spiRoot = project.file(project.getBuildDir() + File.separator + "intermediates" + File.separator + "spi" + File.separator + variant.getDirName() + File.separator);
                final FileCollection spiClasspath = project.files(android.getBootClasspath(), variant.getJavaCompile().getClasspath(), variant.getJavaCompile().getDestinationDir());

                final ServiceRegistryGenerationTask generateTask = project.getTasks().create("generateServiceRegistry" + capitalize(variant.getName()), ServiceRegistryGenerationTask.class, task -> {
                    task.setDescription("Generate ServiceRegistry for " + capitalize(variant.getName()));
                    task.setClasspath(task.getClasspath().plus(spiClasspath));
                    task.setSourceDir(new File(spiRoot, "src"));
                    task.setServicesDir(new File(spiRoot, "services"));
                    task.getOutputs().upToDateWhen(it -> false);
                });

                final JavaCompile compileGeneratedTask = project.getTasks().create("compileGenerated" + capitalize(variant.getName()), JavaCompile.class, task -> {
                    task.setDescription("Compile ServiceRegistry for " + capitalize(variant.getName()));
                    task.setSource(new File(spiRoot, "src"));
                    task.include("**/*.java");
                    task.setClasspath(spiClasspath);
                    task.setDestinationDir(variant.getJavaCompile().getDestinationDir());
                    task.setSourceCompatibility("1.7");
                    task.setTargetCompatibility("1.7");
                });

                generateTask.mustRunAfter(variant.getJavaCompile());
                compileGeneratedTask.mustRunAfter(generateTask);
                variant.getAssemble().dependsOn(generateTask, compileGeneratedTask);
            });
        });
    }

}
