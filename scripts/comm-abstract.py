#!/bin/python

# python scripts/comm-abstract.py \
#        lua*/src/main/java/party/iroiro/luajava/*Natives*

import sys
import re


defPattern = re.compile('^protected native (\\w+) (\\w+)\\(([^)]*)\\); /\\*$')
paramPattern = re.compile('^(.+\\W)(\\w+)$')


def getSig(signature):
    params = filter(lambda param: param != '', signature.strip().split(','))
    params = list(map(lambda param: paramPattern.findall(param)[0],
                      params))
    return params


def strip(line):
    line = line.strip()
    match = defPattern.findall(line)
    return {
        'return': match[0][0],
        'name': match[0][1],
        'signature': getSig(match[0][2]),
        'line': line[:-3],
    }


def readlines(path):
    f = open(path, 'r')
    lines = f.readlines()
    f.close()
    return list(map(strip,
                    filter(lambda line: 'protected native' in line, lines)))


def hashKey(method):
    return (
        method['return'] + method['name'] +
        ''.join([param[0].strip() for param in method['signature']])
    )


files = sys.argv[1:]
contents = list(map(readlines, files))


def comm(content1, content2):
    hashSet = {}
    for method in content1:
        hashSet[hashKey(method)] = True
    output = []
    for method in content2:
        if hashKey(method) in hashSet:
            output.append(method)
    return output


common = contents[0]
for content in contents[1:]:
    common = comm(common, content)

print("""package party.iroiro.luajava;

import java.nio.Buffer;

/**
 * Generated from the common parts of <code>Lua5.[1..4]</code>
 */
@SuppressWarnings("unused")
public abstract class LuaNative {
""")
for line in sorted(map(lambda method: method['line'], common), key=lambda s: s.lower()):
    print('    %s\n' % (line.replace('native', 'abstract'),))
print('}')
