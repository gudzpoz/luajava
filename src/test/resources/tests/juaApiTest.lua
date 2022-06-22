-- java.require
t = java.require('party/iroiro/jua/JuaApiLuaTest')

-- java.new
assert(type(java.new(java.require('java/lang/Object'))) == 'userdata')
i = java.new(java.require('java/lang/Integer'), '1024')
assert(type(i) == 'userdata')
assert(java.new(t, 'no', 'match') == nil)

-- classIndex
assert(t.staticField == 1024)

-- TODO: Fix this two cases
-- Currently all non-fields are treated as methods without even trying to check if the method exists
-- For performance reason though, such a lookup would be slow.
assert(t.nonexistentField ~= nil)
assert(type(t.nonexistentField) == 'function')

assert(t.privateField ~= nil)
assert(type(t.privateField) == 'function')

-- classNewIndex
t.staticField = 100
t.nonexistentField = 100
t.privateField = 100

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
t.t.nonexistentField = 100
t.t.p = 100

-- objectInvoke
assert(t.t:method() == nil)
assert(jfun() == nil)
assert(juafun() == nil)

-- arrayIndex
assert(arr[1] == 1)
assert(arr[4] == 4)
assert(arr[6] == nil)

-- not arrayIndex: t.array converts from java arrays to lua tables by default
-- TODO: Maybe conversions should be explicit. Probably should introduce java.toTable
assert(t.array[1] == 1)
assert(t.array[4] == 4)
assert(t.array[6] == nil)

-- arrayNewIndex
-- TODO: Implement the array system, which is not done yet

-- method with signature
-- TODO: Implement method calling with signature specified, to enhance performance

