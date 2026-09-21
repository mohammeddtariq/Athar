import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

with open(r'C:\Users\moham\.gemini\antigravity\brain\341310cd-f0cc-4da0-b908-318b55deb805\.system_generated\steps\3795\content.md', 'r', encoding='utf-8') as f:
    text = f.read()

svgs = re.findall(r'<svg[^>]*id="([^"]*)"', text)
print("SVGs on page 2:", svgs)

# Check if there is any frame or banner
frames = re.findall(r'class="([^"]*)"', text)
print("Classes sample:", set(frames[:20]))
