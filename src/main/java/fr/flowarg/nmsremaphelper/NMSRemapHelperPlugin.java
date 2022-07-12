package fr.flowarg.nmsremaphelper;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NMSRemapHelperPlugin implements Plugin<Project>
{
    public static final Artifact SPECIAL_SOURCE = new Artifact("net.md-5", "SpecialSource", "1.11.0", "shaded");
    public static final Map<Artifact, Path> ARTIFACT_FILES = new ConcurrentHashMap<>();

    @Override
    public void apply(@NotNull Project project)
    {
        project.getPlugins().apply(JavaLibraryPlugin.class);

        final TaskProvider<Jar> jarTask = project.getTasks().named(JavaPlugin.JAR_TASK_NAME, Jar.class);

        final var extension = project.getExtensions().create("nmsremaphelper", NMSRemapHelperExtension.class);
        final var downloadDepsTask = project.getTasks().register("downloadDeps", DownloadDepsTask.class);
        final var obfJarTask = project.getTasks().register("obfJar", ObfJarTask.class);
        final var remapJarTask = project.getTasks().register("remapJar", RemapJarTask.class);
        final String taskGroup = "nmsremaphelper";

        downloadDepsTask.configure(task -> {
            task.getArtifacts().set(List.of(SPECIAL_SOURCE));
            task.setGroup(taskGroup);
        });

        obfJarTask.configure(task -> {
            task.dependsOn(jarTask);
            task.dependsOn(downloadDepsTask);
            final var inputFile = jarTask.flatMap(AbstractArchiveTask::getArchiveFile).get();
            task.getInputFile().convention(inputFile);
            task.getMappingFile().set(
                    Path.of(System.getProperty("user.home"))
                            .resolve(".m2")
                            .resolve("repository")
                            .resolve("org")
                            .resolve("spigotmc")
                            .resolve("minecraft-server")
                            .resolve(extension.getSpigotVersion().get())
                            .resolve("minecraft-server-" + extension.getSpigotVersion().get() + "-maps-mojang.txt")
                            .toFile());
            task.getOutputFile().set(new File(inputFile.getAsFile().getParentFile(), inputFile.getAsFile().getName().replace(".jar", "") + "-obf.jar"));
            task.getShouldReverse().set(true);
            task.setGroup(taskGroup);
            task.getMappedMojangJar().set(
                    Path.of(System.getProperty("user.home"))
                            .resolve(".m2")
                            .resolve("repository")
                            .resolve("org")
                            .resolve("spigotmc")
                            .resolve("spigot")
                            .resolve(extension.getSpigotVersion().get())
                            .resolve("spigot-" + extension.getSpigotVersion().get() + "-remapped-mojang.jar")
                            .toFile());
        });

        remapJarTask.configure(task -> {
            task.dependsOn(obfJarTask);
            final var inputFile = obfJarTask.flatMap(ObfJarTask::getOutputFile).get();
            task.getInputFile().convention(inputFile);
            task.getMappingFile().set(
                    Path.of(System.getProperty("user.home"))
                            .resolve(".m2")
                            .resolve("repository")
                            .resolve("org")
                            .resolve("spigotmc")
                            .resolve("minecraft-server")
                            .resolve(extension.getSpigotVersion().get())
                            .resolve("minecraft-server-" + extension.getSpigotVersion().get() + "-maps-spigot.csrg")
                            .toFile());

            final var outputFile = jarTask.flatMap(AbstractArchiveTask::getArchiveFile).get();
            task.getOutputFile().set(new File(outputFile.getAsFile().getParentFile(), outputFile.getAsFile().getName().replace(".jar", "") + "-remap.jar"));
            task.getShouldReverse().set(false);
            task.setGroup(taskGroup);
        });
    }
}
