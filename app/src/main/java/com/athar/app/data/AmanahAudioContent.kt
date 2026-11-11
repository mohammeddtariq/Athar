package com.athar.app.data

/**
 * The live Quran radio stations Al-Amanah can tune into.
 *
 * For now only the two official Quran Kareem broadcasts are carried: Cairo
 * and Makkah. Both are direct MP3 streams, so the existing MediaPlayer engine
 * can play them without a second player.
 */
data class AmanahRadioStation(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val countryEn: String,
    val frequency: String?,
    val url: String
)

/**
 * A tawasheeh (voices of praise) recording. The starting playlist is the
 * Egyptian munshid Sheikh Sayed Al-Naqshabandy, streamed from the Internet
 * Archive so the app does not have to ship large audio files.
 */
data class AmanahTawasheehTrack(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val url: String
)

val AmanahRadioStations: List<AmanahRadioStation> = listOf(
    AmanahRadioStation(
        id = "quran_cairo",
        nameAr = "إذاعة القرآن الكريم من القاهرة",
        nameEn = "Quran Kareem Radio — Cairo",
        countryAr = "مصر",
        countryEn = "Egypt",
        frequency = "98.2 FM",
        url = "https://stream.radiojar.com/8s5u5tpdtwzuv"
    ),
    AmanahRadioStation(
        id = "quran_makkah",
        nameAr = "إذاعة القرآن الكريم من مكة",
        nameEn = "Quran Radio — Makkah",
        countryAr = "السعودية",
        countryEn = "Saudi Arabia",
        frequency = null,
        url = "https://stream.radiojar.com/0tpy1h0kxtzuv"
    )
)

private const val NAQSHABANDY_ARCHIVE = "https://archive.org/download/54696850/"

private fun archiveTrack(file: String): String = NAQSHABANDY_ARCHIVE + file

val AmanahTawasheehTracks: List<AmanahTawasheehTrack> = listOf(
    AmanahTawasheehTrack(
        id = "naqshabandy_tawasheeh",
        titleAr = "تواشيح النقشبندي",
        titleEn = "Tawasheeh — Al-Naqshabandy",
        url = archiveTrack("%D8%AA%D9%88%D8%A7%D8%B4%D9%8A%D8%AD%20-%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%89-150300662.mp3")
    ),
    AmanahTawasheehTrack(
        id = "ajmal_tawasheeh",
        titleAr = "أجمل تواشيح النقشبندي",
        titleEn = "The Most Beautiful Tawasheeh",
        url = archiveTrack("%D8%A3%D8%AC%D9%85%D9%84%20%D8%AA%D9%88%D8%A7%D8%B4%D9%8A%D8%AD%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-177790608.mp3")
    ),
    AmanahTawasheehTrack(
        id = "asma_allah",
        titleAr = "أسماء الله الحسنى",
        titleEn = "The Most Beautiful Names of Allah",
        url = archiveTrack("%D8%A3%D8%B3%D9%85%D8%A7%D8%A1%20%D8%A7%D9%84%D9%84%D9%87%20%D8%A7%D9%84%D8%AD%D8%B3%D9%86%D9%89%20-%20%D8%A7%D9%84%D8%B4%D9%8A%D8%AE%20%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-55752784.mp3")
    ),
    AmanahTawasheehTrack(
        id = "agheeb",
        titleAr = "أغيب وذو اللطائف لا يغيب",
        titleEn = "I Depart, Yet the Subtle One Never Departs",
        url = archiveTrack("%D8%A3%D8%BA%D9%8A%D8%A8%20%D9%88%D8%B0%D9%88%20%D8%A7%D9%84%D9%84%D8%B7%D8%A7%D8%A6%D9%81%20%D9%84%D8%A7%20%D9%8A%D8%BA%D9%8A%D8%A8%20-%20%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-86208336.mp3")
    ),
    AmanahTawasheehTrack(
        id = "aqool_ummati",
        titleAr = "أقولُ أُمَّتِى",
        titleEn = "I Say, My Ummah",
        url = archiveTrack("%D8%A3%D9%82%D9%88%D9%84%D9%8F%20%D8%A3%D9%8F%D9%85%D9%91%D9%8E%D8%AA%D9%90%D9%89%20-%20%D8%A7%D9%84%D8%B4%D9%8A%D8%AE%20%D8%B3%D9%8A%D9%91%D9%90%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%89-133412180.mp3")
    ),
    AmanahTawasheehTrack(
        id = "anta_alhima",
        titleAr = "أنتَ أنتَ الحِمَى وأنتَ الأمَانُ",
        titleEn = "You Are the Refuge, You Are the Safety",
        url = archiveTrack("%D8%A3%D9%86%D8%AA%D9%8E%20%D8%A3%D9%86%D8%AA%D9%8E%20%D8%A7%D9%84%D8%AD%D9%90%D9%85%D9%8E%D9%89%20%D9%88%D8%A3%D9%86%D8%AA%D9%8E%20%D8%A7%D9%84%D8%A3%D9%85%D9%8E%D8%A7%D9%86%D9%8F%20%20-%20%D8%A7%D9%84%D8%B4%D9%8A%D8%AE%20%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-103912025.mp3")
    ),
    AmanahTawasheehTrack(
        id = "tabattaltu",
        titleAr = "تبتلت مشتاقاً",
        titleEn = "I Devoted Myself, Longing",
        url = archiveTrack("%D8%AA%D8%A8%D8%AA%D9%84%D8%AA%20%D9%85%D8%B4%D8%AA%D8%A7%D9%82%D8%A7%D9%8B......%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%89-19511445.mp3")
    ),
    AmanahTawasheehTrack(
        id = "mawlay",
        titleAr = "تواشيح مولاي",
        titleEn = "Tawasheeh: My Lord",
        url = archiveTrack("%D8%AA%D9%88%D8%A7%D8%B4%D9%8A%D8%AD%20%D9%85%D9%88%D9%84%D8%A7%D9%89%20%20%D9%84%D9%84%D8%B4%D9%8A%D8%AE%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%89-20722614.mp3")
    ),
    AmanahTawasheehTrack(
        id = "jalla_shan",
        titleAr = "جل شأن الإله",
        titleEn = "Exalted Is the Divine",
        url = archiveTrack("%D8%AC%D9%84%20%D8%B4%D8%A3%D9%86%20%D8%A7%D9%84%D8%A5%D9%84%D9%87....%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%89-19513199.mp3")
    ),
    AmanahTawasheehTrack(
        id = "nafs_tashko",
        titleAr = "النفس تشكو",
        titleEn = "The Soul Complains",
        url = archiveTrack("%D8%A7%D9%84%D9%86%D9%81%D8%B3%20%D8%AA%D8%B4%D9%83%D9%88%20-%20%D8%A7%D9%84%D8%B4%D9%8A%D8%AE%20%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-41350079.mp3")
    ),
    AmanahTawasheehTrack(
        id = "aheem",
        titleAr = "أهيم على طريق الشوق وجداً",
        titleEn = "Lost on the Path of Longing",
        url = archiveTrack("%D8%A3%D9%87%D9%8A%D9%85%20%D8%B9%D9%84%D9%89%20%D8%B7%D8%B1%D9%8A%D9%82%20%D8%A7%D9%84%D8%B4%D9%88%D9%82%20%D9%88%D8%AC%D8%AF%D8%A7%20-%D8%B3%D9%8A%D8%AF%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-144309454.mp3")
    ),
    AmanahTawasheehTrack(
        id = "ilahi",
        titleAr = "إلهي — إبتهالات النقشبندي",
        titleEn = "My God — Supplications",
        url = archiveTrack("%D8%A5%D9%84%D9%87%D9%8A%20_%20%D8%A5%D8%A8%D8%AA%D9%87%D8%A7%D9%84%D8%A7%D8%AA%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A-54127934.mp3")
    ),
    AmanahTawasheehTrack(
        id = "ibtihalat_mawlay",
        titleAr = "ابتهالات النقشبندي — مولاي",
        titleEn = "Supplications: My Lord",
        url = archiveTrack("%D8%A7%D8%A8%D8%AA%D9%87%D8%A7%D9%84%D8%A7%D8%AA%20%D8%A7%D9%84%D9%86%D9%82%D8%B4%D8%A8%D9%86%D8%AF%D9%8A%20-%20%D9%85%D9%88%D9%84%D8%A7%D9%89-125138350.mp3")
    )
)
