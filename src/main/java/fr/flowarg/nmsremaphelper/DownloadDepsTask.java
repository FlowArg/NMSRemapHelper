package fr.flowarg.nmsremaphelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

/**
 * downloadDeps task entry.
 */
public abstract class DownloadDepsTask extends DefaultTask
{
    private final Property<Directory> downloadDirectory;

    /**
     * Constructor. Create {@link #downloadDirectory} property and directory.
     */
    public DownloadDepsTask()
    {
        try
        {
            final var dir = this.getProject().getLayout().getBuildDirectory().dir("nmsremaphelper");
            Files.createDirectories(dir.get().getAsFile().toPath());
            this.downloadDirectory = this.getProject().getObjects().directoryProperty().convention(dir);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Download the specified artifacts to the download directory.
     */
    @TaskAction
    public void execute()
    {
        this.getArtifacts().get().forEach(artifact -> {
            try
            {
                final var data = artifact.genURL(Artifact.CENTRAL, "jar");
                final var url = data[0];
                final var sha1 = this.getContent(new URL(artifact.genURL(Artifact.CENTRAL, "jar.sha1")[0]).openStream());
                final var fileName = data[1];
                final Path file = this.getDownloadDirectory().get().file(fileName).getAsFile().toPath();

                if(Files.exists(file))
                {
                    if(sha1.equals(this.getSHA1(Files.newInputStream(file))))
                    {
                        NMSRemapHelperPlugin.ARTIFACT_FILES.putIfAbsent(artifact, file);
                        return;
                    }
                    NMSRemapHelperPlugin.ARTIFACT_FILES.remove(artifact);
                    Files.deleteIfExists(file);
                }

                System.out.print("Downloading " + fileName + " from " + url + "...");
                Files.copy(new URL(url).openStream(), file);

                NMSRemapHelperPlugin.ARTIFACT_FILES.putIfAbsent(artifact, file);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    private @NotNull String getSHA1(@NotNull InputStream input) throws Exception
    {
        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        final byte[] data = new byte[8192];

        int read;
        while((read = input.read(data)) != -1)
            digest.update(data, 0, read);

        final byte[] bytes = digest.digest();
        final StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes)
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }

    private @NotNull String getContent(InputStream remote)
    {
        final StringBuilder sb = new StringBuilder();

        try(InputStream stream = new BufferedInputStream(remote))
        {
            final ReadableByteChannel rbc = Channels.newChannel(stream);
            final Reader enclosedReader = Channels.newReader(rbc, StandardCharsets.UTF_8.newDecoder(), -1);
            final BufferedReader reader = new BufferedReader(enclosedReader);

            int character;
            while ((character = reader.read()) != -1) sb.append((char)character);

            reader.close();
            enclosedReader.close();
            rbc.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Get the artifacts to process.
     * @return the artifacts to process.
     */
    @Input
    public abstract ListProperty<Artifact> getArtifacts();

    /**
     * Get the download directory where the artifacts will be downloaded.
     * @return the download directory.
     */
    @InputDirectory
    public Property<Directory> getDownloadDirectory()
    {
        return this.downloadDirectory;
    }
}
