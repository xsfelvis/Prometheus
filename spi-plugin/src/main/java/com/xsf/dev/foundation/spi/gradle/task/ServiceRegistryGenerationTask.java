package com.xsf.dev.foundation.spi.gradle.task;


import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Generate {@code ServiceRegistry} class by scanning all classes from classpath
 */
public class ServiceRegistryGenerationTask extends DefaultTask {

    private File sourceDir;

    private File servicesDir;

    private FileCollection classpath;

    public ServiceRegistryGenerationTask() {
        this.classpath = this.getProject().files();
    }

    @InputFiles
    public FileCollection getClasspath() {
        return this.classpath;
    }

    public void setClasspath(final FileCollection classpath) {
        this.classpath = classpath;
    }

    @OutputDirectory
    public File getSourceDir() {
        return this.sourceDir;
    }

    public void setSourceDir(final File sourceDir) {
        this.sourceDir = sourceDir;
    }

    @OutputDirectory
    public File getServicesDir() {
        return this.servicesDir;
    }

    public void setServicesDir(final File servicesDir) {
        this.servicesDir = servicesDir;
    }

    @TaskAction // 加上这个action的作用是当执行这个task的时候会自动执行这个方法
    protected void generate() {
        this.setDidWork(new ServiceRegistryGenerationAction(this.classpath, this.servicesDir, this.sourceDir).execute());
    }
}
