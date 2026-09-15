package com.athar.app.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════
// ATHAR — Ultra-Dark Sage Green Palette
// Deep, moody, pitch-dark aesthetic with dark sage green accents
// ══════════════════════════════════════════════════════════════

// ─── Core Backgrounds (Pitch Dark) ───
val AtharBackground = Color(0xFF030705)       // Absolute pitch black with deep sage undertone
val AtharSurface = Color(0xFF060D09)          // Secondary surface
val AtharCardSurface = Color(0xFF09140E)      // Card fill — very deep muted dark sage
val AtharCardBorder = Color(0xFF112017)       // Hairline subtle dark sage border
val AtharCardGlow = Color(0xFF0D1D14)         // Subtle card highlight

// ─── Primary Accent — Dark Sage Green ───
val AtharPrimary = Color(0xFF4A6E59)          // Muted dark sage green
val AtharPrimaryMuted = Color(0xFF314D3E)     // Deep forest sage
val AtharPrimaryLight = Color(0xFF6B937C)     // Subtle sage highlight
val AtharPrimarySubtle = Color(0xFF12241A)    // Very low-opacity sage fill

// ─── Text Hierarchy ───
val AtharTextPrimary = Color(0xFFDDE6E0)      // Soft sage-tinted white
val AtharTextSecondary = Color(0xFF657E70)    // Muted dark sage-grey
val AtharTextMuted = Color(0xFF374D41)        // Very low-emphasis captions
val AtharTextOnPrimary = Color(0xFF030705)    // Dark text over sage buttons

// ─── Navigation Bar ───
val AtharNavbarBg = Color(0xFF050C08)         // Translucent dark dock background
val AtharNavbarBorder = Color(0xFF101F16)     // Dock hairline border
val AtharNavPillSelected = Color(0xFF142B1E)  // Selected tab dark sage pill background
val AtharNavIconActive = Color(0xFFDDE6E0)    // Active tab icon / text
val AtharNavIconInactive = Color(0xFF455E50)  // Inactive tab icon / text

// ─── Featured Service Card Accents ───
val AtharEmerald = Color(0xFF417858)          // Quran — dark sage emerald
val AtharAmber = Color(0xFFA1854B)            // Books — muted amber
val AtharTerracotta = Color(0xFF935A40)       // Agenda — terracotta
val AtharTeal = Color(0xFF35756A)             // Calendar — muted dark teal
val AtharLavender = Color(0xFF6A5D82)         // Tasbih — dusky lavender

// ─── Dividers & Borders ───
val AtharOutline = Color(0xFF101F16)
val AtharOutlineVariant = Color(0xFF0A160F)

// ─── Gradients for Featured Cards ───
val AtharGradientStart = Color(0xFF102217)
val AtharGradientEnd = Color(0xFF09140E)

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