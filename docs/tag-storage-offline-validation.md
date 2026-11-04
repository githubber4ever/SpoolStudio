# Tag storage and offline RFID validation

Version 3.1.3 implements PR #867's single-store contract. Five live U1 test groups passed with Spoolman 0.27.0: initial legacy checks used 2.0.0-paxx12-24; native mode and return migration used 2.1.0-paxx12-pr867. Firmware PR #867 was merged on 2026-11-04. See the [app guide](APP-GUIDE-DE.md) for setup and the distinctions between app mode selection and firmware migration.

## Storage selection

| App mode | Reads | Link/unlink | Other store |
|---|---|---|---|
| Both (default) | Native first, then legacy; old servers use legacy | Both on Spoolman 0.27.0+, legacy on older servers | Both enabled |
| Only card_uids | Legacy only; native owners are ignored | Legacy only | Native tags untouched; no native API calls |
| Only native tags | Native only; requires Spoolman 0.27.0+ | Native only | Legacy fields untouched |

Choices are saved per server URL. The old native-only checkbox preference maps to Only native tags; other existing installations retain Both. Selecting a mode never automatically migrates data. Changing storage or completing a migration clears the current form selection and scanned UID so a stale link cannot be silently copied into another store on the next save. The app's explicit legacy-to-native copy/migration requires Both or Only native tags; clearing legacy entries remains a separate opt-in action. Use firmware-config for the printer's store switch and reverse migration.

## Automated checks

- API request tests verify legacy-only lookup, assignment and unlink never use the native endpoints.
- Native-only unlink leaves the legacy mirror untouched; existing tests cover native-only lookup/link, unsupported servers, native-first behavior in Both, duplicate UIDs and migration conflicts.
- Catalog tests verify native ownership does not affect legacy-only results and does take priority in Both.
- Preference parsing preserves the old checkbox choice and round-trips each new mode.
- Offline read tests cover no server, connection failure, OpenSpool color through form loading, UID-only tags, decoded Bambu color/weight, successful server precedence and cancellation.

## Manual checks on a U1

The five-step live test plan, including requirements and expected results, is in [LIVE-TESTS-DE.md](../release-assets/v3.1.3/LIVE-TESTS-DE.md). It covers offline colors, legacy-only assignment/moving, both stores, separate clearing/unlinking, and the firmware's native migration plus reverse migration. Tests 1–4 can use stable firmware -24; test 5 requires #867. Back up Spoolman before the firmware migration, which affects the entire server's tag links and restarts Moonraker.

Never infer a successful physical firmware test from the automated results. No live server data is migrated by the automated tests (MockWebServer fixtures only).
