import zipfile
import re
import json
import os
import sys
sys.stdout.reconfigure(encoding='utf-8')

with open(r'app\src\main\java\com\athar\app\ui\corner\QuranData.kt', 'r', encoding='utf-8') as f:
    text = f.read()

matches = re.findall(r'SurahMeta\((\d+),\s*"([^"]+)",\s*"([^"]+)",\s*"[^"]*",\s*\d+,\s*\d+,\s*[^,]+,\s*(\d+)\)', text)

surah_vectors = {}
zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
with zipfile.ZipFile(zip_path, 'r') as z:
    for num, ar_name, en_name, start_page in matches:
        p_str = f"{int(start_page):03d}"
        fname = f"ligature-basd-svg/{p_str}.svg"
        data = z.read(fname).decode('utf-8')
        m = re.search(r'<g\s+id="md-line-\d+"[^>]*data-type="surah-name">(.*?)</g>\s*</g>', data, re.DOTALL)
        if m:
            chunk = m.group(1)
            paths = re.findall(r'<path[^>]*d="([^"]+)"', chunk)
            surah_vectors[num] = paths

out_dir = r"app\src\main\assets"
os.makedirs(out_dir, exist_ok=True)
out_path = os.path.join(out_dir, "quran_surah_titles_vector.json")
with open(out_path, 'w', encoding='utf-8') as f:
    json.dump(surah_vectors, f, separators=(',', ':'))

print(f"Saved {len(surah_vectors)} surahs to {out_path}!")
print("File size:", os.path.getsize(out_path), "bytes (~" + str(round(os.path.getsize(out_path) / 1024)) + " KB)")
