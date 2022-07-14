AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
assert(AtomicInteger ~= nil)

out = java.import('java.lang.System').out
assert(out ~= nil)

assert(java.import('java.lang.NoAClass') == nil)

lang = java.import('java.lang.*')
assert(type(lang) == 'table')

String = lang.String
assert(String ~= nil)
assert(type(String) == 'userdata')

assert(lang[nil] == nil)