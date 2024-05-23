#!/bin/python

# python scripts/comm-abstract.py \
#   luajava/src/main/java/party/iroiro/luajava/LuaNatives.java \
#   lua5*/src/main/java/party/iroiro/luajava/lua5*/*Natives*

import dataclasses
import sys
import re
from pathlib import Path

defPattern = re.compile('^public native (\\w+) (\\w+)\\(([^)]*)\\); /\\*$')
paramPattern = re.compile('^(.+\\W)(\\w+)$')


@dataclasses.dataclass
class Method:
    returns: str
    name: str
    signature: list
    line: str


def getSig(signature: str):
    return [
        paramPattern.findall(param)[0]
        for param in signature.strip().split(',') if param != ''
    ]


def strip(line: str):
    line = line.strip()
    match = defPattern.findall(line)
    return Method(
        returns=match[0][0],
        name=match[0][1],
        signature=getSig(match[0][2]),
        line=line[:-3],
    )


def readlines(path: str):
    f = open(path, 'r')
    lines = f.readlines()
    f.close()
    return [
        strip(line)
        for line in lines if 'public native' in line
    ]


def hashKey(method: Method):
    return (
        method.returns + method.name +
        ''.join([param[0].strip() for param in method.signature])
    )


methodPattern = re.compile('^    (?:public abstract )?\\w+ (lua\\w+|getRegistryIndex)\\([ A-Za-z0-9,]*\\);$')

def extract_documentation(file: Path):
    lines = file.read_text().splitlines()
    documentation: dict[str, str] = {}
    prev_lines: list[str] = []
    for line in lines:
        if '/**' in line:
            assert line.endswith('/**')
            prev_lines = [line]
            continue
        if '*/' in line:
            assert line.endswith('*/')
        match = methodPattern.match(line)
        if match is None:
            prev_lines.append(line)
            continue
        method = match.group(1)
        assert method == 'luaJ_pcall' or method not in documentation, method
        documentation[method] = '\n'.join(prev_lines)
        prev_lines = []
    return documentation


original = Path(sys.argv[1])
documentation = extract_documentation(original)

files = sys.argv[2:]
contents = list(map(readlines, files))


def comm(content1: list[Method], content2: list[Method]) -> list[Method]:
    hashSet = {}
    for method in content1:
        hashSet[hashKey(method)] = True
    output: list[Method] = []
    for method in content2:
        if hashKey(method) in hashSet:
            output.append(method)
    return output


common = contents[0]
for content in contents[1:]:
    common = comm(common, content)

print("""/*
 * Copyright (C) 2022 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package party.iroiro.luajava;

import java.nio.Buffer;

/**
 * Generated from the common parts of <code>Lua5.[1..4]</code>
 *
 * <p>
 * The following documentation is copied from that of Lua 5.1.
 * There might be inconsistencies between versions. So please check the official
 * Lua documentation to confirm.
 * </p>
 */
public interface LuaNatives {

    /**
     * Exposes the symbols in the natives to external libraries.
     *
     * <p>
     *     Users are only allowed load one instance of natives if they want it global.
     *     Otherwise, the JVM might just crash due to identical symbol names in different binaries.
     * </p>
     */
    void loadAsGlobal();
""")
for line, method in sorted(
    [(m.line, m) for m in common],
    key=lambda s: s[0].replace('_', '').lower(),
):
    if method.name in ['luaL_gsub']:
        continue
    print(f"""{documentation.get(method.name, '')}
    {line.replace('public native ', '')}
""")
print('}')
