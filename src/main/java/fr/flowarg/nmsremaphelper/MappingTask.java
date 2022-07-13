package fr.flowarg.nmsremaphelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

/**
 * Basic mapping parent task class.
 */
public abstract class MappingTask extends DefaultTask
{
    /**
     * Do nothing
     */
    @TaskAction
    public void execute() {}

    /**
     * Convert a {@link RegularFileProperty} to an absolute path.
     * @param file the file to convert.
     * @return the absolute path.
     */
    public String toAbsolute(@NotNull RegularFileProperty file)
    {
        return file.get().getAsFile().getAbsolutePath();
    }

    /**
     * The input file to map.
     * @return the input file to map.
     */
    @InputFile
    public abstract RegularFileProperty getInputFile();

    /**
     * The mappings file to use.
     * @return the mapping file to use.
     */
    @InputFile
    public abstract RegularFileProperty getMappingFile();

    /**
     * Should reverse the mappings?
     * @return if it should reverse the mappings.
     */
    @Input
    public abstract Property<Boolean> getShouldReverse();

    /**
     * The mapped output file.
     * @return the mapped output file.
     */
    @OutputFile
    public abstract RegularFileProperty getOutputFile();
}
