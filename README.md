# Grappling Hook (Fabric, MC 1.21.11)

Two separate craftable items:

- **Pull Hook** (iron + string) — right-click while aiming at a block within
  30 blocks to get reeled straight toward that point. Right-click again to
  let go early.
- **Swing Hook** (gold + string) — right-click while aiming at a block
  within 25 blocks to anchor a rope there and swing like a pendulum. Right-click
  again to release.

Neither item shoots a visible flying hook entity — the "cast" is an instant
raycast and the pull/swing physics kicks in immediately. This keeps the mod
simple (no custom entity + renderer needed). If you want an actual flying
hook with a rope render later, that's a solid next step — just ask.

## Building it

I can't reach the internet from my sandbox, so I couldn't run Gradle or
verify this compiles — you'll need to do that step locally where you have
internet access (Loom needs to download Minecraft + mappings + Fabric API
the first time you build).

1. Install a JDK 21 (Temurin/Adoptium works well).
2. Unzip this project.
3. In the project folder, run:
   ```
   gradle wrapper
   ```
   (if you don't have Gradle installed globally, grab it from
   https://gradle.org/install/ first — or just open the folder in IntelliJ
   IDEA with the Fabric/Gradle plugins, which will offer to set up the
   wrapper for you automatically).
4. Then run:
   ```
   ./gradlew build
   ```
   The compiled mod jar will show up in `build/libs/`.
5. Drop that jar into your `.minecraft/mods` folder alongside the matching
   **Fabric Loader** and **Fabric API** for 1.21.11.

### If the build fails on version numbers

Minecraft/Fabric version numbers move fast. Everything in
`gradle.properties` was accurate as of when I built this, but if Gradle
complains it can't find a dependency, check
https://fabricmc.net/develop/ (or the official template generator there)
for the current `loader_version`, `loom_version`, and `fabric_api_version`
that match Minecraft 1.21.11, and swap them in.

### If you get a Java compile error in `use(...)`

Minecraft's item-interaction API (`ActionResult` vs the older
`TypedActionResult`/`ItemActionResult`) has been reshuffled a couple of
times across 1.21.x point releases. I wrote this against the signature
that's been standard since 1.21.2:

```java
public ActionResult use(World world, PlayerEntity user, Hand hand)
```

If your exact 1.21.11 build renamed or changed that method, your IDE will
flag it immediately with a clear "does not override" error — it's a
one-line fix (Fabric's changelog posts at fabricmc.net cover exactly what
changed each version).

## Project layout

```
src/main/java/com/example/grapplinghook/
  GrapplingHookMod.java     - entrypoint, registers items + the tick loop
  registry/ModItems.java    - item registration
  item/PullHookItem.java    - "reel to point" item
  item/SwingHookItem.java   - "anchor + swing" item
  mechanic/PullManager.java - per-tick pull physics
  mechanic/SwingManager.java- per-tick pendulum/swing physics
src/main/resources/
  fabric.mod.json
  assets/.../lang, models, textures (placeholder icons - swap the PNGs for your own art)
  data/.../recipe (crafting recipes)
```
