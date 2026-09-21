import zipfile
import re

z = zipfile.ZipFile('D:/Codespace/android-dev/ligature-basd-svg.zip')
all_tags = set()
all_data_types = set()

for i in range(1, 605):
    fname = f"ligature-basd-svg/{i:03d}.svg"
    try:
        content = z.read(fname).decode('utf-8-sig')
        tags = set(re.findall(r'<([a-zA-Z0-9]+)', content))
        all_tags.update(tags)
        d_types = set(re.findall(r'data-type="([^"]+)"', content))
        all_data_types.update(d_types)
    except Exception as e:
        print(f"Error reading {fname}: {e}")

print("All tags across all 604 pages:", all_tags)
print("All data-types across all 604 pages:", all_data_types)
