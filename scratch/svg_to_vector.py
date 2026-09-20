import xml.etree.ElementTree as ET

def convert_svg_to_vector(svg_path, out_xml_path):
    tree = ET.parse(svg_path)
    root = tree.getroot()

    w = root.attrib.get('width', '373')
    h = root.attrib.get('height', '39')
    viewBox = root.attrib.get('viewBox', '0 0 373 39').split()
    vw = viewBox[2]
    vh = viewBox[3]

    xml_lines = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{w}dp"',
        f'    android:height="{h}dp"',
        f'    android:viewportWidth="{vw}"',
        f'    android:viewportHeight="{vh}">'
    ]

    for p in root.iter('{http://www.w3.org/2000/svg}path'):
        d = p.attrib.get('d', '')
        fill = p.attrib.get('fill', 'none')
        stroke = p.attrib.get('stroke', '')
        if not d:
            continue
        
        attrs = [f'android:pathData="{d}"']
        if fill and fill != 'none':
            if fill.startswith('#'):
                c = '#FF' + fill[1:] if len(fill) == 7 else fill
                attrs.append(f'android:fillColor="{c}"')
            elif fill == 'white':
                attrs.append('android:fillColor="#FFFFFFFF"')
            elif fill == 'black':
                attrs.append('android:fillColor="#FF000000"')
        if stroke and stroke != 'none':
            if stroke.startswith('#'):
                c = '#FF' + stroke[1:] if len(stroke) == 7 else stroke
                attrs.append(f'android:strokeColor="{c}"')
                attrs.append('android:strokeWidth="1"')
        
        xml_lines.append('    <path ' + ' '.join(attrs) + ' />')

    xml_lines.append('</vector>')

    with open(out_xml_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(xml_lines))

    print(f'Converted {svg_path} -> {out_xml_path}, lines: {len(xml_lines)}')

convert_svg_to_vector('scratch/surahSvgBannerDark.svg', 'scratch/ic_surah_banner_dark.xml')
convert_svg_to_vector('scratch/surahSvgBanner.svg', 'scratch/ic_surah_banner_light.xml')
