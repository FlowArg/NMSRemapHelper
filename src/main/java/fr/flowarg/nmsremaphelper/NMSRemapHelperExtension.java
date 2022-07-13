package fr.flowarg.nmsremaphelper;

import org.gradle.api.provider.Property;

/**
 * Represent the nmsremaphelper extension.
 */
public interface NMSRemapHelperExtension
{
    /**
     * Provide the target spigot version (e.g: 1.18.2-R0.1-SNAPSHOT).
     * MUST BE specified.
     * @return the target spigot version.
     */
    Property<String> getSpigotVersion();
}
