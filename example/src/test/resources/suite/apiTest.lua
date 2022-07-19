--[[
 Module `java`
  ]]--
assert(java ~= nil)
methods = { 'import', 'new', 'proxy', 'luaify', 'method', 'array' }
for i = 1, #methods do
    assert(type(java[methods[i]]) == 'function')
end

--[[
  java.import(className)
  ]]--
Integer = java.import('java.lang.Integer')
assert(type(Integer) == 'userdata')
assert(java.import('java.lang.NonExistentClass') == nil)
assert(java.import('java lang Integer') == nil)
assert(java.import('java&&&&&&&&&&&&/') == nil)
-- java.import(package)
lang = java.import('java.lang.*')
assert(type(lang) == 'table')
assert(type(lang.String) == 'userdata')
assert(type(lang.Integer) == 'userdata')
assert(Integer(1024):equals(lang.Integer(1024)) == true)
-- Throws: Type: not string
assertThrows('', java.import)
assertThrows('', java.import, nil)
assertThrows('', java.import, {})
-- Nil: Type: convertible to string
assert(java.import(100) == nil)

--[[
  java.new
  ]]--
assert(type(java.new(Integer, 10)) == 'userdata')
-- Throws: Type: Not jobject nor jclass
assertThrows('', java.new, nil)
assertThrows('', java.new, 'java.lang.String')
assertThrows('', java.new, {})
assertThrows('', java.new, 100)
-- Nil: Type: jobject, but is not Class<?>
assert(java.new(Integer(1024)) == nil)
assert(java.new(Integer(1024):getClass(), 1024):equals(Integer(1024)))
-- Nil: Construction exceptions
assert(java.new(Integer, 1, 1, 1) == nil)
assert(java.new(Integer) == nil)
assert(java.new(Integer, '') == nil)

--[[
  java.proxy
  ]]--
Runnable = java.import('java.lang.Runnable')
run = { run = function() end }
assert(java.proxy(Runnable, run) ~= nil)
assert(java.proxy(Runnable.class, run) ~= nil)
assert(java.proxy('java.lang.Runnable', run) ~= nil)
assert(java.proxy(Integer, run) == nil)
assert(java.proxy(Integer(10), run) == nil)
assert(java.proxy('', run) == nil)
assert(java.proxy('', {}) == nil)
assert(java.proxy({}, {}) == nil)

--[[
  java.array
  ]]--
assertThrows('', java.array)
assertThrows('', java.array, 1)
assertThrows('', java.array, 1, 2)
assert(java.array(Integer(1), 2) == nil)
assertThrows('', java.array, Integer)
assert(java.array(java.import('java.lang.Void').TYPE, 1, 1, 1) == nil)
i = java.import('int')
array = java.array(i, 2)
assert(#array == 2)
array = java.array(i.class, 2)
assert(#array == 2)
array = java.array(i, 2, 2)
assert(#array == 2)
assert(#array[1] == 2)
assert(#array[2] == 2)
assert(java.array(i, 2, 3, {}, 4) == nil)
assert(java.array(i, 2, 3, -4) == nil)
