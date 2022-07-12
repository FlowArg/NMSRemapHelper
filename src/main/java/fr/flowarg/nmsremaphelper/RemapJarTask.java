package fr.flowarg.nmsremaphelper;

import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.jvm.Jvm;

import java.util.List;

public abstract class RemapJarTask extends MappingTask
{
    @TaskAction
    public void execute()
    {
        try
        {
            final var args = List.of("-i", this.toAbsolute(this.getInputFile()), "-o", this.toAbsolute(this.getOutputFile()), "-m", this.toAbsolute(this.getMappingFile()));

            final ProcessBuilder pb = new ProcessBuilder();
            pb.command(Jvm.current().getJavaExecutable().getAbsolutePath(), "-jar", NMSRemapHelperPlugin.ARTIFACT_FILES.get(NMSRemapHelperPlugin.SPECIAL_SOURCE).toAbsolutePath().toString());
            pb.command().addAll(args);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            final Process process = pb.start();
            process.waitFor();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
