import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

with open(r'scratch\ligature-basd-svg\002.svg', 'r', encoding='utf-8') as f:
    content = f.read()

# Let's see all attributes of md-line-06
m = re.search(r'<g\s+id="md-line-06"[^>]*>(.*?)</g>\s*</g>', content, re.DOTALL)
if not m:
    # search till next line
    start = content.find('id="md-line-06"')
    end = content.find('id="md-line-07"')
    chunk = content[start:end]
else:
    chunk = m.group(0)

print("Line 06 raw length:", len(chunk))
# Print all tags and text inside
for tag in re.findall(r'<[^>]+>', chunk)[:30]:
    print(" ", tag)
