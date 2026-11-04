# Spool Studio

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue)
![Version](https://img.shields.io/badge/Version-3.1.3-cyan)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)
[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-FFDD00?logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/georgho)

Spool Studio is an Android app for managing 3D printer filament spools with Spoolman, NFC/RFID tags, and Snapmaker U1 printer integration.

Version 3.1.3 adds per-server tag storage selection (legacy only, native only, or both) and keeps readable RFID colors available when Spoolman is offline. Existing filament-name preservation, dark UI contrast fixes and separate printer-clear/tag-unlink actions remain available.

The previous 3.0 series was tested with **Paxx12 Extended Firmware v1.6.0**. The 3.1.3 storage/offline workflows were tested on a U1 with Spoolman 0.27.0, using `2.0.0-paxx12-24` for the initial legacy checks and `2.1.0-paxx12-pr867` for native tags and return migration.

This project started from the open-source project **SpoolPainter** by ni4223 and has since been heavily extended.

---

## Support

If Spool Studio helps you, you can support the project here:

[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-FFDD00?logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/georgho)

---

## Download

[Download the latest Spool Studio release](https://github.com/GeorgHo/SpoolStudio/releases/latest)

If you still use older Paxx12 firmware or the old Spool Studio tag workflow, use the latest v2 release instead:

[Download Spool Studio v2.0.4](https://github.com/GeorgHo/SpoolStudio/releases/tag/v2.0.4)

---

## Screenshots

<p align="center">
  <img src="docs/images/v3_main.png" width="260" alt="Spool Studio v3 main screen"/>
  <img src="docs/images/v3_toolhead_status.png" width="260" alt="Spool Studio v3 toolhead status"/>
  <img src="docs/images/v3_settings.png" width="260" alt="Spool Studio v3 settings"/>
</p>

---

## Important for v3

### Native tags in v3.1

Version 3.1.3 includes the tested storage/offline workflows. Five live test groups passed.

[Ausführliche deutsche App-Anleitung für v3.1.3](docs/APP-GUIDE-DE.md) — setup, storage modes, migration, tag linking, offline reads and toolhead actions.

- Spoolman 0.27.0 or newer: native tags are read first. By default, new links are also written to `card_uids` for existing Paxx12 firmware.
- Older Spoolman servers (or compatible servers without `/api/v1/info`): the existing `card_uids` workflow remains available.
- Settings → Spoolman → **NFC / RFID tag storage** selects **Both (compatibility)**, **Only card_uids**, or **Only native tags**, per server. Both remains the default, and the previous native-only preference is preserved when upgrading. In a single-store mode, reads, links and unlinks ignore the other store. Native-only requires Spoolman 0.27.0 or newer; legacy-only does not call the native tag API.
- Match the app mode to the printer's selected store. Choosing a mode does not migrate or delete data. For the firmware's storage switch and return migration, use firmware-config (PR #867); copying legacy entries to native tags in the app remains an explicit, confirmed operation available in Both/native-only modes.
- **Review legacy tag migration** previews the number of entries, including archived spools. Migration copies links by default. Clearing successfully migrated legacy entries is an explicit option, and can break older firmware's automatic lookup. Duplicate, invalid or conflicting entries remain for review.
- A native filament tag identifies a product, not a particular spool. Scanning it displays a message instead of selecting a spool automatically.
- **Manage tag links** lists UIDs from the selected store(s). Unlink requires confirmation and affects only those store(s). **Link scanned UID** can explicitly move a native spool tag from its previous owner; ordinary saves do not silently move native tags.
- The tag content format is unchanged. OpenTag3D encoding/decoding and external scanner subscriptions are not part of this release.

### Filament names and clearing assignments

- Existing filament names retain their spelling and full length when editing a spool or changing its color. The **Filament Name** field changes the product name explicitly. A bordered **Use color name: …** button appears only when the name differs from the color suggestion; matching names do not produce an extra action.
- **Toolhead Status → pencil icon → Clear printer assignment** clears the toolhead mapping and keeps tag links. A subsequent RFID scan may assign the spool again. This intentionally avoids firmware `2.0.0-paxx12-24`'s `SET_SPOOL_ID=0`, which also removes legacy UID links.
- **Manage tag links → Unlink** removes the selected tag's links from the selected store(s) after confirmation. It does not clear an already loaded printer assignment.
- Firmware `2.0.0-paxx12-24` still uses `card_uids`: retain compatibility mode and keep the legacy field when copying links to native tags.

### RFID reading without a server

Readable OpenSpool tag contents, including color, are displayed immediately without requiring a successful Spoolman request. Already decoded Bambu metadata can also be displayed locally. A connection failure keeps these values in the form; an embedded spool ID or cached UID match is not treated as a confirmed server assignment. A successful server lookup still takes priority.

An empty or encrypted tag that supplies only a hardware UID has no readable color to display offline. Existing key/decoder requirements for vendor tags are unchanged.

Spool Studio v3 is built for the new Paxx12 SpoolLink workflow.

- v3 no longer writes legacy Spool Studio / spool-id based tags.
- The physical NFC tag UID is used as the primary link between tag and Spoolman spool.
- The UID is stored in Spoolman and used by Paxx12 SpoolLink.
- Old tags can be detected and converted after confirmation.
- If you decline conversion, v3 will not write legacy data.

Recommended migration workflow:

1. Install Spool Studio v3.
2. Check Spoolman and printer integration in Settings.
3. Read one existing tag.
4. Convert only one spool first.
5. Verify the result in Spoolman and the Paxx12 Filament Manager.
6. Continue converting the remaining spools after a successful test.

---

## Highlights

- Paxx12 SpoolLink support for the new 1.5.x firmware workflow
- Spoolman support for reading, creating, updating, and deleting spools
- NFC/RFID read and write support for OpenSpool-compatible tags
- Legacy tag detection and guided conversion
- Physical NFC card UID storage in Spoolman
- Material, material modifier, and variant handled as separate fields
- Guided Spoolman legacy material conversion for older v2 data
- Checks for the required Paxx12 `SET_SPOOL_ID` command
- Searchable dropdowns for spools, materials, variants, brands, colors, locations, and spool tare weights
- Bambu Lab RFID import when a user-provided key is configured
- Toolhead Status view for printer-reported spool assignments
- Manual toolhead spool assignment with clear UID warnings
- Modern dark UI designed for quick spool editing on a phone

---

## Quick Start

### 1. Install the App

- Download the APK from the v3 release.
- Install it on your Android device.
- Enable "Install unknown apps" for your browser or file manager if Android asks for it.

### 2. Connect Spoolman

- Open **Settings**.
- Enter your Spoolman URL, for example `http://spoolman.local:8000`.
- Tap **Test Spoolman Connection**.
- Use **Show Spoolman Info** to inspect the connected Spoolman instance.

Spool Studio can check whether the required Spoolman fields are available and can guide the setup for v3-specific fields.

### 3. Connect the Snapmaker U1

- Install a compatible Paxx12 1.5.x firmware build with SpoolLink support.
- Enable the Spoolman integration in the printer firmware settings.
- Enter your Moonraker URL in **Settings**.
- Tap **Test Moonraker Connection**.

### 4. Use NFC / OpenSpool Tags

- Tap **Read RFID** to scan an existing tag.
- If an old Spool Studio tag is found, the app can offer conversion to the new UID-based workflow.
- Tap **Write RFID** to store the current spool data on a tag.

### 5. Check Toolhead Status

Use **Toolhead Status** to view the spool data reported by the printer. The dialog can show app-composed labels and SpoolLink-reported data for comparison.

### 6. Optional: Bambu Lab RFID

Bambu Lab RFID support requires a user-provided master key. The key is not included in this project.

---

## Features

### Spoolman

- Load existing spools from Spoolman
- Create a completely new spool
- Create a new spool from a selected spool
- Update spool details
- Delete a selected spool after confirmation
- Store NFC card UIDs for Paxx12 SpoolLink
- Store material modifiers through Spoolman extra fields
- Use Spoolman catalogs for materials, brands, locations, and known spool weights
- Display Spoolman server information in Settings

### Filament Data

- Material
- Material modifier, for example Plus or HS
- Variant
- Brand
- Color, color name, and HEX value
- Remaining filament weight
- Optional empty spool weight
- Optional location, product / lot code, and comment
- Nozzle and bed temperature ranges

### NFC / RFID

- Read OpenSpool tags
- Write OpenSpool tags
- Read and convert legacy Spool Studio tags
- Link physical tags and Spoolman records through the NFC card UID
- Keep tag data compatible with the Paxx12 SpoolLink workflow

### Printer Integration

- Check Moonraker reachability
- Check Paxx12 firmware integration readiness
- Show current toolhead spool status
- Compare app labels and SpoolLink-reported data
- Show clear status and error messages instead of raw dumps

### Bambu Lab RFID

- Read Bambu Lab RFID tag data when a key is configured
- Apply parsed Bambu Lab material, color, weight, and temperature data to the spool form
- Keep key handling user-controlled

---

## Requirements

- Android device with NFC
- Android 10 / API 29 or newer
- Spoolman server for the main workflow
- Paxx12 Extended Firmware 1.5.x with SpoolLink support for the v3 printer workflow
- Moonraker access to the Snapmaker U1
- Optional: user-provided Bambu Lab RFID key

---

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- NFC API
- Coroutines
- MVVM architecture
- Spoolman API
- Moonraker API

---

## Credits

- Original project: [SpoolPainter by ni4223](https://github.com/ni4223/SpoolPainter)
- OpenSpool for the open filament tag data format
- Spoolman for filament spool management
- paxx12 for the Snapmaker U1 Extended Firmware
- Moonraker and the Klipper ecosystem

Spool Studio is extended and maintained by Hovi.

---

## License

This project is based on the original SpoolPainter project by ni4223.

Permission to publish this modified version was granted by the original author.

This project is licensed under the MIT License.

---

## Disclaimer

Spool Studio is an independent project and is not affiliated with Snapmaker, Bambu Lab, Spoolman, OpenSpool, Moonraker, Klipper, or the original SpoolPainter developer.

Bambu Lab RFID tags use proprietary encryption and access keys. This project does not provide official keys. Any key usage must be supplied and evaluated by the user.
