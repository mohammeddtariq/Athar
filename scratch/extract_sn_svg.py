import re

with open(r'scratch\ligature-basd-svg\002.svg', 'r', encoding='utf-8') as f:
    content = f.read()

# Extract line 06
m = re.search(r'<g\s+id="md-line-06"[^>]*>(.*?)</g>\s*</g>', content, re.DOTALL)
if m:
    sn_content = m.group(1)
    # Create a standalone SVG for line 06
    # Viewbox around the paths: x: 155..230, y: 100..125
    svg = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="150 100 80 30" width="400" height="150">
    <rect width="100%" height="100%" fill="#000000" />
    <g fill="#FFFFFF">
    {sn_content}
    </g>
    </svg>"""
    with open(r'scratch\sn_002_standalone.svg', 'w', encoding='utf-8') as out:
        out.write(svg)
    print("Created scratch/sn_002_standalone.svg!")
