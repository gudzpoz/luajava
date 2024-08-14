#!/bin/python

# python scripts/update-snippets.py \
#   docs/ \
#   example/src/test/java/party/iroiro/luajava/docs/LuaCodeExampleTest.java

import re
import sys
import typing
from pathlib import Path

snippetRegex = re.compile(
    '^(\\s*)<<< (.+?\\.(java|lua))(#\\w+)?(\\{[\\d\\-,]+\\})?( \\[|$)',
)

java_files: set[Path] = set()
lua_files: set[Path] = set()
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
        # All Java/Lua code must be quoted with snippets,
        # so that we can test them and ensure they are up-to-date.
        assert '~~~' not in line, (line, md)
        assert (
            ('```java' not in line or '```java ignored' in line)
            and ('```lua' not in line or '```lua ignored' in line)
        ), (line, md)
        is_snippet = line.strip().startswith('<<<')
        if not is_snippet or line.endswith('Dockerfile'):
            continue

        # replace the hint with the snippet
        snippet = snippetRegex.match(line)
        assert snippet, (line, md)
        indentation = snippet.group(1)
        link = snippet.group(2)
        lang = snippet.group(3)
        frag = snippet.group(4)
        highlight = snippet.group(5)
        end = snippet.group(6)
        assert lang == 'java' or lang == 'lua'
        code_file = md.parent.joinpath(link)

        if lang == 'java':
            java_files.add(code_file.absolute())
        else:
            lua_files.add(code_file.absolute())


for java_file in java_files:
    # remove indentation inside methods so that injected snippets will not have extraneous spaces
    java_lines = java_file.read_text(encoding='utf-8').splitlines()
    for i in range(len(java_lines)):
        line = java_lines[i]
        if line.startswith(' ' * 8):
            java_lines[i] = line[8:]
    java_file.write_text('\n'.join(java_lines).strip() + '\n', encoding='utf-8')


lua_tester = Path(sys.argv[2])
tester_lines = set(l.strip() for l in lua_tester.read_text(encoding='utf-8').splitlines())
for lua_file in sorted(lua_files):
    if 'example/suite' in str(lua_file):
        continue
    assert f'"{lua_file.stem}",' in tester_lines, lua_file
