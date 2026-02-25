<div align="center">

# FullChest

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-latest-E04E14?style=for-the-badge)](https://neoforged.net/)
[![Java](https://img.shields.io/badge/Java-17+-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

**Expanded chest storage for Minecraft — progressively upgradeable chests with unique capacities, textures, and sounds.**

</div>

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Building from Source](#building-from-source)
- [Mappings](#mappings)
- [Resources](#resources)
- [License](#license)

---

## Features

- Multiple chest tiers from Dirt to Netherite
- Increased storage capacity per upgrade tier
- Unique textures and particle effects for each tier
- Custom sounds for interaction, breaking, and placing
- Upgrade via crafting or special upgrade items (Shift + Right-click)
- Built-in advancements for each upgrade path
- Items are preserved inside the chest when upgrading

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | Latest stable (matching your MC version) |
| Java | 17+ |

---

## Installation

1. Download the latest **FullChest** release from [GitHub Releases](#) or [Modrinth / CurseForge](#)
2. Install [NeoForge](https://neoforged.net/) for your Minecraft version
3. Place the downloaded `.jar` file into your `mods/` folder
4. Launch Minecraft and enjoy

---

## Building from Source

### 1. Clone the repository

```bash
git clone https://github.com/PerretWilliam/FullChestMod.git
cd FullChest
```

Or via SSH:

```bash
git clone git@github.com:PerretWilliam/FullChestMod.git
cd FullChest
```

### 2. Open in your IDE

Recommended editors:
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (recommended)
- [Eclipse](https://www.eclipse.org/downloads/)

### 3. Set up the project

Refresh dependencies:

```bash
gradlew --refresh-dependencies
```

Clean build cache (does not delete your code):

```bash
gradle clean
```

### 4. Build the mod

```bash
gradlew build
```

The compiled `.jar` will be output to `build/libs/`.

---

## Mappings

FullChest uses **official Mojang mappings** for method and field names in the Minecraft codebase. These names are distributed under a specific license: [Mojang License](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).

---

## Resources

- [NeoForge Documentation](https://docs.neoforged.net/)
- [NeoForge Discord](https://discord.neoforged.net/)

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
