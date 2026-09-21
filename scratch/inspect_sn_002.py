import re

with open(r'scratch\ligature-basd-svg\002.svg', 'r', encoding='utf-8') as f:
    content = f.read()

# Extract line 06 (surah-name)
m = re.search(r'(<g\s+id="md-line-06"[^>]*>.*?</g>\s*</g>)', content, re.DOTALL)
if m:
    sn_chunk = m.group(1)
    print("Found line 06 chunk! Length:", len(sn_chunk))
    # Count paths
    paths = re.findall(r'<path[^>]+>', sn_chunk)
    print("Number of paths in surah-name:", len(paths))
    # Let's inspect bounding box or d attributes
    print("First 3 paths:")
    for p in paths[:3]:
        print(" ", p[:120])
else:
    print("Line 06 not matched with regex, let's find indices")
    idx = content.find('id="md-line-06"')
    idx2 = content.find('id="md-line-07"')
    sn_chunk = content[idx-3:idx2-2]
    print("Chunk length:", len(sn_chunk))
    paths = re.findall(r'<path[^>]+>', sn_chunk)
    print("Number of paths:", len(paths))
    for p in paths[:3]:
        print(" ", p[:120])
