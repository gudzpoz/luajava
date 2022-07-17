function assertThrows(fun)
    local success, _ = pcall(fun, {})
    assert(not success)
end

--[[
 Module `java`
  ]]--
assert(java ~= nil)
methods = { 'import', 'new', 'proxy', 'luaify', 'method' }
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
assertThrows(function() java.import() end)
assertThrows(function() java.import(nil) end)
assertThrows(function() java.import({}) end)
-- Nil: Type: convertible to string
assert(java.import(100) == nil)

--[[
  java.new
  ]]--
assert(type(java.new(Integer, 10)) == 'userdata')
-- Throws: Type: Not jobject nor jclass
assertThrows(function() java.new(nil) end)
assertThrows(function() java.new('java.lang.String') end)
assertThrows(function() java.new({}) end)
assertThrows(function() java.new(100) end)
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


