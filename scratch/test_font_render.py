from PIL import Image, ImageDraw, ImageFont
import sys
sys.stdout.reconfigure(encoding='utf-8')

# Let's test rendering with quran_uthmanic_hafs.ttf
font_path = r"app\src\main\res\font\quran_uthmanic_hafs.ttf"
font = ImageFont.truetype(font_path, 40)

text = "الٓمٓ ۝١ ذَٰلِكَ"
im = Image.new('RGB', (400, 100), color=(0, 0, 0))
draw = ImageDraw.Draw(im)
# PIL uses libraqm for complex text if available, or basic
try:
    draw.text((20, 20), text, font=font, fill=(255, 255, 255), direction='rtl', language='ar')
    im.save(r'scratch\pil_test_hafs.png')
    print("Rendered with quran_uthmanic_hafs.ttf successfully!")
except Exception as e:
    print("Error rendering with direction=rtl:", e)
    draw.text((20, 20), text, font=font, fill=(255, 255, 255))
    im.save(r'scratch\pil_test_hafs.png')
    print("Rendered default!")
