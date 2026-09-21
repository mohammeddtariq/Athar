import zipfile
import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
surah_headers = {}

with zipfile.ZipFile(zip_path, 'r') as z:
    for name in z.namelist():
        if name.startswith('__MACOSX') or not name.endswith('.svg'):
            continue
        page_str = name.split('/')[-1].replace('.svg', '')
        if not page_str.isdigit():
            continue
        page_num = int(page_str)
        data = z.read(name).decode('utf-8', errors='replace')
        # Look for surah-name groups
        matches = re.finditer(r'<g\s+id="([^"]+)"[^>]*data-type="surah-name">(.*?)</g>\s*</g>', data, re.DOTALL)
        for m in matches:
            gid = m.group(1)
            chunk = m.group(2)
            # Find the surah number from subsequent words or text
            surah_m = re.search(r'data-surah="(\d+)"', data[m.end():m.end()+2000])
            snum = int(surah_m.group(1)) if surah_m else None
            surah_headers[snum or len(surah_headers)+1] = {
                'page': page_num,
                'gid': gid,
                'path_count': len(re.findall(r'<path', chunk)),
                'chunk_len': len(chunk)
            }

print(f"Total surah-name groups found: {len(surah_headers)}")
print("First 10 surah headers found:")
for k in sorted(surah_headers.keys())[:10]:
    print(f"  Surah {k}: Page {surah_headers[k]['page']}, paths={surah_headers[k]['path_count']}")
