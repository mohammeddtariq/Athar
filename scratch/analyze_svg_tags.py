import zipfile
import re

z = zipfile.ZipFile('D:/Codespace/android-dev/ligature-basd-svg.zip')
c = z.read('ligature-basd-svg/001.svg').decode('utf-8-sig')

tags = set(re.findall(r'<([a-zA-Z0-9]+)', c))
print('Tags in 001.svg:', tags)

data_types = set(re.findall(r'data-type="([^"]+)"', c))
print('data-types:', data_types)

# check viewBox
vb = re.search(r'viewBox="([^"]+)"', c)
print('viewBox:', vb.group(0) if vb else 'None')

# Check first 3 paths
paths = re.findall(r'<path[^>]+>', c)
print(f'Total paths: {len(paths)}')
for p in paths[:3]:
    print(p[:120])
