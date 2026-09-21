import zipfile, re
z = zipfile.ZipFile('D:/Codespace/android-dev/ligature-basd-svg.zip')
for p in [1, 2, 10, 50, 100, 200, 300, 400, 500, 600]:
    c = z.read(f'ligature-basd-svg/{p:03d}.svg').decode('utf-8-sig')
    print(f'Page {p}: {len(re.findall(r"<path", c))} paths')
