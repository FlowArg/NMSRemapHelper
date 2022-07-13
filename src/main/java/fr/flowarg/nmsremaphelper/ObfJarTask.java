package fr.flowarg.nmsremaphelper;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.jvm.Jvm;

import java.io.File;
import java.util.List;

/**
 * obfJar task entry.
 */
public abstract class ObfJarTask extends MappingTask
{
    /**
     * Execute SpecialSource to obfuscate the freshly built plugin like the basic Minecraft jar.
     */
    @TaskAction
    public void execute()
    {
        try
        {
            final var args = List.of("-i", this.toAbsolute(this.getInputFile()), "-o", this.toAbsolute(this.getOutputFile()), "-m", this.toAbsolute(this.getMappingFile()), "--live", "--reverse");

            final ProcessBuilder pb = new ProcessBuilder();
            pb.command(Jvm.current().getJavaExecutable().getAbsolutePath(), "-cp", NMSRemapHelperPlugin.ARTIFACT_FILES.get(NMSRemapHelperPlugin.SPECIAL_SOURCE).toAbsolutePath() + File.pathSeparator + this.getMappedMojangJar().get().getAsFile().getAbsolutePath(), "net.md_5.specialsource.SpecialSource");
            pb.command().addAll(args);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            final Process process = pb.start();
            process.waitFor();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Represent the spigot mapped mojang jar BUILT WITH THE --remapped BUILD TOOLS ARGUMENT.
     * @return the spigot mapped mojang jar file.
     */
    @InputFile
    public abstract RegularFileProperty getMappedMojangJar();
}
