-- java.import
t = java.import('party.iroiro.luajava.JuaApiLuaTest')

-- java.new
assert(type(java.new(java.import('java.lang.Object'))) == 'userdata')
i = java.new(java.import('java.lang.Integer'), '1024')
assert(type(i) == 'userdata')
assertThrows('no matching constructor found', java.new, t, 'no', 'match')

-- classIndex
assert(t.staticField == 1024)

-- Currently all non-fields are treated as methods without even trying to check if the method exists
-- For performance reason though, such a lookup would be slow.
assert(t.nonexistentField ~= nil)
assert(type(t.nonexistentField) == 'function')

assert(t.privateField ~= nil)
assert(type(t.privateField) == 'function')

-- classNewIndex
t.staticField = 100
assertThrows('java.lang.NoSuchFieldException: nonexistentField', function() t.nonexistentField = 100 end)
assertThrows('java.lang.NoSuchFieldException: privateField', function() t.privateField = 100 end)

-- classInvoke
assert(t:staticMethod() == nil)
assert(t:staticMethod(1) == 1)
assert(t:staticMethod(1, 2, 3) == 1 + 2 + 3)
assert(t:getNull() == nil)
assert(t:getVoid() == nil)

-- objectIndex: t.t is a static object

assert(t.t.s == 1024)

-- TODO: Fix this two cases
assert(t.t.nonexistentField ~= nil)
assert(type(t.t.nonexistentField) == 'function')

assert(t.t.p ~= nil)
assert(type(t.t.p) == 'function')

-- objectNewIndex
t.t.s = 100
assertThrows('java.lang.NoSuchFieldException: nonexistentField', function() t.t.nonexistentField = 100 end)
assertThrows('java.lang.NoSuchFieldException: p', function() t.t.p = 100 end)

-- objectInvoke
assert(t.t:method() == nil)
assert(jfun() == nil)
assert(juafun() == nil)

-- arrayIndex
assert(arr[1] == 1)
assert(arr[4] == 4)
assertThrows('java.lang.ArrayIndexOutOfBoundsException', function() print(arr[6]) end)

-- not arrayIndex: t.array converts from java arrays to lua tables by default
assert(type(t) == 'userdata')
assert(t.array[1] == 1)
assert(t.array[4] == 4)
assertThrows('java.lang.ArrayIndexOutOfBoundsException', function() print(t.array[6]) end)
