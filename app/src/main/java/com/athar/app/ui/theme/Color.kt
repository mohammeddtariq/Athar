package com.athar.app.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════
// ATHAR — Dark Green-Grey Palette
// "Forest at twilight" — sophisticated, premium, Islamic.
// ══════════════════════════════════════════════════════════════

// ─── Core Backgrounds ───
val AtharBackground = Color(0xFF0D1512)       // Very dark green-grey (main bg)
val AtharSurface = Color(0xFF15201C)          // Slightly lighter (elevated surfaces)
val AtharCardSurface = Color(0xFF1A2723)      // Card backgrounds
val AtharNavbarGlass = Color(0xFF1A2723)      // Navbar frosted glass base

// ─── Primary Accent — Sage/Mint ───
val AtharPrimary = Color(0xFFA8C5B8)          // Muted sage — main accent
val AtharPrimaryDark = Color(0xFF6B9080)      // Deeper sage — secondary emphasis
val AtharPrimaryLight = Color(0xFFC9DFD2)     // Light sage — active/highlight states

// ─── Text ───
val AtharTextPrimary = Color(0xFFE8EDE9)      // Warm white — main text
val AtharTextSecondary = Color(0xFF8A9B91)    // Muted green-grey — secondary text
val AtharTextOnPrimary = Color(0xFF0D1512)    // Dark text on primary accent

// ─── Navbar ───
val AtharNavPillSelected = Color(0xFF263B33)  // Selected item pill bg
val AtharNavIconInactive = Color(0xFF6B7B72)  // Unselected nav icon color

// ─── Service Card Accents ───
val AtharEmerald = Color(0xFF5DA67D)          // Refined emerald green
val AtharTeal = Color(0xFF4A9B8E)             // Calm teal
val AtharTerracotta = Color(0xFFC17A5E)       // Warm terracotta
val AtharLavender = Color(0xFF8B7DA6)         // Dusty lavender
val AtharAmber = Color(0xFFCDA96D)            // Warm amber (subtle gold nod)

// ─── Borders & Dividers ───
val AtharOutline = Color(0xFF243530)          // Subtle green-tinted border
val AtharOutlineVariant = Color(0xFF1C2B26)   // Even subtler border

// ─── Legacy aliases (for backward compat during migration) ───
@Deprecated("Use AtharBackground", replaceWith = ReplaceWith("AtharBackground"))
val AtharBlack = AtharBackground
@Deprecated("Use AtharSurface", replaceWith = ReplaceWith("AtharSurface"))
val AtharDarkGray = AtharSurface
@Deprecated("Use AtharCardSurface", replaceWith = ReplaceWith("AtharCardSurface"))
val AtharCardGray = AtharCardSurface
@Deprecated("Use AtharPrimary", replaceWith = ReplaceWith("AtharPrimary"))
val AtharGold = AtharPrimary
@Deprecated("Use AtharPrimaryDark", replaceWith = ReplaceWith("AtharPrimaryDark"))
val AtharMutedGold = AtharPrimaryDark
@Deprecated("Use AtharTextPrimary", replaceWith = ReplaceWith("AtharTextPrimary"))
val AtharWhite = AtharTextPrimary

// Legacy service colors
@Deprecated("Use AtharEmerald", replaceWith = ReplaceWith("AtharEmerald"))
val AtharGreen = AtharEmerald
@Deprecated("Use AtharTeal", replaceWith = ReplaceWith("AtharTeal"))
val AtharBlue = AtharTeal
@Deprecated("Use AtharTerracotta", replaceWith = ReplaceWith("AtharTerracotta"))
val AtharRed = AtharTerracotta
@Deprecated("Use AtharLavender", replaceWith = ReplaceWith("AtharLavender"))
val AtharPurple = AtharLavender