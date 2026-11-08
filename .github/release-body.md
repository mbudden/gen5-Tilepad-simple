Tilepad Simple @VERSION@: [Tilepad](https://github.com/KramerTech/gen5-Tilepad) with a cleaner look, for Hyundai/Kia/Genesis Gen5W head units, installed through [NEMESIS](https://github.com/xwtk/hmg_nemesis).
@CHANGES@
**Install by USB** (no computer needed in the car): extract the zip so its *contents* sit at the root of a FAT32/exFAT drive with nothing else from NEMESIS on it, plug it in with the head unit fully booted, and tap **Yes**. Reboot the head unit afterwards. Details are in the [README](https://github.com/mbudden/gen5-Tilepad-simple#install).

| File | Use |
| --- | --- |
| `Tilepad-install-usb.zip` | first install (uninstall KramerTech's Tilepad and Launchpad first) |
| `Tilepad-update-usb.zip` | update an existing install, keeping your tiles |
| `Tilepad-uninstall-usb.zip` | remove Tilepad |
| `tilepad.zip` | the NEMESIS package itself, for `nemesis install` over ADB (don't extract it onto a drive) |
| `Tilepad.apk` | the signed APK, for reference |
| `Tweaks.apk` | bundled settings app; runtime and patches are in the Tilepad package |

Check downloads against `SHA256SUMS`. Built and signed by GitHub Actions from this tag.
