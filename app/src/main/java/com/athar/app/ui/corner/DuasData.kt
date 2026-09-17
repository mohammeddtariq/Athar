package com.athar.app.ui.corner

/**
 * Traditional morning & evening remembrances, prayers, and daily duas from
 * the authentic Sunnah (Hisnul Muslim / حصن المسلم).
 * Arabic wordings follow the verified narrations with accurate diacritics (tashkeel).
 * Each entry includes its English translation, hadith source, and repeat count.
 */
data class Dua(
    val arabic: String,
    val translation: String,
    val source: String,
    val repeat: String? = null
)

data class DuaCategory(val titleAr: String, val titleEn: String, val duas: List<Dua>)

val duaCategories: List<DuaCategory> = listOf(
    // ── 1. أذكار الصباح ──
    DuaCategory(
        "أذكار الصباح", "Morning Adhkar",
        listOf(
            Dua(
                "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                "Ayat al-Kursi (2:255). Whoever recites it in the morning is protected from the jinn until evening.",
                "Quran 2:255 / Al-Hakim"
            ),
            Dua(
                "قُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ\n\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ مِن شَرِّ مَا خَلَقَ ۞ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۞ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۞ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ\n\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۞ مَلِكِ النَّاسِ ۞ إِلَٰهِ النَّاسِ ۞ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۞ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۞ مِنَ الْجِنَّةِ وَالنَّاسِ",
                "Surah Al-Ikhlas, Al-Falaq, and An-Nas. Reciting them thrice morning and evening suffices against all harm.",
                "Abu Dawud & At-Tirmidhi",
                "×3"
            ),
            Dua(
                "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
                "We have entered the morning and all dominion belongs to Allah; praise is to Allah. There is no deity except Allah alone… My Lord, I ask You for the good of this day and what follows it, and I seek refuge from its evil.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
                "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                "Sayyid al-Istighfar (Chief prayer for forgiveness). Whoever recites it during the day with conviction and dies before evening will be among the people of Paradise.",
                "Al-Bukhari"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَصْبَحْتُ أُشْهِدُكَ، وَأُشْهِدُ حَمَلَةَ عَرْشِكَ، وَمَلَائِكَتَكَ، وَجَمِيعَ خَلْقِكَ، أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ، وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
                "O Allah, I have entered the morning calling You to witness, and calling the bearers of Your Throne, Your angels, and all Your creation to witness that You are Allah, there is no deity except You alone.",
                "Abu Dawud",
                "×4"
            ),
            Dua(
                "اللَّهُمَّ مَا أَصْبَحَ بِي مِنْ نِعْمَةٍ أَوْ بِأَحَدٍ مِنْ خَلْقِكَ فَمِنْكَ وَحْدَكَ لَا شَرِيكَ لَكَ، فَلَكَ الْحَمْدُ وَلَكَ الشُّكْرُ",
                "O Allah, whatever blessing has come to me or any of Your creation this morning is from You alone, without partner; to You belongs all praise and gratitude.",
                "Abu Dawud"
            ),
            Dua(
                "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ. اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ وَالْفَقْرِ، وَأَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، لَا إِلَهَ إِلَّا أَنْتَ",
                "O Allah, grant health to my body, my hearing, and my sight; there is no deity except You. I seek refuge in You from disbelief, poverty, and punishment in the grave.",
                "Abu Dawud",
                "×3"
            ),
            Dua(
                "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                "Allah suffices me; there is no deity except Him. In Him I trust, and He is the Lord of the Mighty Throne. Whoever says it seven times, Allah will suffice him against what worries him.",
                "Abu Dawud",
                "×7"
            ),
            Dua(
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                "In the Name of Allah, with Whose Name nothing can cause harm on earth or in heaven, and He is the All-Hearing, the All-Knowing.",
                "At-Tirmidhi & Abu Dawud",
                "×3"
            ),
            Dua(
                "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
                "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad (pbuh) as my Prophet. Whoever says it, it is a duty upon Allah to please him on Judgment Day.",
                "Abu Dawud & At-Tirmidhi",
                "×3"
            ),
            Dua(
                "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
                "O Ever-Living, O Sustainer, by Your mercy I seek assistance; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
                "Al-Hakim & An-Nasa'i"
            ),
            Dua(
                "أَصْبَحْنَا عَلَى فِطْرَةِ الْإِسْلَامِ، وَعَلَى كَلِمَةِ الْإِخْلَاصِ، وَعَلَى دِينِ نَبِيِّنَا مُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ، وَعَلَى مِلَّةِ أَبِينَا إِبْرَاهِيمَ حَنِيفًا مُسْلِمًا وَمَا كَانَ مِنَ الْمُشْرِكِينَ",
                "We enter the morning upon the natural creed of Islam, the word of sincere faith, the religion of our Prophet Muhammad, and the path of our father Ibrahim.",
                "Ahmad"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
                "Glory be to Allah and praise Him, according to the number of His creation, His good pleasure, the weight of His Throne, and the ink of His words.",
                "Muslim",
                "×3"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
                "O Allah, I ask You for beneficial knowledge, wholesome sustenance, and accepted deeds.",
                "Ibn Majah"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                "Glory be to Allah and His is the praise. Whoever says it 100 times, his sins will be forgiven even if they were like the foam of the sea.",
                "Muslim",
                "×100"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                "None has the right to be worshipped but Allah alone, having no partner… Whoever says it 100 times gains the reward of freeing ten slaves, 100 good deeds written, and 100 bad deeds erased.",
                "Bukhari & Muslim",
                "×100"
            ),
            Dua(
                "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
                "I seek forgiveness from Allah and repent to Him.",
                "Al-Bukhari",
                "×100"
            )
        )
    ),

    // ── 2. أذكار المساء ──
    DuaCategory(
        "أذكار المساء", "Evening Adhkar",
        listOf(
            Dua(
                "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                "Ayat al-Kursi (2:255). Whoever recites it in the evening is protected from the jinn until morning.",
                "Quran 2:255 / Al-Hakim"
            ),
            Dua(
                "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۞ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
                "The last two verses of Surah Al-Baqarah (285-286). Whoever recites them at night, they suffice him against all evil.",
                "Bukhari & Muslim"
            ),
            Dua(
                "قُلْ هُوَ اللَّهُ أَحَدٌ ... قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ... قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                "Recite Surah Al-Ikhlas, Al-Falaq, and An-Nas three times in the evening; they suffice you against everything.",
                "Abu Dawud & At-Tirmidhi",
                "×3"
            ),
            Dua(
                "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
                "We have reached the evening and all dominion belongs to Allah… My Lord, I ask You for the good of this night and what follows it, and seek refuge from its evil.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
                "O Allah, by You we enter the evening and by You we enter the morning, by You we live and by You we die, and to You is the final return.",
                "At-Tirmidhi"
            ),
            Dua(
                "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                "I seek refuge in Allah's perfect words from the evil of what He has created. Whoever recites it in the evening, no poison or bite will harm him that night.",
                "Muslim",
                "×3"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَمْسَيْتُ أُشْهِدُكَ، وَأُشْهِدُ حَمَلَةَ عَرْشِكَ، وَمَلَائِكَتَكَ، وَجَمِيعَ خَلْقِكَ، أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ، وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
                "O Allah, I have entered the evening calling You to witness… that You are Allah, there is no deity except You alone.",
                "Abu Dawud",
                "×4"
            ),
            Dua(
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                "In the Name of Allah, with Whose Name nothing can cause harm on earth or in heaven.",
                "At-Tirmidhi & Abu Dawud",
                "×3"
            ),
            Dua(
                "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
                "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad as my Prophet.",
                "Abu Dawud",
                "×3"
            )
        )
    ),

    // ── 3. أذكار بعد الصلاة المفروضة ──
    DuaCategory(
        "أذكار بعد الصلاة", "After Obligatory Prayer",
        listOf(
            Dua(
                "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                "I ask Allah for forgiveness (three times). O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of Glory and Honor.",
                "Muslim",
                "×3"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
                "None has the right to be worshipped but Allah alone… O Allah, none can prevent what You give, nor can anyone give what You prevent.",
                "Bukhari & Muslim"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ",
                "Tasbeeh: Glory be to Allah. Recited 33 times after each obligatory prayer.",
                "Muslim",
                "×33"
            ),
            Dua(
                "الْحَمْدُ لِلَّهِ",
                "Tahmeed: Praise be to Allah. Recited 33 times after each obligatory prayer.",
                "Muslim",
                "×33"
            ),
            Dua(
                "اللَّهُ أَكْبَرُ",
                "Takbeer: Allah is the Greatest. Recited 33 times after each obligatory prayer.",
                "Muslim",
                "×33"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                "Completing the hundredth count: whoever recites this after 33 tasbeeh, 33 tahmeed, and 33 takbeer, his sins will be forgiven even if like the foam of the sea.",
                "Muslim"
            ),
            Dua(
                "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ... (آيَةُ الْكُرْسِيِّ)",
                "Ayat al-Kursi after every obligatory prayer: nothing stands between him and entering Paradise except death.",
                "An-Nasa'i"
            ),
            Dua(
                "قِرَاءَةُ الْمُعَوِّذَاتِ: سُورَةُ الْإِخْلَاصِ، وَالْفَلَقِ، وَالنَّاسِ",
                "Reciting the three surahs (Al-Ikhlas, Al-Falaq, An-Nas) once after each obligatory prayer (and thrice after Fajr and Maghrib).",
                "Abu Dawud & An-Nasa'i"
            )
        )
    ),

    // ── 4. أذكار النوم والاستيقاظ ──
    DuaCategory(
        "النوم والاستيقاظ", "Sleep & Waking",
        listOf(
            Dua(
                "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                "All praise is for Allah Who gave us life after having taken it from us, and unto Him is the resurrection. (Said upon waking up.)",
                "Al-Bukhari"
            ),
            Dua(
                "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                "In Your Name, O Allah, I die and I live. (Said before going to sleep.)",
                "Al-Bukhari"
            ),
            Dua(
                "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
                "In Your Name, my Lord, I lay down my side, and by You I raise it up. If You take my soul, have mercy upon it; if You return it, protect it as You protect Your righteous servants.",
                "Bukhari & Muslim"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ (٣٣)، وَالْحَمْدُ لِلَّهِ (٣٣)، وَاللَّهُ أَكْبَرُ (٣٤)",
                "Recite SubhanAllah 33 times, Alhamdulillah 33 times, and Allahu Akbar 34 times when going to bed. The Prophet taught this to Ali and Fatimah as better than a servant.",
                "Bukhari & Muslim",
                "×33"
            ),
            Dua(
                "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                "O Allah, protect me from Your punishment on the Day You resurrect Your servants. (Said three times before sleeping.)",
                "Abu Dawud",
                "×3"
            )
        )
    ),

    // ── 5. أذكار الأذان والمساجد ──
    DuaCategory(
        "الأذان والمسجد", "Adhan & Mosques",
        listOf(
            Dua(
                "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
                "O Allah, Lord of this perfect call and established prayer, grant Muhammad the intercession and virtue, and resurrect him to the praised station You promised. (Guarantees intercession.)",
                "Al-Bukhari"
            ),
            Dua(
                "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
                "In the Name of Allah, and prayers and peace be upon the Messenger of Allah. O Allah, open for me the gates of Your mercy. (Entering the mosque.)",
                "Muslim"
            ),
            Dua(
                "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
                "In the Name of Allah, and prayers and peace be upon the Messenger of Allah. O Allah, I ask You from Your bounty. (Leaving the mosque.)",
                "Muslim"
            )
        )
    ),

    // ── 6. أدعية الحاجة والكرب والاستخارة ──
    DuaCategory(
        "الكرب والاستخارة", "Distress & Guidance",
        listOf(
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
                "Dua for distress and hardship. None has the right to be worshipped but Allah, the Great, the Forbearing; Lord of the Magnificent Throne.",
                "Bukhari & Muslim"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                "Dua of Yunus (Dhun-Nun): None has the right to be worshipped but You, glorified are You; truly I have been among the wrongdoers. No Muslim supplicates with it for any matter but Allah responds.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ...",
                "Dua al-Istikharah (Seeking divine guidance in decisions): The Prophet taught this for every matter just as he taught a surah of the Quran.",
                "Al-Bukhari"
            ),
            Dua(
                "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
                "O Allah, nothing is easy except what You make easy, and You make hardship easy if You will.",
                "Ibn Hibban"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
                "O Allah, I seek refuge in You from grief and sadness, weakness and laziness, miserliness and cowardice, the burden of debt, and the subjugation of men.",
                "Al-Bukhari"
            )
        )
    ),

    // ── 7. جوامع الدعاء النبوي ──
    DuaCategory(
        "جوامع الدعاء", "Comprehensive Duas",
        listOf(
            Dua(
                "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the torment of the Fire. (The most frequent supplication of the Prophet.)",
                "Quran 2:201 / Bukhari"
            ),
            Dua(
                "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
                "O Turner of the hearts, make my heart firm upon Your religion.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى",
                "O Allah, I ask You for guidance, piety, chastity, and self-sufficiency.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
                "O Allah, assist me in remembering You, expressing gratitude to You, and worshipping You with excellence.",
                "Abu Dawud"
            ),
            Dua(
                "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
                "Kaffarat al-Majlis (Expiation for gathering): Recited at the end of any assembly to forgive whatever occurred therein.",
                "At-Tirmidhi"
            )
        )
    )
)
