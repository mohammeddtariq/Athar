import re
import json

with open(r'app\src\main\assets\quran_surah_titles_vector.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Inspect coordinate range of Surah 1, 2, 3
for s in ["1", "2", "3"]:
    paths = data[s]
    all_coords = []
    for p in paths:
        # find all numbers in path
        nums = re.findall(r'[-+]?\d*\.?\d+', p)
        # pairs
        for i in range(0, len(nums)-1, 2):
            all_coords.append((float(nums[i]), float(nums[i+1])))
    xs = [c[0] for c in all_coords]
    ys = [c[1] for c in all_coords]
    print(f"Surah {s}: minX={min(xs):.1f}, maxX={max(xs):.1f}, minY={min(ys):.1f}, maxY={max(ys):.1f}, width={max(xs)-min(xs):.1f}, height={max(ys)-min(ys):.1f}")
