#!/usr/bin/env python
# coding: utf-8

# In[109]:

# python jni/scripts/jnigen-lua.py 5.1 party.iroiro.jua lua51/src/main/java/
# python jni/scripts/jnigen-lua.py 5.2 party.iroiro.jua lua52/src/main/java/
# python jni/scripts/jnigen-lua.py 5.3 party.iroiro.jua lua53/src/main/java/
# python jni/scripts/jnigen-lua.py 5.4 party.iroiro.jua lua54/src/main/java/

import requests
from lxml import html
import re
import sys
import os


# In[113]:


def getUrl(luaVersion):
    return 'https://www.lua.org/manual/' + luaVersion + '/manual.html'


def getRelative(luaVersion):
    return 'https://www.lua.org/manual/' + luaVersion + '/'


def generate(luaVersion, transformHtml):
    r = requests.get(getUrl(luaVersion))
    if r.status_code == requests.codes.ok:
        return transformHtml(luaVersion, html.fromstring(r.text))


# In[223]:


argSplit = re.compile('\\s*,\\s*')
paramSplit = re.compile('^(.+?)(\\w+)$')


def getParam(s):
    param = paramSplit.findall(s)
    if len(param) == 1:
        return list(map(lambda s: s.strip(), param[0]))
    else:
        return None


def getSignature(name, pre):
    returnType, remaining = pre.strip().split(name)
    remaining = remaining.strip()
    if remaining.startswith('(') and remaining.endswith(');'):
        remaining = remaining[1:-2]
        params = [] if remaining == 'void' else list(map(getParam, argSplit.split(remaining)))
        return {
            'return': returnType.strip(),
            'params': params,
        }
    return None


dupNewline = re.compile('(\\s+\\n\\s+)+')
javaComment = re.compile(re.escape('*/'))


def strippedPrettyPrint(luaVersion, e):
    relative = getRelative(luaVersion)
    for a in e.xpath('.//a'):
        if 'name' in a.attrib:
            del a.attrib['name']
        if 'href' in a.attrib:
            href = a.attrib['href']
            if href.startswith('#'):
                a.attrib['href'] = relative + href
            else:
                if not href.startswith(relative):
                    print(href)
    s = html.tostring(e, pretty_print=True).decode()
    return javaComment.sub('*&#47;', dupNewline.sub('\n', s))


def getDescription(luaVersion, pre):
    e = pre
    description = ''
    while e.getnext().tag not in ['hr', 'h1']:
        e = e.getnext()
        description += '\n'
        description += strippedPrettyPrint(luaVersion, e)
    return description


def getMethodInfo(luaVersion, apii):
    parent = apii.getparent()
    prev = parent.getprevious()
    pre = parent.getnext()
    info = {}
    if parent.tag == 'p' and prev.tag == 'h3' and pre.tag == 'pre':
        name = prev.text_content()
        info['name'] = name
        info['signature'] = getSignature(name, pre.text_content())
        info['description'] = getDescription(luaVersion, pre)
        info['pre'] = pre.text_content()
        return info
    else:
        return None


def transformIntoFunctionInfo(luaVersion, dom):
    functions = []
    errors = []
    for apii in dom.xpath('//span[@class="apii"]')[1:]:
        info = getMethodInfo(luaVersion, apii)
        if (info == None or info['name'] == None or info['signature'] == None
            or info['signature']['return'] == None or info['signature']['params'] == None
            or info['description'] == None or (None in info['signature']['params'])):
            errors.append(info['name'])
        else:
            info['apii'] = apii.text_content()
            if info['name'] == 'luaL_newstate':
                info['signature']['params'] = [['int', 'lid']]
            functions.append(info)
    return functions, errors


# In[238]:


def javadocQuote(des):
    return '\n'.join(map(lambda line: '     * ' + line,
                         des.strip().split('\n')))


paramTypedDescriptions = {
    'L': {'lua_State *': 'the <code>lua_State*</code> pointer'},
    'L1': {'lua_State *': 'a <code>lua_State*</code> pointer'},
    'arg': {'int': 'function argument index'},
    'b': {'int': 'boolean'},
    'ctx': {'int *': 'the context storage'},
    'data': {'int': 'data'},
    'e': {'const char *': 'field name'},
    'errfunc': {'int': '0 or the stack index of an error handler function'},
    'extra': {'int': 'extra slots'},
    'fname': {'const char *': 'the filename'},
    'from': {'lua_State *': 'a thread'},
    'fromidx': {'int': 'a stack position'},
    'funcindex': {'int': 'the stack position of the closure'},
    'funcindex1': {'int': 'the stack position of the closure'},
    'funcindex2': {'int': 'the stack position of the closure'},
    'i': {'int': 'i',
          'lua_Integer': 'i'},
    'idx': {'int': 'the stack position'},
    'index': {'int': 'the stack position of the element'},
    'index1': {'int': 'the stack position of the first element'},
    'index2': {'int': 'the stack position of the second element'},
    'isnum': {'int *': 'pointer to a boolean to be assigned'},
    'k': {'const char *': 'the field name'},
    'len': {'size_t *': 'pointer to length'},
    'level': {'int': 'the running level'},
    'lid': {'int': 'the id of the Lua state, '
            + 'to be used to identify between Java and Lua'},
    'lvl': {'int': 'the running level'},
    'msgh': {'int': 'stack position of message handler'},
    'msg': {'const char *': 'a message'},
    'n': {'int': 'the number of elements',
          'lua_Integer': 'the number of elements',
          'lua_Number': 'the number of elements',
          'lua_Unsigned': 'the value n'},
    'n1': {'int': 'n1'},
    'n2': {'int': 'n2'},
    'name': {'const char *': 'the name'},
    'narg': {'int': 'the number of arguments'},
    'nargs': {'int': 'the number of arguments that you pushed onto the stack'},
    'narr': {'int': 'the number of pre-allocated array elements'},
    'nrec': {'int': 'the number of pre-allocated non-array elements'},
    'nresults': {'int': 'the number of results, or <code>LUA_MULTRET</code>',
                 'int *': 'pointer to the number of results'},
    'nuvalue': {'int': 'number of associated Lua values (user values)'},
    'obj': {'int': 'the stack position of the object'},
    'op': {'int': 'the operator'},
    'p': {'const char *': 'the replaced sequence',
          'void *': 'the pointer',
          'const void *': 'the lightuserdata'},
    'ref': {'int': 'the reference'},
    'r': {'const char *': 'the replacing string'},
    's': {'const char *': 'the string'},
    'size': {'size_t': 'size'},
    'stat': {'int': '(I have no idea)'},
    'str': {'const char *': 'string'},
    't': {'int': 'the stack index'},
    'tname': {'const char *': 'type name'},
    'to': {'lua_State *': 'another thread'},
    'tocont': {'int': 'continue or not'},
    'toidx': {'int': 'another stack position'},
    'tp': {'int': 'type id'},
    'what': {'int': 'what'},
}


def getParamDescription(param, f):
    if 'upvalue' in f['name']:
        if param[1] == 'n':
            return 'the index in the upvalue'
    if (param[1] in paramTypedDescriptions
        and param[0] in paramTypedDescriptions[param[1]]):
        return paramTypedDescriptions[param[1]][param[0]]
    else:
        print(f['name'], param)
        return 'see description'


def replaceL(param1):
    return param1 if param1 != 'L' else 'ptr'


def javadocSignature(sig, f):
    return (
        javadocQuote('\n'.join([('@param ' + replaceL(name[1])
                                 + ' ' + getParamDescription(name, f))
                                for name in sig['params']])) + 
        (('\n' + '     * @return see description')
         if sig['return'] != 'void' else '')
    )


returnTypes = {
    'const char *': 'String',
    'void': 'void',
    'int': 'int',
    'lua_State *': 'long',
    'void *': 'long',
    'size_t': 'int',
    'lua_Integer': 'int',
    'lua_Number': 'double',
    'const void *': 'long',
    'int *': 'long',
    'lua_Unsigned': 'long',
    'const lua_Number *': 'long',
    'size_t *': 'long',
}


def javaReturnType(cType):
    return returnTypes[cType]


def javaSignature(f):
    return (
        '    protected native ' + javaReturnType(f['signature']['return'])
        + ' ' + f['name'] +
        '(' + ', '.join([javaReturnType(param[0]) + ' '
                         + replaceL(param[1]) for param
                         in f['signature']['params']]) +
        ');'
    )


def indent(s):
    return '\n'.join(map(lambda s: '        ' + s, s.strip().split('\n')))


def acceptJniType(cType):
    if javaReturnType(cType) == 'String':
        return 'const char *'
    else:
        return 'j' + javaReturnType(cType)


def callLua(f):
    return (
        f['name'] + '(' + (', '.join([
            ('(' + param[0] + ') ' + param[1])
            for param
            in f['signature']['params']])) + ');'
    )


def callingJni(f):
    if f['signature']['return'] == 'void':
        return callLua(f)
    else:
        t = acceptJniType(f['signature']['return'])
        acceptJniType(f['signature']['return'])
        return (
            t + ' returnValueReceiver = (' + t + ') ' + callLua(f) + '\n' +
            'return ' + ('env->NewStringUTF(returnValueReceiver)'
                         if t == 'const char *' else 'returnValueReceiver')
            + ';'
        )


def jniStatePointerConv(f):
    s = ''
    for param in f['signature']['params']:
        if param[0] == 'lua_State *' and param[1] == 'L':
            s += (
                'lua_State * L = (lua_State *) ptr;\n' +
                'updateJNIEnv(env, L);\n'
            )
        elif param[0] == 'lua_State *':
            s += 'updateJNIEnv(env, (lua_State *) ' + param[1] + ');\n'
    return s


def jniGen(f):
    if f['name'] == 'luaL_newstate':
        return indent(
            'lua_State* L = luaL_newstate();\n' +
            'luaJavaSetup(L, env, lid);\n' +
            'return (jlong) L;'
        )
    else:
        return indent(
            jniStatePointerConv(f) + '\n' +
            callingJni(f)
        )


def formatJavadoc(luaVersion, f):
    return (
        '    /**\n' +
        '     * Wrapper of <a href="' + getRelative(luaVersion)
        + '#' + f['name'] + '"><code>' + f['name'] + '</code></a>\n' +
        '     *\n' + javadocQuote('<pre><code>\n' + f['apii']
                                  + '\n</code></pre>') + '\n' +
        '     *\n' + javadocQuote('<pre><code>\n' + f['pre']
                                  + '\n</code></pre>') + '\n' +
        '     *\n' + javadocQuote(f['description']) + '\n' +
        '     *\n' + javadocSignature(f['signature'], f) + '\n' +
        '     */\n' + javaSignature(f) + ' /*\n' + jniGen(f) + '\n    */\n'
    )


def functionHas(f, noGo):
    if noGo in f['signature']['return']:
        return True
    for param in f['signature']['params']:
        if noGo in param[0] or noGo in param[1]:
            return True
    return False


filtered = [
    'lua_pushlstring',
    'lua_tolstring',
    'lua_call',
    'luaL_argcheck',
    'luaL_argerror',
    'luaL_dofile',
    'luaL_loadbuffer',
    'luaL_loadbufferx',
    'luaL_loadfile',
    'luaL_loadfilex',
    'lua_pushliteral',
]


def filterFunction(f):
    if (
        functionHas(f, 'lua_CFunction')
        or functionHas(f, 'lua_Reader')
        or functionHas(f, 'lua_Writer')
        or functionHas(f, 'lua_Alloc')
        or functionHas(f, 'luaL_Buffer')
        or functionHas(f, 'luaL_Reg')
        or functionHas(f, 'lua_Debug')
        or functionHas(f, 'lua_Hook')
        or functionHas(f, 'lua_KFunction')
        or functionHas(f, 'lua_WarnFunction')
        or functionHas(f, 'fmt')
        or f['name'] in filtered
        or f['name'].startswith('luaL_check')
        or f['name'].startswith('luaL_opt')
        or f['name'].startswith('luaL_arg')
    ):
        return False
    else:
        return True


def getWhole(luaVersion, package):
    functions, errors = generate(luaVersion, transformIntoFunctionInfo)
    inner = '@SuppressWarnings("unused")\n'
    className = 'Lua' + luaVersion.replace('.', '') + 'Natives'
    inner += 'public class ' + className + ' extends LuaNative {\n'
    inner += """        /*JNI
            #include "luacustomamalg.h"

            #include "lua.hpp"
            #include "jni.h"

            #include "luacomp.h"

            #include "jua.h"
            #include "juaapi.h"
            #include "jualib.h"
            #include "juaamalg.h"

            #include "luacustom.h"
         */
    """
    inner += (
        '\n    private final static AtomicBoolean loaded = '
        + 'new AtomicBoolean(false);\n\n' +
        '    protected ' + className + '() '
        + 'throws IllegalStateException {\n' +
        '        synchronized (loaded) {\n' +
        '            if (loaded.get()) { return; }\n' +
        '            try {\n' +
        '                new SharedLibraryLoader().load("'
        + 'lua' + luaVersion.replace('.', '') + '");\n' +
        '                initBindings();\n' +
        '                loaded.set(true);\n' +
        '            } catch (Throwable e) {\n' +
        '                throw new IllegalStateException(e);\n' +
        '            }\n' +
        '        }\n' +
        '    }\n\n'
    )
    inner += (
        '    private native static void initBindings() throws Exception; /*\n' +
        '        if (initLua'
        + luaVersion.replace('.', '') + 'Bindings(env) != 0) {\n' +
        '            // Java-side exceptions are not cleared if any\n' +
        '            return;\n' +
        '        }\n' +
        '    */\n\n'
    )
    inner += """
    /**
     * Open a library indivisually, alternative to <code>luaL_openlibs</code>
     *
     * @param ptr the lua state pointer
     * @param lib the library name
     */
    protected native void luaJ_openlib(long ptr, String lib); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib_comp(L, lib);
    */
    """ + '\n\n'
    for f in functions:
        if filterFunction(f):
            inner += formatJavadoc(luaVersion, f) + '\n\n'
        else:
            errors.append(f['name'])
    inner += '}'
    comment = (
        'package ' + package + ';\n\n' +
        'import java.util.concurrent.atomic.AtomicBoolean;\n' +
        'import com.badlogic.gdx.utils.SharedLibraryLoader;\n\n' +
        '/**\n' +
        ' * Lua C API wrappers\n' +
        ' *\n' +
        ' * <p>\n' +
        ' * This file is programmatically generated from <a href="'
        + getUrl(luaVersion) + '">the Lua ' + luaVersion
        + ' Reference Manual</a>.\n' +
        ' * </p>\n' +
        ' * <p>\n' +
        ' * The following functions are excluded:\n' +
        ' * <ul>\n' +
        ('\n'.join(map(lambda name: ' * <li><code>' + name + '</code></li>',
                       sorted(errors)))) + '\n' +
        ' * </ul>\n' +
        ' */'
    )
    output = comment + '\n' + inner
    return className, output


# In[240]:


if len(sys.argv) != 4:
    print('Usage: ' + sys.argv[0] + ' luaVersion javaPackage outputFolder')
else:
    name, output = getWhole(sys.argv[1], sys.argv[2])
    directory = os.path.join(sys.argv[3], *(sys.argv[2].split('.')))
    out = os.path.join(directory, name + '.java')
    if input('Writing to ' + out + ', continue? (yes/no) ') == 'yes':
        os.makedirs(directory, exist_ok=True)
        f = open(out, 'w')
        f.write(output)
        f.close()
        print('Written')
    else:
        print('Cancelled')
