import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

for p in ['002', '569']:
    with open(rf'scratch\ligature-basd-svg\{p}.svg', 'r', encoding='utf-8') as f:
        content = f.read()

    print(f"=== Structural Groups in {p}.svg ===")
    groups = re.findall(r'<g\s+id="([^"]+)"([^>]*)>', content)
    for g in groups:
        gid = g[0]
        if not any(gid.startswith(x) for x in ['md-word-', 'md-ligature-', 'md-diacritic-', 'md-ornament-', 'md-number-', 'md-aya-mark-']):
            print(f"  id='{gid}' attr='{g[1].strip()}'")
