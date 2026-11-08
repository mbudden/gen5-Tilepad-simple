# Building and releasing

For people working on Tilepad Simple. Installing it is in the [README](../README.md).

## What the installer does

The package lives in `/data/nemesis/apps/tilepad/`. The installer places
`/system/app/Tilepad.apk` and `/system/app/TilepadTweaks.apk`; NEMESIS still runs just
`tilepad/longstar`. Enabled tweak listeners share that process. Explicitly enabled static
tweaks change exact supported system files with verified stock backups; disabling restores
them, and uninstall restores owned tweaks before removing the APKs, then deletes Tilepad's
saved tweak settings (including the Charge-Away Home location). Turning on the Wi-Fi
client remains a separate confirmed action (see [TILES.md](TILES.md)).

Tweaks support is keyed to firmware files rather than the car model, so it covers any car on the
supported firmware release (USA `V015.012.260507` and `V016.010.260903`); heaters a trim lacks are skipped.
On other firmware, an unavailable tweak offers a compatibility log to attach to an issue. Feature behavior,
compatibility, overhead, ownership and recovery are in [TWEAKS.md](TWEAKS.md).
The [mission/checkpoint](TWEAKS-MISSION.md) records completed host validation and
remaining limits; [the consolidated checklist](TWEAKS-DEVICE-CHECKLIST.md) covers
the emulator and car checks still required. In particular, the new pop-over patch's
runtime behavior is not yet verified on a head unit.

## Car test checklist

Things that can't be verified on an emulator, in rough priority order:

- [ ] Holding ★ on the steering wheel opens Tilepad, and holding it again closes it; a short ★ press still runs its stock action. If not, check NEMESIS Manager → Services → `tilepad/longstar`.
- [ ] Grid fit: expect 6 columns × 2 rows of ~185dp squares, clear of the split-screen handle on the right.
- [ ] Wi-Fi tile: name, IP and signal look right. Tapping opens the network picker.
- [ ] On a hotspot-only unit: the Wi-Fi tile reads *Hotspot only*. *Turn on* → *Reboot* → the tile shows the network state and opens the picker.
- [ ] Battery: shows *Ready* with **Start** when READY with conditioning enabled in Hyundai's settings. Start → Waiting → Active (60–120 s), then tap → Off.
- [ ] Nemesis tile opens NEMESIS Manager.
- [ ] Day/Night: switching the head unit's display mode redraws Tilepad in the matching colors.
- [ ] Add: the list loads, the keyboard filters it, and an added tile launches its screen.
- [ ] Reboot: confirm → the head unit restarts. If NEMESIS's root channel refuses, the popup shows its output.

If something fails, `adb logcat | grep -i tilepad` (with ADB enabled) or NEMESIS Manager →
Logs → Copy to USB helps.

## Build

```sh
./gradlew :app:testDebugUnitTest            # unit tests (tile order, tile support, grid math, presenter, search, backend)
sh tools/build_apk.sh                       # tests + lint + signed v1 APK → dist/Tilepad.apk
                                           # also builds/signs dist/Tweaks.apk
python3 tools/build_usb_bundles.py          # → dist/tilepad.zip + Tilepad-{install,update,uninstall}-usb.zip
```

`build_apk.sh` signs with `keystore/tilepad.jks`, whose password is in `keystore/password`. Both
are gitignored. **Back them up.** Future updates should be signed with the same key. If
`tools/signing-cert.sha256` exists, the build fails unless the APK's signing certificate has that
SHA-256 (`apksigner verify --print-certs dist/Tilepad.apk` prints it).

### Releases

GitHub Actions builds, signs and publishes releases. Bump `versionName` (and `versionCode`) in
`app/build.gradle.kts` and `package-version=` in `tune-service/NEMESIS.data` (its `version=` is
the descriptor format, not Tilepad's version), commit, then push a matching tag:

```sh
git tag -a v1.0.1 -m "What changed"   # the message becomes the release notes
git push origin v1.0.1
```

The [release workflow](../.github/workflows/release.yml) checks that the three versions match, runs
`build_apk.sh` (tests, lint, signing, certificate check) and `build_usb_bundles.py`, and publishes the
APK, `tilepad.zip`, the three USB bundles and `SHA256SUMS`. The job holding the key can only read the
repo; a separate job publishes. Every push and pull request runs the same build with a throwaway key.

The key comes from repository secrets:

```sh
base64 -i keystore/tilepad.jks | gh secret set TILEPAD_KEYSTORE_B64
gh secret set TILEPAD_STORE_PASSWORD < keystore/password
# only if the key's password differs from the store's: gh secret set TILEPAD_KEY_PASSWORD
```

The debug build simulates the vehicle on an emulator (`ranchu`/`goldfish`). Launch it with
`--ez design_preview true` to get a fake Wi-Fi connection for screenshots.

### Source map

| | |
| --- | --- |
| `MainActivity` | tiles, popups, preconditioning popup flow, Wi-Fi polling, TUNE knob navigation |
| `TileGrid`, `PagedGridMath` | square tiles on swipeable pages with dots, fixed core block on page one, long-press drag reorder across pages, edit mode |
| `TileView` / `StatusTile` / `WifiTile` / `PreconTile` | tile views |
| `AddShortcutPage`, `KeyPad` | screen picker and built-in keyboard |
| `Sheet`, `StatusCard` | popup layer |
| `TileOrder`, `TileSpec`, `TileSupport`, `GridMath`, `PreconPresenter`, `SearchFilter` | pure, unit-tested logic (`TileSupport` decides which core tiles the car can back) |
| `Preconditioning*`, `OemVehicleClient`, `VehiclePort` | Launchpad backend updates adapted to Tilepad's existing service and tile flow |
| `longstar/*` | the steering-wheel ★ hold service (passive evdev reader, hold timing) |
| `RootShell` | `nemesis root -c` (what NEMESIS Manager uses) |
| `tune-service/bin/wifi-client.sh` | root side of *turn on Wi-Fi* (run through `RootShell`) |
| `tweaks/` | standalone Android settings APK, checkbox rows, help and option pages |
| `tweaks-core/` | shared in-process listeners, OEM adapters, config and reversible patch transactions |
| `tune-service/tweaks/patches/` | tiny, exact-file-gated recipes; no OEM APK/ODEX shipped |

Preconditioning compatibility and safety notes are in Launchpad's
[COMPATIBILITY.md](https://github.com/paytondevs/gen5w-preconditioning/blob/main/COMPATIBILITY.md).
The pinned profiles, imported fixes and validation are recorded in
[the upstream integration notes](PRECONDITIONING-UPSTREAM.md).

Credits: [NEMESIS](https://github.com/xwtk/hmg_nemesis), Launchpad by paytondev,
[Ioniq5_CAN](https://github.com/tylerharvey/Ioniq5_CAN),
[gen5w-utils](https://github.com/Helloyunho/gen5w-utils).
