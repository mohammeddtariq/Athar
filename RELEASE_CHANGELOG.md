# Introducing Hisn al-Muslim - حِصْنُ الْمُسْلِم: (Release 1.0.8-beta)

> *«فِي حِصْنِ الذِّكْرِ يَسْكُنُ الْقَلْبُ، وَبَيْنَ السُّوَرِ يَهْدَأُ الْفِكْرُ»*  
> *"In the fortress of remembrance the heart finds rest; between the surahs, the mind grows still."*

# What changed?

### ✨ New Features

- **Hisn al-Muslim (حِصْنُ الْمُسْلِم) — The Complete Dua Library**:  
  The full Hisn al-Muslim collection joins Athar as its eighth library, with every chapter, dhikr, and reference available offline. It introduces:
  - **Full Offline Library**: The complete supplication collection, organized by chapter, ready whenever you need it.
  - **Library Search**: Find any dua instantly by its text, title, or translation.
  - **Rebuilt Dua Cards**: Larger touch targets, clearer Arabic, and a calmer reading rhythm.
  - **Completion Celebration**: Finish any dua collection — not only the after-prayer adhkar — and the app celebrates with you.
  - **Auto-Advance Toggle**: Let the after-prayer counter move forward on its own, or keep it fully manual.

- **A Calmer Home**:  
  The home screen has been rebuilt around stillness and touch:
  - **Wallet-Style Prayer Deck**: Prayer times now live in stacked, draggable cards that fan open under your thumb, with a Mihrab watermark and ornament rule running through them.
  - **Unified Hero Card**: The next-prayer card is now part of the same deck, and upcoming occasions sit below it instead of competing for attention.
  - **Floating Header**: The header drifts over a fading scrim, matching the Duas screen, so the content never feels boxed in.
  - **Poetic Prayer Lines**: Every prayer carries a fresh, quiet line of its own — no longer opening with the prayer's name.
  - **Next Hijri Occasion**: The home screen gently surfaces the next blessed occasion.

- **Quran Index Reimagined**:  
  The surah and juz lists now speak the same visual language as the prayer deck:
  - **Expanded Cards**: Tap any surah or juz to expand it in place, with the Amanah rosette marking your place.
  - **Wallet Stacking**: The lists stack like a wallet, so you always feel where you are.
  - **Floating Header**: The index header floats and fades as you scroll, and the first card is no longer clipped.

- **Unified Offline Search**: One search now reaches across the Quran and the dua libraries, entirely offline.

- **Share as a Designed Image**: Share duas and tafsir passages as beautifully composed cards, rendered at higher resolution on a solid, quieter background.

- **Settings Backup & Restore**: Export your settings to a JSON file and restore them whenever you move to a new device.

- **Smarter Updates**: Release notes now render as formatted markdown, downloads are verified against GitHub's SHA-256 digest, and GitHub is polled from a wake-up alarm so new releases are announced in the background.

- **Per-Minute Reminder Adjustment**: Fine-tune prayer reminders minute by minute.

---

### 🐛 Bug Fixes & Improvements

- **Printed Mushaf Restored**: Brought back the printed SVG page renderer, with authentic Madani line slots, aligned surah headers, and correct line fitting.
- **Reader Comfort**: Vertical swipes now scroll the mushaf page from anywhere, and adjacent pages are pre-parsed for smoother turns.
- **Al-Dirayah Timing**: The tafsir tip now appears after the reader-controls hint clears.
- **Quran Index Spacing**: Added breathing room between Al-Dirayah and the surah list, and aligned the continue-reading card with the floating nav dock.
- **Offline Clarity**: Every reading mode is bundled in the APK, and the reading-mode panel now says so.
- **Dua State Persistence**: Category, chapter, search, and counts now survive tab switches.
- **RTL Punctuation**: English punctuation stays correct inside right-to-left layouts.
- **Shadow Boxes**: Shadows are applied before clipping, so they no longer draw as dark rectangles.
- **Home Polish**: Restored the pattern at the top, slimmed the next-prayer card, swapped the notifications and after-prayer pills, and reduced the notification shortcut to its icon.
- **Onboarding**: Removed the duplicate skip-setup link above the next button.
- **Build & Tests**: Enabled R8 and resource shrinking for release, and added coverage for prayer times, high latitudes, Hijri formatting, search, occasions, and markdown release notes.

---

### 🧪 Public Beta Notes & Community Feedback

As "Athar - أَثَر" is still in public beta phase, your feedback, bug reports, and feature suggestions directly shape future updates.  
Feel free to open an issue or share ideas through the GitHub repository. More features and enhancements are coming!
