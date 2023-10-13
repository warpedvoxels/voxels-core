<h1>
    <img alt="" width="38" height="38" src="./assets/logo@10x.png" />
    <code>voxels-core</code>
</h1>

## What is voxels?

**voxels-core** is a comprehensive collection of utilities and abstractions to 
streamline Minecraft server-side modding by simplifying the overall development process 
and making it more enjoyable and less error-prone. This project is part of the larger
[WarpedVoxels] initiative, but can be used independently.

Minecraft server-side modding in Kotlin is incredibly powerful, although existing 
tools and APIs often lack the flexibility and seamless integration needed to fully
leverage the power of the language. This project aims to fill the gap between the two,
providing a solid foundation for any Minecraft project that uses the base game core 
features to build upon.

The primary focus of this project is on **modularity** and tight integration with Kotlin's
modern ecosystem, including, but not limited to, [`kotlinx.coroutines`], [`kotlinx.serialization`],
and [Jetpack Compose].

[WarpedVoxels]: https://github.com/warpedvoxels/java-edition
[`kotlinx.coroutines`]: https://github.com/Kotlin/kotlinx.coroutines
[`kotlinx.serialization`]: https://github.com/Kotlin/kotlinx.serialization
[Jetpack Compose]: https://developer.android.com/jetpack/compose

## Who is using voxels?

**WarpedVoxels** is an open-source free-to-play network of Minecraft servers licensed 
under the [GNU Affero General Public License version 3] license. It strives to make your
Minecraft experience as fun, unique, and enjoyable as possible by using the limited 
resources the base game has to offer.

<a href="https://github.com/warpedvoxels/java-edition">
    <img width="356" height="58" src="./assets/header@10x.png" alt="[WarpedVoxels Source Code]" />
</a>

[GNU Affero General Public License version 3]: https://www.gnu.org/licenses/agpl-3.0.en.html

## Documentation

The documentation should be a key aspect of any project. For this reason, at **voxels-core**
we prioritize top-notch documentation that is easy to read and understand and delves into
the reasoning behind the design decisions.

You can find the documentation on ou [GitHub Pages] website thanks to the [Dokka] 
documentation engine and [GitHub Actions].

[GitHub Pages]: https://warpedvoxels.github.io/voxels-core/
[GitHub Actions]: https://github.com/features/actions
[Dokka]: https://github.com/Kotlin/dokka

## Status of the project

voxels-core is currently in its early development stages, and there is much work 
to be done. As such, expect breaking changes and potential instability until the
project reaches a stable version.

- [x] A DSL command framework based on Mojang's Brigadier
- [x] ~~Paper network pipeline interception system~~ I've decided to work on
  Kotlin extensions for [packetevents] instead as it is such a nice project
- [x] `kotlinx.coroutines` integration on top of the Bukkit scheduler and
  Velocity event continuations
- [x] Support to event cold streams with coroutines' `Flow`
- [ ] `kotlinx.serialization` integration with entities, inventories, 
  blocks, items, and other game objects
- [ ] TOML-based configuration system for extensions
- [ ] Jetpack Compose UI framework for server-side Minecraft environments
- - [ ] Inventory-based rendering with simple materials
- - [ ] Inventory-based rendering with custom textures
- - [ ] HUD-based rendering with custom fonts and textures
- - [ ] Item-frame-based rendering system
- - [ ] State and change tracking
- [ ] Behaviour packs integrated with code-based resource pack generation system
- - [ ] Custom blocks and items
- - [ ] Custom block hardness
- - [ ] Fonts and texture rendering
- - [ ] Custom sounds and music
- - [ ] Custom particles and effects
- - [ ] Custom dimensions and biomes
- - [ ] Custom structures and world generation
- - [ ] Custom mobs
- - - [ ] Animations and texture
- - - [ ] Pathfinding and AI

[packetevents]: https://github.com/retrooper/packetevents

## Join our community!

**Southdust Labs** is a general chatting community for developers, gamers, and thinkers alike. Our approach on
internalisation is to provide a safe and welcoming environment where everyone can feel comfortable and valued, regardless
of their cultural background.

We are a community of people who love to learn, discover, and share our knowledge with others. We are a place where
people can ask questions, share their ideas, and collaborate on projects.

<a href="https://discord.gg/gRFnmxHkFb">
    <img src="https://img.shields.io/discord/908438033613848596?colorA=1e1e28&colorB=e0a621&style=for-the-badge&logo=discord&logoColor=e0a621" />
</a>
