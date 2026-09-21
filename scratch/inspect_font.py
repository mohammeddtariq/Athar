from PIL import ImageFont
import sys
sys.stdout.reconfigure(encoding='utf-8')

font_path = r"app\src\main\res\font\quran_uthmanic_hafs.ttf"
try:
    font = ImageFont.truetype(font_path, 20)
    print("Loaded font successfully with PIL!")
    print("Font name / family:", font.getname())
except Exception as e:
    print("Error:", e)
