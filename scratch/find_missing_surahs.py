import zipfile
import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
found_surahs = set()

with zipfile.ZipFile(zip_path, 'r') as z:
    for name in z.namelist():
        if name.startswith('__MACOSX') or not name.endswith('.svg'):
            continue
        data = z.read(name).decode('utf-8', errors='replace')
        for m in re.finditer(r'data-type="surah-name"', data):
            # Look backwards or forwards for surah number
            window = data[max(0, m.start()-500):min(len(data), m.end()+2000)]
            # Find all data-surah attributes in this window
            surah_nums = re.findall(r'data-surah="(\d+)"', window)
            for s in surah_nums:
                found_surahs.add(int(s))

print("Found surahs count:", len(found_surahs))
missing = [i for i in range(1, 115) if i not in found_surahs]
print("Missing surahs:", missing)
