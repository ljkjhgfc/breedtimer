# 🐄 Breed Timer — Fabric 1.21.1 Mod

Shows a **draggable, resizable HUD timer** for every mob you breed. Each timer is labeled with the mob name and counts down the 5-minute cooldown.

---

## Features

- ⏱️ Per-mob countdown timers (e.g. "Cow: 4:58", "Sheep: 2:31")
- 🖱️ **Drag** each timer widget anywhere on screen
- 🔧 **Scale** timer size from 0.5x to 2.0x
- 🔄 Progress bar fills as cooldown expires
- 🟢 Color-coded border: gray → yellow (2 min left) → green (30s left)
- Works with **all breedable mobs** (Cow, Pig, Sheep, Chicken, Horse, Llama, etc.)
- Config saved to `.minecraft/config/breedtimer.json`

---

## Setup & Building

### Requirements
- Java 21+
- [Fabric Loader 0.16.5+](https://fabricmc.net/use/installer/)
- [Fabric API](https://modrinth.com/mod/fabric-api)

### Build from Source
```bash
git clone <this-repo>
cd breedtimer
./gradlew build
```
The built `.jar` will be at `build/libs/breedtimer-1.0.0.jar`.

### Install
1. Copy the `.jar` into your `.minecraft/mods/` folder
2. Make sure Fabric API is also in `mods/`
3. Launch Minecraft 1.21.1 with Fabric

---

## Controls

| Action | Default |
|--------|---------|
| Open settings screen | `K` |
| Drag timer widget | Left-click + drag |

You can rebind the settings key in **Options → Controls → Breed Timer**.

---

## Project Structure

```
src/
├── main/java/com/breedtimer/mixin/
│   └── AnimalEntityMixin.java     ← Detects breeding events
├── client/java/com/breedtimer/client/
│   ├── BreedTimerClient.java      ← Mod entrypoint
│   ├── BreedTimerData.java        ← Timer state management
│   ├── BreedTimerHud.java         ← HUD rendering + drag logic
│   ├── BreedTimerConfig.java      ← Config load/save (JSON)
│   └── BreedTimerConfigScreen.java← In-game settings screen
└── main/resources/
    ├── fabric.mod.json
    ├── breedtimer.mixins.json
    └── assets/breedtimer/lang/en_us.json
```

---

## How It Works

1. `AnimalEntityMixin` hooks into `setLoveTicks()` — called whenever an animal enters "love mode" after being fed
2. The mob's display name is passed to `BreedTimerData` which tracks the 6000-tick (5 min) countdown
3. `BreedTimerHud` renders a widget per active timer, with drag support via mouse tracking
4. Settings (positions, scale) are persisted to `config/breedtimer.json`
