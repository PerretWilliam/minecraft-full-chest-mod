# **FullChest – Expanded Chest Storage for Minecraft**

FullChest is a Minecraft mod that adds **progressively upgradeable chests** with unique capacities, textures, and sounds.
Players can upgrade their chests through crafting or special upgrade items, keeping their stored items safe during the process.

---

## **Features**

* 🪵 Multiple chest tiers from Dirt to Netherite.
* 📦 Increased storage capacity per upgrade.
* 🎨 Unique textures and particle effects for each tier.
* 🔊 Custom sounds for interaction, breaking, and placing chests.
* 🛠 Upgrade via crafting or using special upgrade items (shift + right-click).
* 🎯 Built-in advancements for each upgrade path.
* ♻ Items remain in the chest when upgrading.

---

## **Requirements**

* **Minecraft**: 1.21.1
* **NeoForge**: Latest stable release (matching your MC version)
* **Java**: 17+

---

## **Installation (Players)**

1. Download the latest **FullChest** release from [GitHub Releases](#) or [Modrinth/CurseForge](#).
2. Install [NeoForge](https://neoforged.net/) for your Minecraft version.
3. Place the downloaded `.jar` file into your Minecraft `mods/` folder.
4. Launch Minecraft and enjoy!

---

## **Cloning & Building (Developers)**

If you want to work on or contribute to FullChest, follow these steps:

### **1. Clone the repository**

```bash
git clone https://github.com/PerretWilliam/FullChestMod.git
cd FullChest
```

or

```bash
git clone git@github.com:PerretWilliam/FullChestMod.git
cd FullChest
```

### **2. Open in your IDE**

It is recommended to use:

* [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (recommended)
* [Eclipse](https://www.eclipse.org/downloads/)

### **3. Setup project**

Run the following commands:


#### Refresh dependencies
```gradlew --refresh-dependencies```

#### Clean build cache (does not delete your code)
```gradle clean```

### **4. Build the mod**

```gradlew build```

The compiled `.jar` will be in `build/libs/`.

---

## **Mappings**

FullChest uses **official Mojang mappings** for method and field names in the Minecraft codebase.
These names are under a specific license: [Mojang License](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).

---

## **Resources**

* 📖 [NeoForge Documentation](https://docs.neoforged.net/)
* 💬 [NeoForge Discord](https://discord.neoforged.net/)

---

## **License**

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.