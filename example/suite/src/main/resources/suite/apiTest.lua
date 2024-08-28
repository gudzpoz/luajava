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
assertThrows('java.lang.ClassNotFoundException: java.lang.NonExistentClass',
             java.import, 'java.lang.NonExistentClass')
-- Message is "java.lang.ClassNotFoundException: Invalid name: blah blah" on Android
assertThrows('java.lang.ClassNotFoundException: ',
             java.import, 'java lang Integer')
-- Message is "java.lang.ClassNotFoundException: Invalid name: java&&&&&&&&&&&&/" on Android
assertThrows('java.lang.ClassNotFoundException: ',
             java.import, 'java&&&&&&&&&&&&/')
-- java.import(package)
lang = java.import('java.lang.*')
assert(type(lang) == 'table')
assert(type(lang.String) == 'userdata')
assert(type(lang.Integer) == 'userdata')
assert(Integer(1024):equals(lang.Integer(1024)) == true)
-- Throws: Type: not string
assertThrows('bad argument', java.import)
assertThrows('bad argument', java.import, nil)
assertThrows('bad argument', java.import, {})
-- Nil: Type: convertible to string
assertThrows('java.lang.ClassNotFoundException: 100',
             java.import, 100)

-- Other tests
System = java.import('java.lang.System')
Thread = java.import('java.lang.Thread')
Class = java.import('java.lang.Class')
Short = java.import('java.lang.Short')
-- Static method
assert(1024 == Short:valueOf(1024))
assertThrows('bad argument #1', Short.valueOf, 1024)
-- Static field
assert(type(System.out) == 'userdata')
assert(type(System.out:getClass()) == 'userdata')

--[[
  java.new
  ]]--
assert(type(java.new(Integer, 10)) == 'userdata')
-- Throws: Type: Not jobject nor jclass
assertThrows('bad argument #1 to \'java.new\': __jclass__ or __jobject__ expected', java.new, nil)
assertThrows('bad argument #1 to \'java.new\': __jclass__ or __jobject__ expected', java.new, 'java.lang.String')
assertThrows('bad argument #1 to \'java.new\': __jclass__ or __jobject__ expected', java.new, {})
assertThrows('bad argument #1 to \'java.new\': __jclass__ or __jobject__ expected', java.new, 100)
-- Nil: Type: jobject, but is not Class<?>
assertThrows('bad argument #1', java.new, Integer(1024))
assert(java.new(Integer(1024):getClass(), 1024):equals(Integer(1024)))
-- Nil: Construction exceptions
assertThrows('no matching constructor found', java.new, Integer, 1, 1, 1)
assertThrows('no matching constructor found', java.new, Integer)
assertThrows('java.lang.NumberFormatException', java.new, Integer, '')

--[[
  java.proxy
  ]]--
Runnable = java.import('java.lang.Runnable')
run = { run = function() end }
assert(java.proxy(Runnable, run) ~= nil)
assert(java.proxy(Runnable.class, run) ~= nil)
assert(java.proxy('java.lang.Runnable', run) ~= nil)
assertThrows('bad argument #1 to', java.proxy, Integer, run)
assertThrows('bad argument #1 to', java.proxy, Integer(10), run)
assertThrows('(expecting an interface)', java.proxy, '', run)
assertThrows('(expecting an interface)', java.proxy, '', {})
assertThrows('(expecting an interface)', java.proxy, {}, {})

--[[
  java.array
  ]]--
assertThrows('bad argument #1 to \'java.array\':', java.array)
assertThrows('bad argument #1 to \'java.array\':', java.array, 1)
assertThrows('bad argument #1 to \'java.array\':', java.array, 1, 2)
assertThrows('bad argument #1 to \'java.array\'', java.array, Integer(1), 2)
assertThrows('bad argument', java.array, Integer)
assertThrows('bad argument', java.array, java.import('java.lang.Void').TYPE, 1, 1, 1)
i = java.import('int')
array = java.array(i, 2)
assert(#array == 2)
array = java.array(i.class, 2)
assert(#array == 2)
array = java.array(i, 2, 2)
assert(#array == 2)
assert(#array[1] == 2)
assert(#array[2] == 2)
assertThrows('bad argument #4', java.array, i, 2, 3, {}, 4)
assertThrows('bad argument #3', java.array, i, 2, -3, -4)

--[[
  java.detach
  ]]--
assert(currentThread ~= nil) -- Injected by the runner
assertThrows('unable to detach a main state', java.detach, currentThread)
subThread = coroutine.create(function() end)
java.detach(subThread)

--[[
  java.method
  ]]--
-- The following ensures coverage of method caching
BigInteger = java.import('java.math.BigInteger')
Constructor = java.method(BigInteger, 'new', 'java.lang.String')
integer1 = Constructor('100')
integer2 = Constructor('100')
assert(integer1:equals(integer2))
added = java.method(integer1, 'add', 'java.math.BigInteger')(integer2)
added = java.method(added, 'add', 'java.math.BigInteger')(added)
assert(integer:intValue() == 400)
