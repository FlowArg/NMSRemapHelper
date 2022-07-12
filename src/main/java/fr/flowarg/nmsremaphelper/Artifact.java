package fr.flowarg.nmsremaphelper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Artifact(String group, String name, String version, String classifier)
{
    public static final String CENTRAL = "https://repo1.maven.org/maven2/%s/%s/%s/%s";

    @Contract("_, _ -> new")
    public String @NotNull [] genURL(String repo, String ext)
    {
        final String fileName = name + "-" + version + (classifier.isEmpty() ? "" : "-" + classifier) + "." + ext;
        return new String[]{String.format(repo, group.replace('.', '/'), name, version, fileName), fileName};
    }
}
