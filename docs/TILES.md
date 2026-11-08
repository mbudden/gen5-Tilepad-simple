# Tiles

Everything each tile shows and does. The [README](../README.md) has the short version.

| Tile | Shows | Tap |
| --- | --- | --- |
| **Wi-Fi** *(Wi-Fi module only)* | a green dot and *Connected* with the network name, IP and link speed; *No network*, *Off*, or *Hotspot only* when the client is off | Android's Wi-Fi network picker, or *turn on Wi-Fi* (see below). Long-press: the Wi-Fi MAC address, for a static IP reservation |
| **Battery** *(EVs only)* | *Ready* with a **Start** button; *Waiting* with a spinner and the elapsed time; *Active* with **Turn off**; or Unavailable / Error with a short reason | see below |
| **Tweaks** | how many tweaks are on (*4 on*, *All on*, *Off*) and how many are off; green dot when any is on | opens the bundled `Tweaks.apk`; each feature has a checkbox and detailed explanation |
| **ADB** | *On* with the port, or *Off* (*Port closed*); green dot while on | asks to confirm, then turns ADB over Wi-Fi on or off (see below) |
| **Nemesis** | | NEMESIS Manager (dimmed if it's not installed) |
| **Reboot** | | asks to confirm, then hard-reboots the head unit (`nemesis root -c reboot`) |
| **Add** | | searchable list of every screen (apps, plus Hyundai/system screens with no icon) |

Colors follow the head unit's Day/Night display setting. A shortcut whose own icon would blend
into its tile (a white icon in day mode, say) gets a thin edge or a gray plate behind it.

**Battery preconditioning, one button:**
- Off → tap: sends Start and pops up a progress popup. It shows *Waiting for car* (usually 1–2 minutes) and turns into a ✓ *Preconditioning active* when the car reports it. If the popup was closed by then, it pops up on its own.
- Waiting → tap: reopens the popup, which has **Cancel request**. A stray tap won't cancel.
- Active → tap: turns it off. The popup shows *Turning off*, then *Off*.
- Anything else (Unavailable, Error, not READY…) → tap: a popup with the car's reason.

"Active" only ever comes from the car's own indicator, never from a timer. The backend is
Launchpad's, unchanged: firmware-hash gate, ownership journal, cancel-on-recovery, and a
foreground service that keeps the request alive after the app closes.

**Turning on the Wi-Fi client.** Stock US firmware, and wireless CarPlay/Android Auto mods
installed without their Wi-Fi client option (HMG Leafia Plus's `LEAFIA-WIFI-CLIENT`), use the
radio only for the projection hotspot. The tile then reads *Hotspot only*, and tapping it offers
to turn the client on. That does what Leafia's option does: it adds
`/system/etc/permissions/android.hardware.wifi.xml` (Hyundai's Wi-Fi service and settings app
read it as "client supported") and sets `persist.wifi.state=3` (client + wireless). It doesn't
write `build.prop`; the persistent property survives reboots on its own. The popup then offers a
reboot, since Android reads features at boot. Until then, the tile reads *Reboot to finish*.

- It's idempotent and never touches a file that's already there, so it can't fight Leafia. If
  Leafia is installed later with its client option, it writes the same file. If Leafia was
  installed first without it, Leafia's uninstall restores its snapshot and removes the client
  again, and the tile offers it again.
- There's one Wi-Fi radio. While it's on a network, wireless CarPlay/Android Auto may not connect.
- Uninstalling Tilepad turns the client back off (after the reboot) if Tilepad turned it on:
  it removes the file, if it's still exactly what Tilepad wrote, and puts `persist.wifi.state`
  back. It leaves the client alone while HMG Leafia Plus is installed, since Leafia's option
  uses the same file.

**ADB over Wi-Fi.** The ADB tile turns ADB over Wi-Fi on and off. Tapping it when it's off asks
to confirm, because of what "on" means:

> ⚠️ While ADB is on, **any computer that can reach the head unit can connect and get a
> root-capable shell**, with no key to approve: this head unit's adbd doesn't check keys over
> Wi-Fi. That includes the telematics modem's cellular connection, not just your Wi-Fi. Only turn
> it on while you need it, and turn it off when you're done.

> ⚠️ **Wireless CarPlay and Android Auto don't work while Wi-Fi is on.** The head unit has one
> Wi-Fi radio: it either joins a network or runs the hotspot your phone uses for wireless
> CarPlay, not both. While ADB is on, Tilepad turns the hotspot off and Wi-Fi on, at every boot
> too.

- **On** sets the firmware's own switch for ADB over TCP (`persist.service.adb.tcp.enable`), so
  adbd listens on port 5555, now and after every reboot, until you turn it off. Connect with
  `adb connect <head-unit-ip>:5555`.
- **Wi-Fi.** ADB needs the head unit on the same network as your computer, so while ADB is on,
  Tilepad turns on Wi-Fi (joining your saved network), right away and again at every boot. On a
  unit whose Wi-Fi only runs the CarPlay hotspot, turn on Wi-Fi from the Wi-Fi tile first.
- **Off** clears that switch and stops adbd, so nothing listens for ADB over TCP. It leaves Wi-Fi
  as it is.
- The head unit serves **one computer at a time**: while one is connected, another's
  `adb connect` hangs and shows `offline` or `unauthorized` until the first disconnects
  (`adb disconnect`).
- It needs NEMESIS (for root); without it the tile says so and does nothing.
- An **update** keeps ADB in whatever state you left it. **Uninstalling** Tilepad turns it off if
  the tile turned it on.

**Arranging:**
- Page one starts with a fixed block of six core tiles: Wi-Fi, ADB and Nemesis on top, Tweaks, Battery and Reboot below. A wider gap separates it from your tiles; unsupported vehicle features are still omitted.
- Your tiles fill the rest of page one (six on the car). More continue on further pages, with dots at the bottom: swipe left and right. Later pages have no fixed block.
- Long-press any tile, then drag it. Tiles wiggle while you're in edit mode.
- In edit mode, shortcuts get ✎ (rename) on the top-left and − (remove) on the top-right. The six fixed tiles don't move; Add can be moved but not removed or renamed. Drag a tile to a screen edge to carry it to the next page.
- Leave edit mode with **Done**, by tapping empty space, or with Back.
- New shortcuts are added just before the Add tile. Renaming to an empty name restores the original name.

Search and rename use a built-in keyboard, so no Android IME is needed. Long-press ⌫ to clear.
The TUNE knob also works: turn it to move the highlight and press it to open the tile.

