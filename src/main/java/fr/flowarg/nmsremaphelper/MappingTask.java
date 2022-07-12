package fr.flowarg.nmsremaphelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

public abstract class MappingTask extends DefaultTask
{
    @TaskAction
    public void execute() {}

    protected String toAbsolute(@NotNull RegularFileProperty file)
    {
        return file.get().getAsFile().getAbsolutePath();
    }

    @InputFile
    public abstract RegularFileProperty getInputFile();

    @InputFile
    public abstract RegularFileProperty getMappingFile();

    @Input
    public abstract Property<Boolean> getShouldReverse();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();
}
