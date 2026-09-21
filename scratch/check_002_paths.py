import re

with open(r'scratch\ligature-basd-svg\002.svg', 'r', encoding='utf-8') as f:
    content = f.read()

# Let's check bounding box of all paths in 002.svg
# All path 'd' attributes: let's extract min/max x, y
paths = re.findall(r'<path\s+id="([^"]+)"[^>]*d="([^"]+)"', content)
print(f"Total paths in 002.svg: {len(paths)}")

# Check groups under md-page-outer
outer_m = re.search(r'<g\s+id="md-page-outer">(.*?)</g>', content, re.DOTALL)
if outer_m:
    print("md-page-outer content length:", len(outer_m.group(1).strip()))
    print("md-page-outer content:", outer_m.group(1).strip()[:200])

# Check all lines and their types
lines = re.findall(r'<g\s+id="([^"]+)"[^>]*data-type="([^"]+)"', content)
print("Lines in 002.svg:")
for l in lines:
    print(" ", l)
