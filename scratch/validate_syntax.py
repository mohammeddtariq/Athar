import os, glob

def check_brackets(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    stack = []
    i = 0
    n = len(content)
    line = 1
    col = 1

    while i < n:
        c = content[i]
        if c == '\n':
            line += 1
            col = 1
            i += 1
            continue

        # Single line comment
        if c == '/' and i + 1 < n and content[i+1] == '/':
            while i < n and content[i] != '\n':
                i += 1
            continue

        # Multi line comment
        if c == '/' and i + 1 < n and content[i+1] == '*':
            i += 2
            while i + 1 < n and not (content[i] == '*' and content[i+1] == '/'):
                if content[i] == '\n':
                    line += 1
                i += 1
            i += 2
            continue

        # String literal with triple quotes
        if c == '"' and i + 2 < n and content[i+1] == '"' and content[i+2] == '"':
            i += 3
            while i + 2 < n and not (content[i] == '"' and content[i+1] == '"' and content[i+2] == '"'):
                if content[i] == '\n':
                    line += 1
                i += 1
            i += 3
            continue

        # Normal string literal
        if c == '"':
            i += 1
            while i < n and content[i] != '"':
                if content[i] == '\\':
                    i += 2
                    continue
                elif content[i] == '\n':
                    line += 1
                i += 1
            i += 1
            continue

        # Char literal
        if c == "'":
            i += 1
            while i < n and content[i] != "'":
                if content[i] == '\\':
                    i += 2
                    continue
                i += 1
            i += 1
            continue

        if c in '({[':
            stack.append((c, line, col))
        elif c in ')}]':
            if not stack:
                return f'Unmatched closing {c} at {line}:{col}'
            top, tl, tc = stack.pop()
            if (top == '(' and c != ')') or (top == '{' and c != '}') or (top == '[' and c != ']'):
                return f'Mismatched {top} (at {tl}:{tc}) closed by {c} at {line}:{col}'

        i += 1
        col += 1

    if stack:
        top, tl, tc = stack.pop()
        return f'Unclosed {top} from line {tl}:{tc}'
    return None

kt_files = glob.glob('app/src/main/java/**/*.kt', recursive=True)
print(f'Validating {len(kt_files)} Kotlin files...')
errors = []
for kf in kt_files:
    res = check_brackets(kf)
    if res:
        errors.append((kf, res))

if errors:
    for f, err in errors:
        print(f'ERROR in {f}: {err}')
    exit(1)
else:
    print('ALL Kotlin files have 100% BALANCED brackets and clean syntax!')
