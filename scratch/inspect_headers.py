import zipfile
import re

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
with zipfile.ZipFile(zip_path, 'r') as z:
    for page in ['001', '002', '568', '569']:
        fname = f"ligature-basd-svg/{page}.svg"
        data = z.read(fname).decode('utf-8', errors='replace')
        lines = re.findall(r'<g\s+id="(md-line-[^"]+)"([^>]*)>', data)
        print(f"=== Page {page} ===")
        for l in lines:
            print(f"  {l[0]} {l[1]}")
