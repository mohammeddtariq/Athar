import zipfile
import re

z = zipfile.ZipFile('D:/Codespace/android-dev/ligature-basd-svg.zip')
c = z.read('ligature-basd-svg/001.svg').decode('utf-8-sig')

# remove xml declaration
if c.startswith('<?xml'):
    clean_svg = c.split('?>', 1)[1].strip()
else:
    # could have leading whitespace or BOM
    clean_svg = re.sub(r'^\s*<\?xml[^>]*\?>', '', c).strip()

html = f"""<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    * {{ margin: 0; padding: 0; box-sizing: border-box; }}
    body {{ background: #000000; display: flex; justify-content: center; align-items: center; min-height: 100vh; }}
    .mushaf-container {{ width: 100%; max-width: 480px; padding: 16px; }}
    svg {{ width: 100%; height: auto; display: block; }}
    path {{ fill: #ffffff; }}
    [data-type="aya-mark"] path {{ fill: #D4AF37 !important; }}
    [data-type="surah-name"] path {{ fill: #ffffff !important; }}
  </style>
</head>
<body>
  <div class="mushaf-container">
    {clean_svg}
  </div>
</body>
</html>"""

with open('scratch/test_page.html', 'w', encoding='utf-8') as f:
    f.write(html)

print('Wrote scratch/test_page.html, size:', len(html))
