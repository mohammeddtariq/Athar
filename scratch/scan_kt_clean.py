import os
import re

kt_files = []
for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            kt_files.append(os.path.join(root, file))

issues = []
for fpath in kt_files:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    if 'QuranSvgRepository' in content:
        issues.append(f"QuranSvgRepository found in {fpath}")
    if 'MushafPageView' in content:
        issues.append(f"MushafPageView found in {fpath}")
    if 'BismillahDivider' in content:
        issues.append(f"BismillahDivider found in {fpath}")

print("Issues found:", issues if issues else "None! All clean.")
