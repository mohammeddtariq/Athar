import zipfile
import re

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
with zipfile.ZipFile(zip_path, 'r') as z:
    svg_002 = z.read("ligature-basd-svg/002.svg").decode('utf-8')
    svg_569 = z.read("ligature-basd-svg/569.svg").decode('utf-8')

# Strip XML header
svg_002_clean = svg_002[svg_002.find('<svg'):]
svg_569_clean = svg_569[svg_569.find('<svg'):]

html = f"""<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
<meta charset="UTF-8">
<title>Ligature SVG Mushaf Test</title>
<style>
  body {{
    background-color: #121212;
    color: #ffffff;
    font-family: sans-serif;
    margin: 0;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 40px;
  }}
  h2 {{
    margin: 0 0 10px 0;
    font-size: 16px;
    color: #888;
  }}
  .page-card {{
    width: 420px;
    max-width: 95vw;
    border-radius: 16px;
    padding: 24px 16px;
    box-shadow: 0 4px 24px rgba(0,0,0,0.4);
    box-sizing: border-box;
  }}
  
  /* Theme 1: AMOLED Pure Black */
  .theme-amoled {{
    background-color: #000000;
  }}
  .theme-amoled svg {{
    width: 100%;
    height: auto;
    display: block;
    fill: #FFFFFF;
  }}
  .theme-amoled g[id^="md-ornament"] path {{
    fill: #D4AF37;
  }}
  .theme-amoled g[id^="md-number"] path {{
    fill: #FFFFFF;
  }}
  .theme-amoled g[id*="surah-name"] path {{
    fill: #E5C378;
  }}

  /* Theme 2: Athar Dark Olive */
  .theme-olive {{
    background-color: #0A0C08;
    border: 1px solid #22281D;
  }}
  .theme-olive svg {{
    width: 100%;
    height: auto;
    display: block;
    fill: #EDEFEA;
  }}
  .theme-olive g[id^="md-ornament"] path {{
    fill: #8E9B86;
  }}
  .theme-olive g[id^="md-number"] path {{
    fill: #EDEFEA;
  }}

  /* Theme 3: Classic Light */
  .theme-light {{
    background-color: #FBF9F4;
    border: 1px solid #DFD9CC;
  }}
  .theme-light svg {{
    width: 100%;
    height: auto;
    display: block;
    fill: #1A1D18;
  }}
  .theme-light g[id^="md-ornament"] path {{
    fill: #8B7355;
  }}
  .theme-light g[id^="md-number"] path {{
    fill: #1A1D18;
  }}
</style>
</head>
<body>

  <h1>Athar Mushaf Themes Preview (Ligature SVG)</h1>

  <h2>AMOLED Pure Black (Page 2 - Surah Al-Baqarah)</h2>
  <div class="page-card theme-amoled">
    {svg_002_clean}
  </div>

  <h2>Athar Dark Olive (Page 569 - Surah Al-Haqqah / Al-Ma'arij)</h2>
  <div class="page-card theme-olive">
    {svg_569_clean}
  </div>

  <h2>Classic Light Paper (Page 2)</h2>
  <div class="page-card theme-light">
    {svg_002_clean}
  </div>

</body>
</html>
"""

with open(r'scratch\test_all_themes.html', 'w', encoding='utf-8') as f:
    f.write(html)

print("Generated scratch/test_all_themes.html! Size:", len(html))
