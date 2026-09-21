with open('scratch/test_page.html', 'r', encoding='utf-8') as f:
    text = f.read()

idx = text.find('data-type="surah-name"')
print(text[idx:idx+600])
