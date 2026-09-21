import zipfile
import re
from collections import Counter

zip_path = r"D:\Codespace\android-dev\ligature-basd-svg.zip"
with zipfile.ZipFile(zip_path, 'r') as z:
    for page in ['ligature-basd-svg/1.svg', 'ligature-basd-svg/2.svg', 'ligature-basd-svg/569.svg']:
        if page in z.namelist():
            data = z.read(page).decode('utf-8', errors='replace')
            viewbox = re.search(r'viewBox="([^"]*)"', data)
            tags = re.findall(r'<([a-zA-Z0-9]+)', data)
            tag_counts = Counter(tags)
            classes = re.findall(r'class="([^"]*)"', data)
            class_counts = Counter(classes)
            # Check for text or path elements
            paths = len(re.findall(r'<path', data))
            texts = len(re.findall(r'<text', data))
            print(f"=== {page} ===")
            print(f"  Length: {len(data)} bytes")
            print(f"  ViewBox: {viewbox.group(1) if viewbox else None}")
            print(f"  Tags: {dict(tag_counts.most_common(10))}")
            print(f"  Classes: {dict(class_counts.most_common(10))}")
            print(f"  Paths: {paths}, Texts: {texts}")
            # Print sample elements
            sample_elements = re.findall(r'<[a-zA-Z0-9]+[^>]{1,100}>', data)[:10]
            print("  Sample elements:")
            for elem in sample_elements:
                print("   ", elem.replace('\n', ' '))
