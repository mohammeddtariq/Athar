import zipfile
import re
import json

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
with open(r'app\src\main\java\com\athar\app\ui\corner\QuranData.kt', 'r', encoding='utf-8') as f:
    text = f.read()

matches = re.findall(r'SurahMeta\((\d+),\s*"([^"]+)",\s*"([^"]+)",\s*"[^"]*",\s*\d+,\s*\d+,\s*[^,]+,\s*(\d+)\)', text)

surah_vectors = {}
with zipfile.ZipFile(zip_path, 'r') as z:
    for num, ar_name, en_name, start_page in matches:
        p_str = f"{int(start_page):03d}"
        fname = f"ligature-basd-svg/{p_str}.svg"
        data = z.read(fname).decode('utf-8')
        m = re.search(r'<g\s+id="md-line-\d+"[^>]*data-type="surah-name">(.*?)</g>\s*</g>', data, re.DOTALL)
        if m:
            chunk = m.group(1)
            # Extract all paths
            paths = re.findall(r'<path[^>]*d="([^"]+)"', chunk)
            surah_vectors[int(num)] = {
                'name': ar_name,
                'page': int(start_page),
                'paths': paths
            }

print(f"Extracted vector paths for {len(surah_vectors)} / 114 surahs!")
# Check path count and size for Surah 1, 2, 114
for s in [1, 2, 112, 113, 114]:
    print(f"  Surah {s} ({surah_vectors[s]['name']}): {len(surah_vectors[s]['paths'])} paths")
