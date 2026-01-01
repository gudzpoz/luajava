#!/bin/python

# for i in {1..5}; do python scripts/jnigen-gradle.py lua5${i} > lua5${i}/build.gradle; done
# Don't forget to update luajit/build.gradle !

import sys

lua_version = sys.argv[1]
assert lua_version in ["lua51", "lua52", "lua53", "lua54", "lua55"]
header_dirs = {
    "lua51": "'jni/lua51/src', 'jni/lua51/etc'",
    "lua52": "'jni/lua52'",
    "lua53": "'jni/lua53'",
    "lua54": "'jni/lua54'",
    "lua55": "'jni/lua55'",
}

compat_flags = {
    "lua52": "-DLUA_COMPAT_ALL",
    "lua53": "-DLUA_COMPAT_5_2",
    "lua54": "-DLUA_COMPAT_5_3",
    "lua55": "-DLUA_COMPAT_5_3", # does not seem to be used
}

script = f"""\
plugins {{
    id 'java'
    id 'java-library'
}}

group = rootProject.group
version = rootProject.version

configurations {{
    desktopNatives {{
        canBeConsumed = true
        canBeResolved = false
    }}

    instrumentedJars {{
        canBeConsumed = true
        canBeResolved = false
        extendsFrom api, implementation, runtimeOnly
    }}
}}

dependencies {{
    api project(':luajava')
}}

apply plugin: 'com.badlogicgames.jnigen.jnigen-gradle'

jnigen {{
    sharedLibName = '{lua_version}'

    all {{{'' if lua_version not in compat_flags else f'''
        cFlags += ['{compat_flags[lua_version]}']
        cppFlags += ['{compat_flags[lua_version]}']'''}
        headerDirs = ['../jni/luajava', 'jni/mod', {header_dirs[lua_version]}]
        cppExcludes = ['jni/{lua_version}/**/*']
        cExcludes = ['jni/{lua_version}/**/*']
        libraries = ['-lm']
    }}

    addWindows(x32, x86)
    addWindows(x64, x86)
    addWindows(x64, ARM)

    addLinux(x32, x86) {{
        // TODO: report to jnigen
        compilerPrefix = 'i686-linux-gnu-'
        cFlags = cFlags.findAll {{ it != '-m32' }}
        cppFlags = cppFlags.findAll {{ it != '-m32' }}
        linkerFlags = linkerFlags.findAll {{ it != '-m32' }}
    }}
    addLinux(x64, x86)
    addLinux(x32, ARM)
    addLinux(x64, ARM)
    // TODO: Until we have a RISCV toolchain on Ubuntu.
    // addLinux(x64, RISCV)
    each({{ it.os == Linux }}) {{
        String[] linuxFlags = ['-D_FORTIFY_SOURCE=0', '-DLUA_USE_DLOPEN']
        cFlags += linuxFlags
        cppFlags += linuxFlags
    }}

    addMac(x64, x86)
    addMac(x64, ARM)
    each({{ it.os == MacOsX }}) {{
        String[] macFlags = ['-DLUA_USE_DLOPEN']
        libraries = []
        cFlags += macFlags
        cppFlags += macFlags
    }}

    addAndroid {{
        String[] androidFlags = ['-D_FORTIFY_SOURCE=1', '-DLUA_USE_DLOPEN']
        cFlags += androidFlags
        cppFlags += androidFlags
        androidApplicationMk = [
                'APP_PLATFORM := android-21',
                "APP_CFLAG :=$androidFlags",
        ]
    }}

    robovm {{
        forceLinkClasses "java.lang.Class", "java.lang.Throwable", "party.iroiro.luajava.JuaAPI"
        xcframeworkBundleIdentifier = "party.iroiro.luajava.{lua_version}"
        minIOSVersion = "11.0"
    }}
    addIOS()
}}

artifacts {{
    artifacts {{
        [
                tasks.jnigenPackageAllAndroid,
                tasks.jnigenPackageAllIOS,
                tasks.jnigenPackageAllDesktop,
        ].forEach {{ packageTask ->
            packageTask.outputs.files.forEach {{ file ->
                archives(file) {{
                    builtBy(packageTask)
                }}
            }}
        }}
    }}

    instrumentedJars(jar)
    desktopNatives(jnigenPackageAllDesktop.outputs.files.files) {{
        builtBy(jnigenPackageAllDesktop)
    }}
}}

tasks.named('jar') {{
    manifest {{
        attributes('Automatic-Module-Name': 'party.iroiro.luajava.{lua_version}')
    }}
}}
"""

print(script, end="")
