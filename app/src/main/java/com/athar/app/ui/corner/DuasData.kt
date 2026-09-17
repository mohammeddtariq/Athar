package com.athar.app.ui.corner

/** A dua with Arabic text, translation hint and source. Content: traditional Hisnul Muslim duas. */
data class Dua(val arabic: String, val translation: String, val source: String)
data class DuaCategory(val titleAr: String, val titleEn: String, val duas: List<Dua>)

val duaCategories: List<DuaCategory> = listOf(
    DuaCategory(
        "أذكار الصباح", "Morning",
        listOf(
            Dua(
                "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
                "We have reached the morning, and all sovereignty belongs to Allah. Praise be to Allah.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
                "O Allah, by You we enter the morning and by You we enter the evening; by You we live and by You we die, and to You is the resurrection.",
                "At-Tirmidhi"
            ),
            Dua(
                "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ… (آية الكرسي)",
                "Ayat al-Kursi (2:255) — whoever recites it in the morning is protected until evening.",
                "Al-Bukhari (Tārīkh)"
            )
        )
    ),
    DuaCategory(
        "أذكار المساء", "Evening",
        listOf(
            Dua(
                "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
                "We have reached the evening, and all sovereignty belongs to Allah. Praise be to Allah.",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
                "O Allah, by You we enter the evening and by You we enter the morning; by You we live and by You we die, and to You is the return.",
                "At-Tirmidhi"
            )
        )
    ),
    DuaCategory(
        "بعد الصلاة", "After prayer",
        listOf(
            Dua(
                "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ",
                "I seek Allah's forgiveness (three times).",
                "Muslim"
            ),
            Dua(
                "اللَّهُمَّ أَنْتَ السَّلَامُ، وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                "O Allah, You are Peace, and from You comes peace. Blessed are You, O Possessor of Majesty and Honor.",
                "Muslim"
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
                "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                "Praise be to Allah who gave us life after death, and to Him is the resurrection.",
                "Al-Bukhari"
            )
        )
    ),
    DuaCategory(
        "الطعام والبيت", "Food & home",
        listOf(
            Dua(
                "بِسْمِ اللَّهِ",
                "In the name of Allah (before eating).",
                "Abu Dawud"
            ),
            Dua(
                "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                "Praise be to Allah who fed me this and provided it without any power of mine.",
                "Abu Dawud"
            ),
            Dua(
                "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
                "In Allah's name we enter and in Allah's name we leave, and upon our Lord we rely (entering home).",
                "Abu Dawud"
            )
        )
    ),
    DuaCategory(
        "الكرب والسفر", "Distress & travel",
        listOf(
            Dua(
                "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ",
                "None has the right to be worshipped but Allah, the Great, the Forbearing… (in distress).",
                "Al-Bukhari & Muslim"
            ),
            Dua(
                "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنقَلِبُونَ",
                "Glory be to Him who subjected this to us… and to our Lord we shall return (when mounting).",
                "Muslim"
            )
        )
    )
)
