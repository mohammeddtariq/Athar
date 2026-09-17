package com.athar.app.ui.corner

/**
 * Traditional morning & evening remembrances and daily duas in the spirit of
 * Hisnul Muslim. Arabic wordings follow the famous narrations; each entry is
 * tagged with its source and repeat count where the Sunnah specifies one.
 */
data class Dua(
    val arabic: String,
    val translation: String,
    val source: String,
    val repeat: String? = null
)

data class DuaCategory(val titleAr: String, val titleEn: String, val duas: List<Dua>)

val duaCategories: List<DuaCategory> = listOf(
    DuaCategory(
        "أذكار الصباح", "Morning",
        listOf(
            Dua(
                "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ وَلَا يَئُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                "Ayat al-Kursi (2:255). Whoever recites it in the morning is protected until the evening.",
                "Quran 2:255"
            ),
            Dua(
                "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
                "We have reached the morning and all sovereignty belongs to Allah… My Lord, I ask You for the good of this day and I seek refuge in You from its evil.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ أَمْسَيْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ",
                "O Allah, by You we enter the morning and by You we enter the evening; by You we live and by You we die, and to You is the resurrection.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                "Sayyid al-Istighfar. Whoever says it in the morning with certainty and dies that day enters Paradise.",
                "Al-Bukhari"
            ),
            Dua(
                "رَضِيتُ بِاللَّهِ رَبًّا وَبِالْإِسْلَامِ دِينًا وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
                "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad as my Prophet.",
                "Abu Dawud",
                "×3"
            ),
            Dua(
                "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                "Allah suffices me; there is no god but He. In Him I trust, and He is the Lord of the Mighty Throne. Allah suffices whoever says it against what worries him.",
                "Abu Dawud",
                "×7"
            ),
            Dua(
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                "In the name of Allah, with whose name nothing on earth or in heaven can harm. Nothing sudden befalls whoever says it thrice.",
                "At-Tirmidhi",
                "×3"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                "Glory be to Allah and praise Him. Whoever says it a hundred times, his sins are forgiven even if like the sea's foam.",
                "Muslim",
                "×100"
            ),
            Dua(
                "أَصْبَحْنَا عَلَى فِطْرَةِ الْإِسْلَامِ وَعَلَى كَلِمَةِ الْإِخْلَاصِ وَعَلَى دِينِ نَبِيِّنَا مُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ وَعَلَى مِلَّةِ أَبِينَا إِبْرَاهِيمَ حَنِيفًا مُسْلِمًا وَمَا كَانَ مِنَ الْمُشْرِكِينَ",
                "We have entered the morning upon the fitrah of Islam, the word of sincerity, the religion of our Prophet Muhammad, and the creed of our father Abraham.",
                "Ahmad"
            ),
            Dua(
                "اللَّهُمَّ فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ رَبَّ كُلِّ شَيْءٍ وَمَلِيكَهُ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَعُوذُ بِكَ مِنْ شَرِّ نَفْسِي وَشَرِّ الشَّيْطَانِ وَشِرْكِهِ",
                "O Allah, Originator of the heavens and the earth, Knower of the unseen and the seen… I seek refuge in You from the evil of my soul and of Satan.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ",
                "O Allah, grant me health in my body, my hearing and my sight; there is no god but You.",
                "Abu Dawud",
                "×3"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ وَالْفَقْرِ، وَأَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، لَا إِلَهَ إِلَّا أَنْتَ",
                "O Allah, I seek refuge in You from disbelief and poverty, and from the punishment of the grave.",
                "Abu Dawud",
                "×3"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                "None has the right to be worshipped but Allah alone… Whoever says it a hundred times, it equals freeing ten slaves and no one brings better.",
                "Bukhari & Muslim",
                "×100"
            ),
            Dua(
                "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
                "I seek Allah's forgiveness and repent to Him.",
                "Al-Bukhari",
                "×100"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ",
                "Glory be to Allah and praise Him, by the number of His creation, His pleasure, the weight of His Throne and the ink of His words. These words outweigh a morning of worship.",
                "Muslim",
                "×3"
            ),
            Dua(
                "سورة الإخلاص والفلق والناس — تُقرأ حين تُصبح وحين تُمسي",
                "Recite Al-Ikhlas, Al-Falaq and An-Nas morning and evening thrice; they suffice you against everything.",
                "At-Tirmidhi",
                "×3"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَصْبَحْتُ أُشْهِدُكَ وَأُشْهِدُ حَمَلَةَ عَرْشِكَ وَمَلَائِكَتَكَ وَجَمِيعَ خَلْقِكَ أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
                "O Allah, I enter the morning calling You and all creation to witness that You are Allah… Whoever says it, Allah frees him from the Fire.",
                "Abu Dawud",
                "×4"
            )
        )
    ),
    DuaCategory(
        "أذكار المساء", "Evening",
        listOf(
            Dua(
                "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
                "We have reached the evening and all sovereignty belongs to Allah… My Lord, I ask You for the good of this night and I seek refuge in You from its evil.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَمْسَيْنَا وَبِكَ أَصْبَحْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ",
                "O Allah, by You we enter the evening and by You we enter the morning; by You we live and by You we die, and to You is the return.",
                "At-Tirmidhi"
            ),
            Dua(
                "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                "I seek refuge in Allah's perfect words from the evil of what He created. Nothing harms whoever says it in the evening thrice.",
                "Muslim",
                "×3"
            ),
            Dua(
                "آمَنَ الرَّسُولُ بِمَا أُنْزِلَ إِلَيْهِ مِنْ رَبِّهِ وَالْمُؤْمِنُونَ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِنْ رُسُلِهِ وَقَالُوا سَمِعْنَا وَأَطَعْنَا غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ. لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ رَبَّنَا لَا تُؤَاخِذْنَا إِنْ نَسِينَا أَوْ أَخْطَأْنَا رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِنْ قَبْلِنَا رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا أَنْتَ مَوْلَانَا فَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
                "The last two verses of Al-Baqarah. Whoever recites them at night, they suffice him.",
                "Bukhari & Muslim"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَمْسَيْتُ أُشْهِدُكَ وَأُشْهِدُ حَمَلَةَ عَرْشِكَ وَمَلَائِكَتَكَ وَجَمِيعَ خَلْقِكَ أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ",
                "O Allah, I enter the evening calling You and all creation to witness that You are Allah… Whoever says it, Allah frees him from the Fire.",
                "Abu Dawud",
                "×4"
            )
        )
    ),
    DuaCategory(
        "بعد الصلاة", "After prayer",
        listOf(
            Dua(
                "أَسْتَغْفِرُ اللَّهَ أَسْتَغْفِرُ اللَّهَ أَسْتَغْفِرُ اللَّهَ، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                "I seek Allah's forgiveness (thrice). O Allah, You are Peace and from You comes peace; blessed are You, Possessor of Majesty and Honor.",
                "Muslim",
                "×3"
            ),
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ وَلَا مُعْطِيَ لِمَا مَنَعْتَ وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
                "None has the right to be worshipped but Allah alone… O Allah, none can withhold what You give and none can give what You withhold.",
                "Bukhari & Muslim"
            ),
            Dua(
                "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَاللَّهُ أَكْبَرُ",
                "Glory be to Allah (33), praise be to Allah (33), Allah is Greatest (34). His sins are forgiven even if like the sea's foam.",
                "Muslim",
                "33 / 34"
            ),
            Dua(
                "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ وَلَا يَئُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                "Ayat al-Kursi after every obligatory prayer: nothing stands between him and Paradise but death.",
                "An-Nasa'i"
            )
        )
    ),
    DuaCategory(
        "النوم والاستيقاظ", "Sleep & waking",
        listOf(
            Dua(
                "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                "In Your name, O Allah, I die and I live.",
                "Al-Bukhari"
            ),
            Dua(
                "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
                "In Your name, my Lord, I lay down and in Your name I rise. If You take my soul, have mercy on it; if You return it, protect it as You protect Your righteous servants.",
                "Bukhari & Muslim"
            ),
            Dua(
                "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                "O Allah, shield me from Your punishment on the Day You resurrect Your servants.",
                "Abu Dawud",
                "×3"
            ),
            Dua(
                "يَجْمَعُ كَفَّيْهِ فَيَنْفُثُ فِيهِمَا فَيَقْرَأُ: سُورَةَ الْإِخْلَاصِ وَالْفَلَقِ وَالنَّاسِ، ثُمَّ يَمْسَحُ بِهِمَا مَا اسْتَطَاعَ مِنْ جَسَدِهِ",
                "Cup the hands, blow into them, recite Al-Ikhlas, Al-Falaq and An-Nas, then wipe the body — thrice.",
                "Al-Bukhari",
                "×3"
            ),
            Dua(
                "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                "Praise be to Allah who gave us life after death, and to Him is the resurrection. (Upon waking.)",
                "Al-Bukhari"
            )
        )
    ),
    DuaCategory(
        "الطعام والبيت والمسجد", "Food, home & mosque",
        listOf(
            Dua(
                "بِسْمِ اللَّهِ، فَإِنْ نَسِيَ فِي أَوَّلِهِ فَلْيَقُلْ: بِسْمِ اللَّهِ فِي أَوَّلِهِ وَآخِرِهِ",
                "In the name of Allah (before eating). If forgotten at the start: In Allah's name in its beginning and its end.",
                "Abu Dawud"
            ),
            Dua(
                "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                "Praise be to Allah who fed me this and provided it without any power of mine. His past sins are forgiven.",
                "Abu Dawud"
            ),
            Dua(
                "بِسْمِ اللَّهِ وَلَجْنَا وَبِسْمِ اللَّهِ خَرَجْنَا وَعَلَى رَبِّنَا تَوَكَّلْنَا",
                "In Allah's name we enter and in Allah's name we leave, and upon our Lord we rely. (Entering home.)",
                "Abu Dawud"
            ),
            Dua(
                "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                "In Allah's name, I trust in Allah; there is no power except with Allah. It is said to him: you are guided, sufficed and protected. (Leaving home.)",
                "Abu Dawud"
            ),
            Dua(
                "أَعُوذُ بِاللَّهِ الْعَظِيمِ وَبِوَجْهِهِ الْكَرِيمِ وَسُلْطَانِهِ الْقَدِيمِ مِنَ الشَّيْطَانِ الرَّجِيمِ، بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
                "I seek refuge in Almighty Allah… O Allah, open for me the doors of Your mercy. (Entering the mosque.)",
                "Abu Dawud"
            ),
            Dua(
                "بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
                "In Allah's name, and blessings upon the Messenger of Allah. O Allah, I ask You from Your bounty. (Leaving the mosque.)",
                "Muslim"
            )
        )
    ),
    DuaCategory(
        "بعد الأذان", "After the adhan",
        listOf(
            Dua(
                "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
                "O Allah, Lord of this perfect call and established prayer, grant Muhammad the intercession and distinction, and raise him to the praised station. Whoever says it, my intercession is assured for him.",
                "Al-Bukhari"
            )
        )
    ),
    DuaCategory(
        "جوامع الدعاء", "Essential supplications",
        listOf(
            Dua(
                "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                "Our Lord, give us good in this world and good in the Hereafter, and shield us from the punishment of the Fire.",
                "Quran 2:201"
            ),
            Dua(
                "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
                "O Allah, help me to remember You, thank You, and worship You excellently.",
                "Abu Dawud"
            ),
            Dua(
                "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
                "O Turner of hearts, make my heart firm upon Your religion.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ زَوَالِ نِعْمَتِكَ وَتَحَوُّلِ عَافِيَتِكَ وَفُجَاءَةِ نِقْمَتِكَ وَجَمِيعِ سَخَطِكَ",
                "O Allah, I seek refuge in You from the removal of Your blessing, the loss of Your protection, sudden punishment, and all Your anger.",
                "Muslim"
            )
        )
    ),
    DuaCategory(
        "الكرب والسفر", "Distress & travel",
        listOf(
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
                "None has the right to be worshipped but Allah, the Great, the Forbearing… (Said in distress.)",
                "Bukhari & Muslim"
            ),
            Dua(
                "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
                "O Living, O Sustainer, by Your mercy I seek relief; set all my affairs right and do not leave me to myself for a blink.",
                "Al-Hakim"
            ),
            Dua(
                "اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ، اللَّهُمَّ أَنْتَ الصَّاحِبُ فِي السَّفَرِ وَالْخَلِيفَةُ فِي الْأَهْلِ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ وَعْثَاءِ السَّفَرِ وَكَآبَةِ الْمَنْظَرِ وَسُوءِ الْمُنْقَلَبِ فِي الْمَالِ وَالْأَهْلِ",
                "The traveler's dua: takbir thrice, glorification of Him who subjected this ride, then asking for righteousness, ease, and companionship. (When mounting for travel.)",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
                "O Allah, nothing is easy except what You make easy, and You make grief easy when You will. (Before a difficult matter.)",
                "Ibn Hibban"
            )
        )
    )
)
