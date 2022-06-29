-- Lua values returned as is
assert(java.luaify(1) == 1)
assert(java.luaify(true) == true)
assert(java.luaify(nil) == nil)
assert(java.luaify('s') == 's')
f = function() end
t = {a = 1}
assert(java.luaify(t) == t)
java.luaify(t).a = 2
assert(t.a == 2)

assert(type(java.luaify(java.import('java.lang.String')())) == 'string')
assert(type(java.luaify(java.import('java.util.HashMap')())) == 'table')