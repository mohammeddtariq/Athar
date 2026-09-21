with open('app/src/main/java/com/athar/app/ui/corner/QuranScreen.kt', 'r', encoding='utf-8') as f:
    lines = f.readlines()

brackets = {'{': '}', '(': ')', '[': ']'}
inv_brackets = {v: k for k, v in brackets.items()}
stack = []

for line_no, line in enumerate(lines, 1):
    # strip comments and string literals for simple balance check
    in_string = False
    in_char = False
    escaped = False
    i = 0
    while i < len(line):
        ch = line[i]
        if line[i:i+2] == '//' and not in_string and not in_char:
            break
        if ch == '"' and not in_char and not escaped:
            in_string = not in_string
        elif ch == "'" and not in_string and not escaped:
            in_char = not in_char
        elif ch == '\\' and (in_string or in_char):
            escaped = not escaped
            i += 1
            continue
        elif not in_string and not in_char:
            if ch in brackets:
                stack.append((ch, line_no))
            elif ch in inv_brackets:
                if not stack:
                    print(f"Error: unmatched {ch} at line {line_no}")
                    break
                top, top_line = stack.pop()
                if brackets[top] != ch:
                    print(f"Error: mismatched {top} from line {top_line} with {ch} at line {line_no}")
                    break
        escaped = False
        i += 1

if stack:
    print(f"Error: unclosed brackets count={len(stack)}, top={stack[-5:]}")
else:
    print("All brackets, parentheses, and braces are perfectly balanced!")
