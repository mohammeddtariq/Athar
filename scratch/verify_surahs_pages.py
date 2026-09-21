import zipfile
import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

# Let's read QuranData.kt to extract allSurahs pages
with open(r'app\src\main\java\com\athar\app\ui\corner\QuranData.kt', 'r', encoding='utf-8') as f:
    text = f.read()

# Pattern: SurahMeta(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, 1, RevelationType.MECCAN, 1)
matches = re.findall(r'SurahMeta\((\d+),\s*"([^"]+)",\s*"([^"]+)",\s*"[^"]*",\s*\d+,\s*\d+,\s*[^,]+,\s*(\d+)\)', text)
print(f"Loaded {len(matches)} surahs from QuranData.kt")

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
all_ok = True
with zipfile.ZipFile(zip_path, 'r') as z:
    for num, ar_name, en_name, start_page in matches:
        p_str = f"{int(start_page):03d}"
        fname = f"ligature-basd-svg/{p_str}.svg"
        if fname in z.namelist():
            data = z.read(fname).decode('utf-8')
            if 'data-type="surah-name"' not in data:
                print(f"Surah {num} ({ar_name}): Page {p_str} -> MISSING surah-name!")
                all_ok = False
        else:
            print(f"Surah {num} ({ar_name}): Page {p_str} NOT in zip")
            all_ok = False

if all_ok:
    print("ALL 114 SURAHS HAVE AUTHENTIC CALLIGRAPHIC SURAH-NAME IN ZIP!")
