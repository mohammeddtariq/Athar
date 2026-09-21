import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

with open(r'scratch\ligature-basd-svg\002.svg', 'r', encoding='utf-8') as f:
    content = f.read()

print("File size:", len(content))
svg_header = content[:500]
print("SVG Header:\n", svg_header)

# Extract all top-level / structural groups
groups = re.findall(r'<g\s+([^>]+)>', content)
print(f"Total <g> tags: {len(groups)}")
for g in groups[:20]:
    print("  <g", g)

# Look for fill colors or stroke colors
fills = set(re.findall(r'fill="([^"]+)"', content))
print("Fills:", fills)
strokes = set(re.findall(r'stroke="([^"]+)"', content))
print("Strokes:", strokes)
styles = set(re.findall(r'style="([^"]+)"', content))
print("Styles (sample 5):", list(styles)[:5])
