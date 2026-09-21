import zipfile

z = zipfile.ZipFile('D:/Codespace/android-dev/ligature-basd-svg.zip')
c = z.read('ligature-basd-svg/001.svg').decode('utf-8-sig')

for line in c.splitlines():
    if '<g id="md-line' in line:
        print(line.strip())
