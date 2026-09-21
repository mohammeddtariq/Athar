from PIL import Image
import numpy as np

im = Image.open(r'scratch\extracted_frame.png')
w, h = im.size
# Let's crop the center where the text is: x from 250 to 510, y from 10 to 120
center_crop = im.crop((int(w * 0.3), 0, int(w * 0.7), int(h * 0.6)))
center_crop.save(r'scratch\extracted_text.png')
print("Saved extracted_text.png, size:", center_crop.size)

# Print horizontal profile of brightness to see vertical alignment
arr = np.array(im.convert('L'))
row_sums = arr.mean(axis=1)
print("Row profile (top 50):", [int(x) for x in row_sums[:50:2]])
