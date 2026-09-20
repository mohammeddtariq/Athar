import re

with open('scratch/surahSvgBannerDark.svg', 'r', encoding='utf-8') as f:
    text = f.read()

fills = set(re.findall(r'fill="([^"]+)"', text))
strokes = set(re.findall(r'stroke="([^"]+)"', text))
print('Fills in dark:', fills)
print('Strokes in dark:', strokes)

with open('scratch/surahSvgBanner.svg', 'r', encoding='utf-8') as f:
    text_light = f.read()

fills_light = set(re.findall(r'fill="([^"]+)"', text_light))
strokes_light = set(re.findall(r'stroke="([^"]+)"', text_light))
print('Fills in light:', fills_light)
print('Strokes in light:', strokes_light)
