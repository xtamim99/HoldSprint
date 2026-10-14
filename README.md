# Hold Sprint (Minecraft Java 26.2 / Fabric)

A tiny client-side Fabric mod that makes sprinting follow the sprint key:
- hold the sprint key to sprint;
- release it to stop sprinting;
- opening a menu/chat stops sprinting.

This version uses a Fabric client-tick callback and does not use Mixins.

## Build

Requires JDK 25 and Gradle 9.5.1 or newer.

From this folder, run:

```sh
gradle build
```

The mod JAR will be created in `build/libs/hold-sprint-1.0.0.jar`.

## Install

Copy the built JAR into your Minecraft instance's `mods` folder. Use Minecraft 26.2, Fabric Loader 0.19.3 or newer, and Fabric API for 26.2.

## Notes

This project source is prepared for the Minecraft 26.2 unobfuscated Fabric toolchain. It has not been compiled or launched in this environment, so if your setup reports an API-name mismatch, share the build error and I can adjust it.
