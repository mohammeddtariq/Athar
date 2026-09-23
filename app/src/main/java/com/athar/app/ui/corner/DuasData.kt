package com.athar.app.ui.corner

/**
 * Complete, verified supplications and remembrances from the authentic Sunnah
 * (Hisnul Muslim / حصن المسلم) categorized and ordered
 * exact collections:
 * - After Prayer (Fajr: 15, Maghrib: 14, Other Prayers: 12)
 * - Morning Adhkar (All 31 in sequence)
 * - Evening Adhkar (All 30 in sequence)
 * - Sleep & Waking, Mosque & Adhan, Distress & Guidance, Comprehensive Duas
 */
data class Dua(
    val id: String,
    val arabic: String,
    val translation: String,
    val source: String,
    val repeat: Int = 1,
    val noteAr: String? = null,
    val noteEn: String? = null
)

data class DuaCategory(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val duas: List<Dua>
)

// ─────────────────────────────────────────────────────────────────────────────
// AFTER PRAYER SUB-COLLECTIONS (Fajr, Maghrib, Other Prayers)
// ─────────────────────────────────────────────────────────────────────────────

val afterPrayerOtherDuas: List<Dua> = listOf(
    Dua(
        id = "after_other_1",
        arabic = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
        translation = "I ask Allah for forgiveness (three times). O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of Glory and Honor.",
        source = "صحيح مسلم / Muslim",
        repeat = 3,
        noteAr = "يُستغفر ثلاثاً بعد السلام من الصلاة المكتوبة مباشرة ثم يُقال هذا الذكر",
        noteEn = "Seek forgiveness three times directly after completing the obligatory prayer, then recite this supplication."
    ),
    Dua(
        id = "after_other_2",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
        translation = "None has the right to be worshipped but Allah alone, without partner; to Him belongs the dominion and praise... O Allah, none can prevent what You give, nor can anyone give what You prevent.",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 1,
        noteAr = "يُقال دبر كل صلاة مكتوبة",
        noteEn = "Recited after each obligatory prayer."
    ),
    Dua(
        id = "after_other_3",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ. لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ، لَا إِلَهَ إِلَّا اللَّهُ، وَلَا نَعْبُدُ إِلَّا إِيَّاهُ، لَهُ النِّعْمَةُ وَلَهُ الْفَضْلُ وَلَهُ الثَّنَاءُ الْحَسَنُ، لَا إِلَهَ إِلَّا اللَّهُ مُخْلِصِينَ لَهُ الدِّينَ وَلَوْ كَرِهَ الْكَافِرُونَ",
        translation = "None has the right to be worshipped but Allah alone... There is no power and no strength except with Allah. None has the right to be worshipped but Allah, and we worship none but Him.",
        source = "صحيح مسلم / Muslim",
        repeat = 1,
        noteAr = "كان النبي صلى الله عليه وسلم يهلل بهن دبر كل صلاة",
        noteEn = "The Prophet (pbuh) used to recite this tahlil after each prayer."
    ),
    Dua(
        id = "after_other_4",
        arabic = "سُبْحَانَ اللَّهِ",
        translation = "Glory be to Allah (33 times).",
        source = "صحيح مسلم / Muslim",
        repeat = 33,
        noteAr = "تُسبح ثلاثاً وثلاثين بعد الصلاة المكتوبة",
        noteEn = "Recite SubhanAllah 33 times after each obligatory prayer."
    ),
    Dua(
        id = "after_other_5",
        arabic = "الْحَمْدُ لِلَّهِ",
        translation = "Praise be to Allah (33 times).",
        source = "صحيح مسلم / Muslim",
        repeat = 33,
        noteAr = "تحمد ثلاثاً وثلاثين بعد الصلاة المكتوبة",
        noteEn = "Recite Alhamdulillah 33 times after each obligatory prayer."
    ),
    Dua(
        id = "after_other_6",
        arabic = "اللَّهُ أَكْبَرُ",
        translation = "Allah is the Greatest (33 times).",
        source = "صحيح مسلم / Muslim",
        repeat = 33,
        noteAr = "تكبر ثلاثاً وثلاثين بعد الصلاة المكتوبة",
        noteEn = "Recite Allahu Akbar 33 times after each obligatory prayer."
    ),
    Dua(
        id = "after_other_7",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        translation = "None has the right to be worshipped but Allah alone, having no partner; to Him belongs the dominion and praise, and He is over all things omnipotent.",
        source = "صحيح مسلم / Muslim",
        repeat = 1,
        noteAr = "تمام المائة: من قالها دبر كل صلاة غُفرت خطاياه وإن كانت مثل زبد البحر",
        noteEn = "Completing the 100th: sins will be forgiven even if like the foam of the sea."
    ),
    Dua(
        id = "after_other_8",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        translation = "Surah Al-Ikhlas (Quran 112).",
        source = "أبو داود والترمذي والنسائي / Abu Dawud & Tirmidhi",
        repeat = 1,
        noteAr = "تقرأ مرة واحدة بعد الصلوات الأخرى (الظهر، العصر، العشاء)",
        noteEn = "Recited once after Dhuhr, Asr, and Isha prayers."
    ),
    Dua(
        id = "after_other_9",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ مِن شَرِّ مَا خَلَقَ ۞ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۞ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۞ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        translation = "Surah Al-Falaq (Quran 113).",
        source = "أبو داود والترمذي والنسائي / Abu Dawud & Tirmidhi",
        repeat = 1,
        noteAr = "تقرأ مرة واحدة بعد الصلوات الأخرى (الظهر، العصر، العشاء)",
        noteEn = "Recited once after Dhuhr, Asr, and Isha prayers."
    ),
    Dua(
        id = "after_other_10",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۞ مَلِكِ النَّاسِ ۞ إِلَهِ النَّاسِ ۞ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۞ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۞ مِنَ الْجِنَّةِ وَالنَّاسِ",
        translation = "Surah An-Nas (Quran 114).",
        source = "أبو داود والترمذي والنسائي / Abu Dawud & Tirmidhi",
        repeat = 1,
        noteAr = "تقرأ مرة واحدة بعد الصلوات الأخرى (الظهر، العصر، العشاء)",
        noteEn = "Recited once after Dhuhr, Asr, and Isha prayers."
    ),
    Dua(
        id = "after_other_11",
        arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        translation = "Ayat al-Kursi (Quran 2:255).",
        source = "سنن النسائي / An-Nasa'i",
        repeat = 1,
        noteAr = "من قرأ آية الكرسي دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا أن يموت",
        noteEn = "Whoever recites Ayat al-Kursi after every obligatory prayer, nothing stands between him and Paradise except death."
    ),
    Dua(
        id = "after_other_12",
        arabic = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
        translation = "O Allah, help me to remember You, give thanks to You, and worship You with excellence.",
        source = "أبو داود والنسائي / Abu Dawud & An-Nasa'i",
        repeat = 1,
        noteAr = "أوصى به النبي ﷺ معاذاً ألا يدعه في دبر كل صلاة",
        noteEn = "The Prophet (pbuh) advised Mu'adh never to omit this supplication after each prayer."
    )
)

val afterPrayerFajrDuas: List<Dua> = listOf(
    afterPrayerOtherDuas[0].copy(id = "after_fajr_1"),
    afterPrayerOtherDuas[1].copy(id = "after_fajr_2"),
    afterPrayerOtherDuas[2].copy(id = "after_fajr_3"),
    afterPrayerOtherDuas[3].copy(id = "after_fajr_4"),
    afterPrayerOtherDuas[4].copy(id = "after_fajr_5"),
    afterPrayerOtherDuas[5].copy(id = "after_fajr_6"),
    afterPrayerOtherDuas[6].copy(id = "after_fajr_7"),
    afterPrayerOtherDuas[7].copy(
        id = "after_fajr_8",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[8].copy(
        id = "after_fajr_9",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[9].copy(
        id = "after_fajr_10",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[10].copy(id = "after_fajr_11"),
    Dua(
        id = "after_fajr_12",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        translation = "None has the right to be worshipped but Allah alone, without partner; to Him belongs dominion and praise, He gives life and causes death, and He is over all things omnipotent (10 times).",
        source = "الترمذي وأحمد / At-Tirmidhi & Ahmad",
        repeat = 10,
        noteAr = "تقال عشر مرات بعد صلاتي الصبح (الفجر) والمغرب قبل أن يثني رجليه",
        noteEn = "Recited 10 times after Fajr and Maghrib prayers before changing sitting posture."
    ),
    Dua(
        id = "after_fajr_13",
        arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
        translation = "O Allah, I ask You for beneficial knowledge, wholesome sustenance, and accepted deeds.",
        source = "سنن ابن ماجه / Ibn Majah",
        repeat = 1,
        noteAr = "كان النبي ﷺ يقولها إذا صلى الصبح حين يسلم",
        noteEn = "The Prophet (pbuh) used to say this upon completing the Fajr prayer."
    ),
    Dua(
        id = "after_fajr_14",
        arabic = "اللَّهُمَّ أَجِرْنِي مِنَ النَّارِ",
        translation = "O Allah, protect me from the Fire (seven times).",
        source = "سنن أبي داود وأحمد / Abu Dawud & Ahmad",
        repeat = 7,
        noteAr = "تقال سبع مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited 7 times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[11].copy(id = "after_fajr_15")
)

val afterPrayerMaghribDuas: List<Dua> = listOf(
    afterPrayerOtherDuas[0].copy(id = "after_maghrib_1"),
    afterPrayerOtherDuas[1].copy(id = "after_maghrib_2"),
    afterPrayerOtherDuas[2].copy(id = "after_maghrib_3"),
    afterPrayerOtherDuas[3].copy(id = "after_maghrib_4"),
    afterPrayerOtherDuas[4].copy(id = "after_maghrib_5"),
    afterPrayerOtherDuas[5].copy(id = "after_maghrib_6"),
    afterPrayerOtherDuas[6].copy(id = "after_maghrib_7"),
    afterPrayerOtherDuas[7].copy(
        id = "after_maghrib_8",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[8].copy(
        id = "after_maghrib_9",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[9].copy(
        id = "after_maghrib_10",
        repeat = 3,
        noteAr = "تقرأ ثلاث مرات بعد صلاتي الفجر والمغرب",
        noteEn = "Recited three times after Fajr and Maghrib prayers."
    ),
    afterPrayerOtherDuas[10].copy(id = "after_maghrib_11"),
    Dua(
        id = "after_maghrib_12",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        translation = "None has the right to be worshipped but Allah alone, without partner; to Him belongs dominion and praise, He gives life and causes death, and He is over all things omnipotent (10 times).",
        source = "الترمذي وأحمد / At-Tirmidhi & Ahmad",
        repeat = 10,
        noteAr = "تقال عشر مرات بعد صلاتي المغرب والصبح قبل أن يثني رجليه",
        noteEn = "Recited 10 times after Maghrib and Fajr prayers before changing sitting posture."
    ),
    Dua(
        id = "after_maghrib_13",
        arabic = "اللَّهُمَّ أَجِرْنِي مِنَ النَّارِ",
        translation = "O Allah, protect me from the Fire (seven times).",
        source = "سنن أبي داود وأحمد / Abu Dawud & Ahmad",
        repeat = 7,
        noteAr = "تقال سبع مرات بعد صلاتي المغرب والفجر",
        noteEn = "Recited 7 times after Maghrib and Fajr prayers."
    ),
    afterPrayerOtherDuas[11].copy(id = "after_maghrib_14")
)

// ─────────────────────────────────────────────────────────────────────────────
// MORNING ADHKAR (Authentic Sunnah & Hisnul Muslim)
// ─────────────────────────────────────────────────────────────────────────────

val morningAdhkarList: List<Dua> = listOf(
    // 1/31
    Dua(
        id = "morning_1",
        arabic = "أَعُوذُ بِاللهِ مِنْ الشَّيْطَانِ الرَّجِيمِ\nاللهُ لاَ إِلَـهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ مَن ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلاَّ بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلاَ يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلاَّ بِمَا شَاء وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالأَرْضَ وَلاَ يَؤُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        translation = "Ayat al-Kursi (Quran 2:255). Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence...",
        source = "آية الكرسي - البقرة 255 / Quran 2:255",
        repeat = 1
    ),
    // 2/31
    Dua(
        id = "morning_2",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        translation = "Surah Al-Ikhlas (Quran 112). Say, 'He is Allah, [who is] One, Allah, the Eternal Refuge. He neither begets nor is born, Nor is there to Him any equivalent.'",
        source = "سورة الإخلاص / Quran 112",
        repeat = 3
    ),
    // 3/31
    Dua(
        id = "morning_3",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ مِن شَرِّ مَا خَلَقَ ۞ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۞ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۞ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        translation = "Surah Al-Falaq (Quran 113). Say, 'I seek refuge in the Lord of daybreak From the evil of that which He created...'",
        source = "سورة الفلق / Quran 113",
        repeat = 3
    ),
    // 4/31
    Dua(
        id = "morning_4",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۞ مَلِكِ النَّاسِ ۞ إِلَهِ النَّاسِ ۞ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۞ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۞ مِنَ الْجِنَّةِ وَالنَّاسِ",
        translation = "Surah An-Nas (Quran 114). Say, 'I seek refuge in the Lord of mankind, The Sovereign of mankind, The God of mankind...'",
        source = "سورة الناس / Quran 114",
        repeat = 3
    ),
    // 5/31
    Dua(
        id = "morning_5",
        arabic = "أَصْبَحْنَا وَأَصْبَحَ المُلْكُ لِلَّهِ وَالحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا اليَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا اليَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الكَسَلِ وَسُوءِ الكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي القَبْرِ.",
        translation = "We have entered the morning and all dominion belongs to Allah; praise is to Allah. There is no deity except Allah alone, without partner; to Him belongs the dominion and to Him belongs praise, and He is over all things omnipotent...",
        source = "صحيح مسلم / Muslim",
        repeat = 1
    ),
    // 6/31
    Dua(
        id = "morning_6",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        translation = "Sayyid al-Istighfar: O Allah, You are my Lord, there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can...",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    // 7/31
    Dua(
        id = "morning_7",
        arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        translation = "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad (pbuh) as my Prophet.",
        source = "أبو داود والترمذي / Abu Dawud & At-Tirmidhi",
        repeat = 3
    ),
    // 8/31
    Dua(
        id = "morning_8",
        arabic = "اللَّهُمَّ إِنِّي أَصْبَحْتُ أُشْهِدُكَ، وَأُشْهِدُ حَمَلَةَ عَرْشِكَ، وَمَلَائِكَتَكَ، وَجَمِيعَ خَلْقِكَ، أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ، وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
        translation = "O Allah, I have entered the morning calling You to witness, and calling the bearers of Your Throne, Your angels, and all Your creation to witness that You are Allah...",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 4
    ),
    // 9/31
    Dua(
        id = "morning_9",
        arabic = "اللَّهُمَّ مَا أَصْبَحَ بِي مِنْ نِعْمَةٍ أَوْ بِأَحَدٍ مِنْ خَلْقِكَ فَمِنْكَ وَحْدَكَ لَا شَرِيكَ لَكَ، فَلَكَ الْحَمْدُ وَلَكَ الشُّكْرُ",
        translation = "O Allah, whatever blessing has come to me or any of Your creation this morning is from You alone, without partner; to You belongs all praise and gratitude.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 10/31
    Dua(
        id = "morning_10",
        arabic = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
        translation = "Allah suffices me; there is no deity except Him. In Him I trust, and He is the Lord of the Mighty Throne (seven times).",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 7
    ),
    // 11/31
    Dua(
        id = "morning_11",
        arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        translation = "In the Name of Allah, with Whose Name nothing can cause harm on earth or in heaven, and He is the All-Hearing, the All-Knowing.",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 3
    ),
    // 12/31
    Dua(
        id = "morning_12",
        arabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
        translation = "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
        source = "سنن الترمذي / At-Tirmidhi",
        repeat = 1
    ),
    // 13/31
    Dua(
        id = "morning_13",
        arabic = "أَصْبَحْنَا عَلَى فِطْرَةِ الْإِسْلَامِ، وَعَلَى كَلِمَةِ الْإِخْلَاصِ، وَعَلَى دِينِ نَبِيِّنَا مُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ، وَعَلَى مِلَّةِ أَبِينَا إِبْرَاهِيمَ حَنِيفًا مُسْلِمًا وَمَا كَانَ مِنَ الْمُشْرِكِينَ",
        translation = "We enter the morning upon the natural creed of Islam, the word of sincere faith, the religion of our Prophet Muhammad, and the path of our father Ibrahim.",
        source = "مسند أحمد / Ahmad",
        repeat = 1
    ),
    // 14/31
    Dua(
        id = "morning_14",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
        translation = "Glory be to Allah and praise Him, according to the number of His creation, His good pleasure, the weight of His Throne, and the ink of His words.",
        source = "صحيح مسلم / Muslim",
        repeat = 3
    ),
    // 15/31
    Dua(
        id = "morning_15",
        arabic = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ",
        translation = "O Allah, grant health to my body, my hearing, and my sight; there is no deity except You.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 3
    ),
    // 16/31
    Dua(
        id = "morning_16",
        arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ، وَالْفَقْرِ، وَأَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، لَا إِلَهَ إِلَّا أَنْتَ",
        translation = "O Allah, I seek refuge in You from disbelief and poverty, and from punishment in the grave; there is no deity except You.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 3
    ),
    // 17/31
    Dua(
        id = "morning_17",
        arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي، وَآمِنْ رَوْعَاتِي، اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ، وَمِنْ خَلْفِي، وَعَنْ يَمِينِي، وَعَنْ شِمَالِي، وَمِنْ فَوْقِي، وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
        translation = "O Allah, I ask You for forgiveness and well-being in this world and the next; in my religion, worldly affairs, family, and wealth...",
        source = "سنن أبي داود وابن ماجه / Abu Dawud & Ibn Majah",
        repeat = 1
    ),
    // 18/31
    Dua(
        id = "morning_18",
        arabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
        translation = "O Ever-Living, O Sustainer, by Your mercy I seek assistance; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
        source = "الحاكم والنسائي / Al-Hakim & An-Nasa'i",
        repeat = 1
    ),
    // 19/31
    Dua(
        id = "morning_19",
        arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ رَبِّ الْعَالَمِينَ، اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذَا الْيَوْمِ: فَتْحَهُ، وَنَصْرَهُ، وَنُورَهُ، وَبَرَكَتَهُ، وَهُدَاهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِيهِ وَشَرِّ مَا بَعْدَهُ",
        translation = "We have entered the morning and all dominion belongs to Allah, Lord of the worlds. O Allah, I ask You for the good of this day: its triumph, victory, light, blessings, and guidance...",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 20/31
    Dua(
        id = "morning_20",
        arabic = "اللَّهُمَّ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ، فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ، رَبَّ كُلِّ شَيْءٍ وَمَلِيكَهُ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَعُوذُ بِكَ مِنْ شَرِّ نَفْسِي، وَمِنْ شَرِّ الشَّيْطَانِ وَشِرْكِهِ، وَأَنْ أَقْتَرِفَ عَلَى نَفْسِي سُوءًا أَوْ أَجُرَّهُ إِلَى مُسْلِمٍ",
        translation = "O Allah, Knower of the unseen and seen, Originator of the heavens and earth, Lord of all things and Sovereign! I testify that there is no deity except You...",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 1
    ),
    // 21/31
    Dua(
        id = "morning_21",
        arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
        translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
        source = "صحيح مسلم / Muslim",
        repeat = 3
    ),
    // 22/31
    Dua(
        id = "morning_22",
        arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        translation = "O Allah, send prayers, peace, and blessings upon our Prophet Muhammad (10 times).",
        source = "المعجم الكبير للطبراني / At-Tabarani",
        repeat = 10
    ),
    // 23/31
    Dua(
        id = "morning_23",
        arabic = "اللَّهُمَّ إِنَّا نَعُوذُ بِكَ مِنْ أَنْ نُشْرِكَ بِكَ شَيْئًا نَعْلَمُهُ، وَنَسْتَغْفِرُكَ لِمَا لَا نَعْلَمُهُ",
        translation = "O Allah, we seek refuge in You from knowingly associating anything with You, and we seek Your forgiveness for what we do not know.",
        source = "مسند أحمد / Ahmad",
        repeat = 3
    ),
    // 24/31
    Dua(
        id = "morning_24",
        arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
        translation = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, cowardice and miserliness, the burden of debt, and the overpowering of men.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 25/31
    Dua(
        id = "morning_25",
        arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيَّ الْقَيُّومَ وَأَتُوبُ إِلَيْهِ",
        translation = "I seek forgiveness from Allah the Almighty, there is no deity except Him, the Ever-Living, the Sustainer, and I repent to Him.",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 3
    ),
    // 26/31
    Dua(
        id = "morning_26",
        arabic = "يَا رَبِّ لَكَ الْحَمْدُ كَمَا يَنْبَغِي لِجَلَالِ وَجْهِكَ وَلِعَظِيمِ سُلْطَانِكَ",
        translation = "O my Lord, to You belongs praise as befits the majesty of Your Face and the greatness of Your authority.",
        source = "سنن ابن ماجه / Ibn Majah",
        repeat = 1
    ),
    // 27/31
    Dua(
        id = "morning_27",
        arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
        translation = "O Allah, I ask You for beneficial knowledge, wholesome sustenance, and accepted deeds.",
        source = "سنن ابن ماجه / Ibn Majah",
        repeat = 1
    ),
    // 28/31
    Dua(
        id = "morning_28",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، عَلَيْكَ تَوَكَّلْتُ، وَأَنْتَ رَبُّ الْعَرْشِ الْعَظِيمِ، مَا شَاءَ اللَّهُ كَانَ، وَمَا لَمْ يَشَأْ لَمْ يَكُنْ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ، أَعْلَمُ أَنَّ اللَّهَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، وَأَنَّ اللَّهَ قَدْ أَحَاطَ بِكُلِّ شَيْءٍ عِلْمًا، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ شَرِّ نَفْسِي، وَمِنْ شَرِّ كُلِّ دَابَّةٍ أَنْتَ آخِذٌ بِنَاصِيَتِهَا، إِنَّ رَبِّي عَلَى صِرَاطٍ مُسْتَقِيمٍ",
        translation = "O Allah, You are my Lord, there is no deity except You. Upon You I rely, and You are Lord of the Mighty Throne. Whatever Allah wills happens, and whatever He does not will does not happen...",
        source = "أبو داود وابن السني / Abu Dawud & Ibn As-Sunni",
        repeat = 1
    ),
    // 29/31
    Dua(
        id = "morning_29",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        translation = "None has the right to be worshipped but Allah alone, having no partner; to Him belongs dominion and praise, and He is over all things omnipotent (100 times).",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 100
    ),
    // 30/31
    Dua(
        id = "morning_30",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        translation = "Glory be to Allah and His is the praise (100 times). Whoever says this 100 times, his sins will be forgiven even if like the foam of the sea.",
        source = "صحيح مسلم / Muslim",
        repeat = 100
    ),
    // 31/31
    Dua(
        id = "morning_31",
        arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        translation = "I seek forgiveness from Allah and repent to Him (100 times).",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 100
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// EVENING ADHKAR (Authentic Sunnah & Hisnul Muslim)
// ─────────────────────────────────────────────────────────────────────────────

val eveningAdhkarList: List<Dua> = listOf(
    // 1/30
    Dua(
        id = "evening_1",
        arabic = "أَعُوذُ بِاللهِ مِنْ الشَّيْطَانِ الرَّجِيمِ\nاللهُ لاَ إِلَـهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ مَن ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلاَّ بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلاَ يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلاَّ بِمَا شَاء وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالأَرْضَ وَلاَ يَؤُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        translation = "Ayat al-Kursi (Quran 2:255). Whoever recites it in the evening is protected from the jinn until morning.",
        source = "آية الكرسي - البقرة 255 / Quran 2:255",
        repeat = 1
    ),
    // 2/30
    Dua(
        id = "evening_2",
        arabic = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۞ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
        translation = "The last two verses of Surah Al-Baqarah (285-286). Whoever recites them at night, they suffice him against all harm.",
        source = "البقرة ٢٨٥-٢٨٦ / Bukhari & Muslim",
        repeat = 1
    ),
    // 3/30
    Dua(
        id = "evening_3",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        translation = "Surah Al-Ikhlas (Quran 112). Recite three times in the evening.",
        source = "سورة الإخلاص / Quran 112",
        repeat = 3
    ),
    // 4/30
    Dua(
        id = "evening_4",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ مِن شَرِّ مَا خَلَقَ ۞ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۞ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۞ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        translation = "Surah Al-Falaq (Quran 113). Recite three times in the evening.",
        source = "سورة الفلق / Quran 113",
        repeat = 3
    ),
    // 5/30
    Dua(
        id = "evening_5",
        arabic = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيم\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۞ مَلِكِ النَّاسِ ۞ إِلَهِ النَّاسِ ۞ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۞ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۞ مِنَ الْجِنَّةِ وَالنَّاسِ",
        translation = "Surah An-Nas (Quran 114). Recite three times in the evening.",
        source = "سورة الناس / Quran 114",
        repeat = 3
    ),
    // 6/30
    Dua(
        id = "evening_6",
        arabic = "أَمْسَيْنَا وَأَمْسَى المُلْكُ لِلَّهِ وَالحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا، رَبِّ أَعُوذُ بِكَ مِنَ الكَسَلِ وَسُوءِ الكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي القَبْرِ",
        translation = "We have entered the evening and all dominion belongs to Allah; praise is to Allah. There is no deity except Allah alone, without partner; to Him belongs the dominion and to Him belongs praise, and He is over all things omnipotent...",
        source = "صحيح مسلم / Muslim",
        repeat = 1
    ),
    // 7/30
    Dua(
        id = "evening_7",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        translation = "Sayyid al-Istighfar: O Allah, You are my Lord, there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can...",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    // 8/30
    Dua(
        id = "evening_8",
        arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        translation = "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad (pbuh) as my Prophet.",
        source = "أبو داود والترمذي / Abu Dawud & At-Tirmidhi",
        repeat = 3
    ),
    // 9/30
    Dua(
        id = "evening_9",
        arabic = "اللَّهُمَّ إِنِّي أَمْسَيْتُ أُشْهِدُكَ، وَأُشْهِدُ حَمَلَةَ عَرْشِكَ، وَمَلَائِكَتَكَ، وَجَمِيعَ خَلْقِكَ، أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ، وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
        translation = "O Allah, I have entered the evening calling You to witness, and calling the bearers of Your Throne, Your angels, and all Your creation to witness that You are Allah...",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 4
    ),
    // 10/30
    Dua(
        id = "evening_10",
        arabic = "اللَّهُمَّ مَا أَمْسَى بِي مِنْ نِعْمَةٍ أَوْ بِأَحَدٍ مِنْ خَلْقِكَ فَمِنْكَ وَحْدَكَ لَا شَرِيكَ لَكَ، فَلَكَ الْحَمْدُ وَلَكَ الشُّكْرُ",
        translation = "O Allah, whatever blessing has come to me or any of Your creation this evening is from You alone, without partner; to You belongs all praise and gratitude.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 11/30
    Dua(
        id = "evening_11",
        arabic = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
        translation = "Allah suffices me; there is no deity except Him. In Him I trust, and He is the Lord of the Mighty Throne (seven times).",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 7
    ),
    // 12/30
    Dua(
        id = "evening_12",
        arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        translation = "In the Name of Allah, with Whose Name nothing can cause harm on earth or in heaven, and He is the All-Hearing, the All-Knowing.",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 3
    ),
    // 13/30
    Dua(
        id = "evening_13",
        arabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
        translation = "O Allah, by You we enter the evening and by You we enter the morning, by You we live and by You we die, and to You is the final return.",
        source = "سنن الترمذي / At-Tirmidhi",
        repeat = 1
    ),
    // 14/30
    Dua(
        id = "evening_14",
        arabic = "أَمْسَيْنَا عَلَى فِطْرَةِ الْإِسْلَامِ، وَعَلَى كَلِمَةِ الْإِخْلَاصِ، وَعَلَى دِينِ نَبِيِّنَا مُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ، وَعَلَى مِلَّةِ أَبِينَا إِبْرَاهِيمَ حَنِيفًا مُسْلِمًا وَمَا كَانَ مِنَ الْمُشْرِكِينَ",
        translation = "We enter the evening upon the natural creed of Islam, the word of sincere faith, the religion of our Prophet Muhammad, and the path of our father Ibrahim.",
        source = "مسند أحمد / Ahmad",
        repeat = 1
    ),
    // 15/30
    Dua(
        id = "evening_15",
        arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
        translation = "I seek refuge in the perfect words of Allah from the evil of what He has created (three times).",
        source = "صحيح مسلم / Muslim",
        repeat = 3
    ),
    // 16/30
    Dua(
        id = "evening_16",
        arabic = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ",
        translation = "O Allah, grant health to my body, my hearing, and my sight; there is no deity except You.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 3
    ),
    // 17/30
    Dua(
        id = "evening_17",
        arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ، وَالْفَقْرِ، وَأَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، لَا إِلَهَ إِلَّا أَنْتَ",
        translation = "O Allah, I seek refuge in You from disbelief and poverty, and from punishment in the grave; there is no deity except You.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 3
    ),
    // 18/30
    Dua(
        id = "evening_18",
        arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي، وَآمِنْ رَوْعَاتِي، اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ، وَمِنْ خَلْفِي، وَعَنْ يَمِينِي، وَعَنْ شِمَالِي، وَمِنْ فَوْقِي، وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
        translation = "O Allah, I ask You for forgiveness and well-being in this world and the next; in my religion, worldly affairs, family, and wealth...",
        source = "سنن أبي داود وابن ماجه / Abu Dawud & Ibn Majah",
        repeat = 1
    ),
    // 19/30
    Dua(
        id = "evening_19",
        arabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
        translation = "O Ever-Living, O Sustainer, by Your mercy I seek assistance; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
        source = "الحاكم والنسائي / Al-Hakim & An-Nasa'i",
        repeat = 1
    ),
    // 20/30
    Dua(
        id = "evening_20",
        arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ رَبِّ الْعَالَمِينَ، اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذِهِ اللَّيْلَةِ: فَتْحَهَا، وَنَصْرَهَا، وَنُورَهَا، وَبَرَكَتَهَا، وَهُدَاهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِيهَا وَشَرِّ مَا بَعْدَهَا",
        translation = "We have entered the evening and all dominion belongs to Allah, Lord of the worlds. O Allah, I ask You for the good of this night: its triumph, victory, light, blessings, and guidance...",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 21/30
    Dua(
        id = "evening_21",
        arabic = "اللَّهُمَّ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ، فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ، رَبَّ كُلِّ شَيْءٍ وَمَلِيكَهُ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَعُوذُ بِكَ مِنْ شَرِّ نَفْسِي، وَمِنْ شَرِّ الشَّيْطَانِ وَشِرْكِهِ، وَأَنْ أَقْتَرِفَ عَلَى نَفْسِي سُوءًا أَوْ أَجُرَّهُ إِلَى مُسْلِمٍ",
        translation = "O Allah, Knower of the unseen and seen, Originator of the heavens and earth, Lord of all things and Sovereign! I testify that there is no deity except You...",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 1
    ),
    // 22/30
    Dua(
        id = "evening_22",
        arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        translation = "O Allah, send prayers, peace, and blessings upon our Prophet Muhammad (10 times).",
        source = "المعجم الكبير للطبراني / At-Tabarani",
        repeat = 10
    ),
    // 23/30
    Dua(
        id = "evening_23",
        arabic = "اللَّهُمَّ إِنَّا نَعُوذُ بِكَ مِنْ أَنْ نُشْرِكَ بِكَ شَيْئًا نَعْلَمُهُ، وَنَسْتَغْفِرُكَ لِمَا لَا نَعْلَمُهُ",
        translation = "O Allah, we seek refuge in You from knowingly associating anything with You, and we seek Your forgiveness for what we do not know.",
        source = "مسند أحمد / Ahmad",
        repeat = 3
    ),
    // 24/30
    Dua(
        id = "evening_24",
        arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
        translation = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, cowardice and miserliness, the burden of debt, and the overpowering of men.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    // 25/30
    Dua(
        id = "evening_25",
        arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيَّ الْقَيُّومَ وَأَتُوبُ إِلَيْهِ",
        translation = "I seek forgiveness from Allah the Almighty, there is no deity except Him, the Ever-Living, the Sustainer, and I repent to Him.",
        source = "الترمذي وأبو داود / At-Tirmidhi & Abu Dawud",
        repeat = 3
    ),
    // 26/30
    Dua(
        id = "evening_26",
        arabic = "يَا رَبِّ لَكَ الْحَمْدُ كَمَا يَنْبَغِي لِجَلَالِ وَجْهِكَ وَلِعَظِيمِ سُلْطَانِكَ",
        translation = "O my Lord, to You belongs praise as befits the majesty of Your Face and the greatness of Your authority.",
        source = "سنن ابن ماجه / Ibn Majah",
        repeat = 1
    ),
    // 27/30
    Dua(
        id = "evening_27",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، عَلَيْكَ تَوَكَّلْتُ، وَأَنْتَ رَبُّ الْعَرْشِ الْعَظِيمِ، مَا شَاءَ اللَّهُ كَانَ، وَمَا لَمْ يَشَأْ لَمْ يَكُنْ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ، أَعْلَمُ أَنَّ اللَّهَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، وَأَنَّ اللَّهَ قَدْ أَحَاطَ بِكُلِّ شَيْءٍ عِلْمًا، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ شَرِّ نَفْسِي، وَمِنْ شَرِّ كُلِّ دَابَّةٍ أَنْتَ آخِذٌ بِنَاصِيَتِهَا، إِنَّ رَبِّي عَلَى صِرَاطٍ مُسْتَقِيمٍ",
        translation = "O Allah, You are my Lord, there is no deity except You. Upon You I rely, and You are Lord of the Mighty Throne...",
        source = "أبو داود وابن السني / Abu Dawud & Ibn As-Sunni",
        repeat = 1
    ),
    // 28/30
    Dua(
        id = "evening_28",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        translation = "None has the right to be worshipped but Allah alone, having no partner; to Him belongs dominion and praise, and He is over all things omnipotent (100 times).",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 100
    ),
    // 29/30
    Dua(
        id = "evening_29",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        translation = "Glory be to Allah and His is the praise (100 times). Whoever says this 100 times, his sins will be forgiven even if like the foam of the sea.",
        source = "صحيح مسلم / Muslim",
        repeat = 100
    ),
    // 30/30
    Dua(
        id = "evening_30",
        arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        translation = "I seek forgiveness from Allah and repent to Him (100 times).",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 100
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// SLEEP, MOSQUE, DISTRESS & COMPREHENSIVE CATEGORIES
// ─────────────────────────────────────────────────────────────────────────────

val sleepDuas: List<Dua> = listOf(
    Dua(
        id = "sleep_1",
        arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
        translation = "All praise is for Allah Who gave us life after having taken it from us, and unto Him is the resurrection. (Said upon waking up.)",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    Dua(
        id = "sleep_2",
        arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        translation = "In Your Name, O Allah, I die and I live. (Said before going to sleep.)",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    Dua(
        id = "sleep_3",
        arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
        translation = "In Your Name, my Lord, I lay down my side, and by You I raise it up. If You take my soul, have mercy upon it; if You return it, protect it as You protect Your righteous servants.",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 1
    ),
    Dua(
        id = "sleep_4",
        arabic = "سُبْحَانَ اللَّهِ (٣٣)، وَالْحَمْدُ لِلَّهِ (٣٣)، وَاللَّهُ أَكْبَرُ (٣٤)",
        translation = "Recite SubhanAllah 33 times, Alhamdulillah 33 times, and Allahu Akbar 34 times when going to bed.",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 33
    ),
    Dua(
        id = "sleep_5",
        arabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
        translation = "O Allah, protect me from Your punishment on the Day You resurrect Your servants. (Said three times before sleeping.)",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 3
    )
)

val mosqueDuas: List<Dua> = listOf(
    Dua(
        id = "mosque_1",
        arabic = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
        translation = "O Allah, Lord of this perfect call and established prayer, grant Muhammad the intercession and virtue, and resurrect him to the praised station You promised.",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    Dua(
        id = "mosque_2",
        arabic = "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
        translation = "In the Name of Allah, and prayers and peace be upon the Messenger of Allah. O Allah, open for me the gates of Your mercy. (Entering the mosque.)",
        source = "صحيح مسلم / Muslim",
        repeat = 1
    ),
    Dua(
        id = "mosque_3",
        arabic = "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
        translation = "In the Name of Allah, and prayers and peace be upon the Messenger of Allah. O Allah, I ask You from Your bounty. (Leaving the mosque.)",
        source = "صحيح مسلم / Muslim",
        repeat = 1
    )
)

val distressDuas: List<Dua> = listOf(
    Dua(
        id = "distress_1",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
        translation = "Dua for distress: None has the right to be worshipped but Allah, the Great, the Forbearing; Lord of the Magnificent Throne.",
        source = "صحيح البخاري ومسلم / Bukhari & Muslim",
        repeat = 1
    ),
    Dua(
        id = "distress_2",
        arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        translation = "Dua of Yunus: None has the right to be worshipped but You, glorified are You; truly I have been among the wrongdoers.",
        source = "جامع الترمذي / At-Tirmidhi",
        repeat = 1
    ),
    Dua(
        id = "distress_3",
        arabic = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ...",
        translation = "Dua al-Istikharah (Seeking divine guidance in decisions).",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    ),
    Dua(
        id = "distress_4",
        arabic = "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
        translation = "O Allah, nothing is easy except what You make easy, and You make hardship easy if You will.",
        source = "صحيح ابن حبان / Ibn Hibban",
        repeat = 1
    ),
    Dua(
        id = "distress_5",
        arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
        translation = "O Allah, I seek refuge in You from grief and sadness, weakness and laziness, miserliness and cowardice, the burden of debt, and the subjugation of men.",
        source = "صحيح البخاري / Al-Bukhari",
        repeat = 1
    )
)

val generalDuas: List<Dua> = listOf(
    Dua(
        id = "general_1",
        arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        translation = "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the torment of the Fire.",
        source = "القرآن ٢:٢٠١ / Bukhari",
        repeat = 1
    ),
    Dua(
        id = "general_2",
        arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
        translation = "O Turner of the hearts, make my heart firm upon Your religion.",
        source = "جامع الترمذي / At-Tirmidhi",
        repeat = 1
    ),
    Dua(
        id = "general_3",
        arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى",
        translation = "O Allah, I ask You for guidance, piety, chastity, and self-sufficiency.",
        source = "صحيح مسلم / Muslim",
        repeat = 1
    ),
    Dua(
        id = "general_4",
        arabic = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
        translation = "O Allah, assist me in remembering You, expressing gratitude to You, and worshipping You with excellence.",
        source = "سنن أبي داود / Abu Dawud",
        repeat = 1
    ),
    Dua(
        id = "general_5",
        arabic = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
        translation = "Kaffarat al-Majlis: Glory be to You, O Allah, and praise be to You. I bear witness that there is no deity except You, I seek Your forgiveness and repent to You.",
        source = "جامع الترمذي / At-Tirmidhi",
        repeat = 1
    )
)

val duaCategories: List<DuaCategory> = listOf(
    DuaCategory(
        id = "after_prayer",
        titleAr = "أذكار بعد الصلاة",
        titleEn = "After Prayer",
        duas = afterPrayerOtherDuas
    ),
    DuaCategory(
        id = "morning",
        titleAr = "أذكار الصباح",
        titleEn = "Morning Adhkar",
        duas = morningAdhkarList
    ),
    DuaCategory(
        id = "evening",
        titleAr = "أذكار المساء",
        titleEn = "Evening Adhkar",
        duas = eveningAdhkarList
    ),
    DuaCategory(
        id = "sleep",
        titleAr = "أذكار النوم",
        titleEn = "Sleep & Waking",
        duas = sleepDuas
    ),
    DuaCategory(
        id = "mosque",
        titleAr = "الأذان والمسجد",
        titleEn = "Adhan & Mosque",
        duas = mosqueDuas
    ),
    DuaCategory(
        id = "distress",
        titleAr = "الكرب والاستخارة",
        titleEn = "Distress & Guidance",
        duas = distressDuas
    ),
    DuaCategory(
        id = "general",
        titleAr = "جوامع الدعاء",
        titleEn = "Comprehensive Duas",
        duas = generalDuas
    )
)
