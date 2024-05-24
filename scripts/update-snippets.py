#!/bin/python

# python scripts/update-snippets.py docs/

import re
import sys
import typing
from pathlib import Path

hintRegex = re.compile('^\\s*<!-- @code:(\\w+) -->$')
snippetRegex = re.compile('^(\\s*)@\\[code[\\d{},\\-]* java([\\w\\d\\-,: {}]*)\\]\\(([\\./\\w]+)\\)$')

java_files: set[Path] = set()
files = sorted([
    md
    for d in Path(sys.argv[1]).glob('*/') if (
        not d.name.startswith('.')
        and d.name != 'node_modules'
        and d.name != 'build'
    )
    for md in d.glob('**/*.md')
] + list(Path(sys.argv[1]).glob('*.md')))
for n, md in enumerate(files):
    print(f'Updating {n}/{len(files)}: {md}')
    lines = md.read_text(encoding='utf-8').splitlines()
    hint_java_method: typing.Optional[str] = None
    for i in range(len(lines)):
        line = lines[i]
        assert '~~~' not in line, (line, md)
        assert '```java' not in line or '```java ignored' in line, (line, md)
        is_hint = line.strip().startswith('<!-- @code:')
        is_snippet = line.strip().startswith('@[code')
        if not is_snippet:
            if is_hint:
                hint = hintRegex.match(line)
                assert hint, (line, md)
                hint_java_method = hint.group(1)
            else:
                hint_java_method = None
            continue

        if '@[code lua]' in line:
            continue

        # replace the hint with the snippet
        snippet = snippetRegex.match(line)
        assert snippet, (line, md)
        assert hint_java_method, (line, md)
        indentation = snippet.group(1)
        extra = snippet.group(2)
        java_link = snippet.group(3)
        java_file = md.parent.joinpath(java_link)
        java_files.add(java_file.absolute())

        if hint_java_method == 'class':
            lines[i] = f'{indentation}@[code java{extra}]({java_link})'
        else:
            # find the line number range of the snippet
            start, end = -1, -1
            for j, jline in enumerate(java_file.read_text(encoding='utf-8').splitlines()):
                if hint_java_method in jline:
                    assert start == -1, (line, jline, md)
                    start = j
                elif start != -1 and end == -1:
                    if jline == '    }':
                        end = j
            assert start != -1 and end != -1, (line, md)
            lines[i] = f'{indentation}@[code{{{start + 2}-{end}}} java{extra}]({java_link})'

        hint_java_method = None

    # write the updated file
    md.write_text('\n'.join(lines).strip() + '\n', encoding='utf-8')

for java_file in java_files:
    # remove indentation inside methods so that injected snippets will not have extraneous spaces
    java_lines = java_file.read_text(encoding='utf-8').splitlines()
    for i in range(len(java_lines)):
        line = java_lines[i]
        if line.startswith(' ' * 8):
            java_lines[i] = line[8:]
    java_file.write_text('\n'.join(java_lines).strip() + '\n', encoding='utf-8')
