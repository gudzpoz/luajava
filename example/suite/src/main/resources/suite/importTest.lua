AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
assert(AtomicInteger ~= nil)

out = java.import('java.lang.System').out
assert(out ~= nil)

assertThrows('java.lang.ClassNotFoundException: java.lang.NoAClass',
             java.import, 'java.lang.NoAClass')

lang = java.import('java.lang.*')
assert(type(lang) == 'table')
assert(type(lang.reflect) == 'table')
assert(type(lang.reflect.Method) == 'userdata')
-- Cached
assert(lang.reflect.Method == lang.reflect.Method)

String = lang.String
assert(String ~= nil)
assert(type(String) == 'userdata')

assertThrows("bad argument",
             function() print(lang[nil]) end)
