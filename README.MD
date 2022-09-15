# NMS Remap Helper

### Goal
This plugin targets Spigot Plugins developers who use the NMS classes.
This plugin allows you to use the remapped-mojang spigot jar as a dependency and develop your plugins with this jar.
This plugin exports your plugin mapped correctly according to your Spigot version (and your spigot server of course).

### Usage
First, add the plugin to your project :
```gradle
plugins {
    id 'fr.flowarg.nmsremaphelper' version '1.0.1'
}
```
It's possible that the version written in the README is not up-to-date. So make sure you have installed the latest plugin version.
Check that on the [Gradle Plugins Page](https://plugins.gradle.org/plugin/fr.flowarg.nmsremaphelper).

Don't reload now your gradle project because it will crash.
We need to populate the `nmsremaphelper` extension before!

```gradle
nmsremaphelper {
    spigotVersion = "1.18.2-R0.1-SNAPSHOT"
}
```
Then, make sure the remapped jar is in the maven local repository.
- Go to your user directory
- Then, go to .m2, repository, org, spigotmc, spigot, spigot version. And make sure the file spigot-spigotversion-remapped-mojang.jar exists.
- If not, you have to execute the [BuildTools.jar](https://www.spigotmc.org/wiki/buildtools/) with the `--remapped` argument!

Once it's done, you have to add two repositories to your project :
```gradle
repositories {
    mavenCentral()
    mavenLocal()
}
```
They are very common, so I think you should already have them written in the `repositories` block :).

Then add the spigot remapped jar (which contains NMS classes!) to your dependencies :
```gradle
dependencies {
    implementation 'org.spigotmc:spigot:spigotVersion:remapped-mojang'
}
```

Then just call the `remapJar` gradle task and you'll find in your build/libs directory a filename-remap.jar file! This is the one you have to place it in the `plugins` folder of your spigot server instance.

And all it's done!