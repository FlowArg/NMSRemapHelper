package fr.flowarg.nmsremaphelper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represent an artifact (to be downloaded and used in the future)
 * @param group the groupdId of the artifact (e.g: fr.flowarg)
 * @param name the artifactId of the artifact (e.g: nmsremaphelper)
 * @param version the version of the artifact (e.g: 1.0.0)
 * @param classifier the classifier of the artifact (e.g: sources). Let this field empty if there is no classifier.
 */
public record Artifact(String group, String name, String version, String classifier)
{
    /**
     * This field is used to build the url of the artifact to download from Maven Central repository.
     */
    public static final String CENTRAL = "https://repo1.maven.org/maven2/%s/%s/%s/%s";

    /**
     * This method generates and return an url (and the corresponding file name) from the provided repository.
     * @param repo repo to use (e.g: "{@link #CENTRAL}")
     * @param ext the extension of the file (e.g: "jar" or "jar.sha1")
     * @return the built url and filename of the artifact.
     */
    @Contract("_, _ -> new")
    public String @NotNull [] genURL(String repo, String ext)
    {
        final String fileName = name + "-" + version + (classifier.isEmpty() ? "" : "-" + classifier) + "." + ext;
        return new String[]{String.format(repo, group.replace('.', '/'), name, version, fileName), fileName};
    }
}
