with open('scratch/test_page.html', 'r', encoding='utf-8') as f:
    text = f.read()

idx = text.find('<g id="md-line-06"')
print(text[idx:idx+1200])
