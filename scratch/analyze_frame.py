from PIL import Image
import numpy as np

im = Image.open(r'scratch\user_screenshot_header.png')
w, h = im.size
print("Crop dimensions:", w, h)
# Find the frame bounding box
arr = np.array(im.convert('L'))
# Foreground pixels > 40
ys, xs = np.where(arr > 40)
if len(ys) > 0:
    print(f"Non-black bbox: y=[{ys.min()}, {ys.max()}], x=[{xs.min()}, {xs.max()}]")
    frame_crop = im.crop((xs.min(), ys.min(), xs.max(), ys.max()))
    frame_crop.save(r'scratch\extracted_frame.png')
    print("Saved extracted_frame.png, size:", frame_crop.size)
