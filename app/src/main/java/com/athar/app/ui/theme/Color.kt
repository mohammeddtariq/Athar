package com.athar.app.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════
// ATHAR — Keyboard-Matched Deep Olive Palette
// Sampled from the user's keyboard screenshot: near-black olive
// surfaces (#141712 family) + pale pistachio accent (#D9E4CB family).
// Dark-first, AMOLED-friendly, olive forest character.
// ══════════════════════════════════════════════════════════════

// ─── Core Backgrounds (AMOLED olive-black) ───
val AtharBackground = Color(0xFF0A0C08)       // App base — olive-tinted black
val AtharSurface = Color(0xFF10130D)          // Secondary surface
val AtharCardSurface = Color(0xFF151A11)      // Card fill — keyboard key tone
val AtharCardBorder = Color(0xFF1E281B)       // Hairline olive border
val AtharCardGlow = Color(0xFF1A2214)         // Subtle card highlight

// ─── Primary Accent — Pale Pistachio / Olive Forest ───
// AtharPrimary is the light accent (keyboard "done" key family),
// readable on dark surfaces; dark text sits on top of it.
val AtharPrimary = Color(0xFFC9D8B4)          // Pale pistachio accent
val AtharPrimaryMuted = Color(0xFF7C9070)     // Olive forest green
val AtharPrimaryLight = Color(0xFFE6EEDA)     // Near-white sage highlight
val AtharPrimarySubtle = Color(0xFF1A2115)    // Low-opacity olive fill
val AtharPrimaryDeep = Color(0xFF2A3325)      // Deep olive for gradients

// ─── Text Hierarchy ───
val AtharTextPrimary = Color(0xFFEBEFE3)      // Soft sage-tinted white
val AtharTextSecondary = Color(0xFF93A086)    // Muted olive-grey
val AtharTextMuted = Color(0xFF4A5A43)        // Very low-emphasis captions
val AtharTextOnPrimary = Color(0xFF141A10)    // Dark text over pale accent

// ─── Navigation Bar (glassmorphism dock) ───
val AtharNavbarBg = Color(0xFF0D110B)         // Dock background (alpha applied in UI)
val AtharNavbarBorder = Color(0xFF233020)     // Dock hairline border
val AtharNavPillSelected = Color(0xFF1C2615)  // Selected tab olive pill
val AtharNavIconActive = Color(0xFFEBEFE3)    // Active tab icon / text
val AtharNavIconInactive = Color(0xFF6B7D60)  // Inactive tab icon / text

// ─── Featured Service Card Accents ───
val AtharEmerald = Color(0xFF417858)          // Quran — dark sage emerald
val AtharAmber = Color(0xFFA1854B)            // Books — muted amber
val AtharTerracotta = Color(0xFF935A40)       // Agenda — terracotta
val AtharTeal = Color(0xFF35756A)             // Calendar — muted dark teal
val AtharLavender = Color(0xFF6A5D82)         // Tasbih — dusky lavender

// ─── Dividers & Borders ───
val AtharOutline = Color(0xFF1E281B)
val AtharOutlineVariant = Color(0xFF12170E)

// ─── Gradients for Featured Cards ───
val AtharGradientStart = Color(0xFF1B2413)
val AtharGradientEnd = Color(0xFF10130D)

// ─── Legacy aliases for backward compatibility ───
@Deprecated("Use AtharBackground", replaceWith = ReplaceWith("AtharBackground"))
val AtharBlack = AtharBackground
@Deprecated("Use AtharSurface", replaceWith = ReplaceWith("AtharSurface"))
val AtharDarkGray = AtharSurface
@Deprecated("Use AtharCardSurface", replaceWith = ReplaceWith("AtharCardSurface"))
val AtharCardGray = AtharCardSurface
@Deprecated("Use AtharPrimary", replaceWith = ReplaceWith("AtharPrimary"))
val AtharGold = AtharPrimary
@Deprecated("Use AtharPrimaryMuted", replaceWith = ReplaceWith("AtharPrimaryMuted"))
val AtharMutedGold = AtharPrimaryMuted
@Deprecated("Use AtharTextPrimary", replaceWith = ReplaceWith("AtharTextPrimary"))
val AtharWhite = AtharTextPrimary
@Deprecated("Use AtharEmerald", replaceWith = ReplaceWith("AtharEmerald"))
val AtharGreen = AtharEmerald
@Deprecated("Use AtharTeal", replaceWith = ReplaceWith("AtharTeal"))
val AtharBlue = AtharTeal
@Deprecated("Use AtharTerracotta", replaceWith = ReplaceWith("AtharTerracotta"))
val AtharRed = AtharTerracotta
@Deprecated("Use AtharLavender", replaceWith = ReplaceWith("AtharLavender"))
val AtharPurple = AtharLavender