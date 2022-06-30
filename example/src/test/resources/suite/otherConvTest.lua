assert(others ~= nil)

assert(others.i == 1)
assert(others.s == "2")
others.i = "2"
others.s = 1
assert(others.i == 1)
assert(others.s == "2")
assert(type(myuserdata) == 'userdata')
others.s = others.big
assert(others.s == "2")
others.s = myuserdata
assert(others.s == "2")
others.s = {1, 2, 3}
assert(others.s == "2")
others.s = java.import('java.lang.String')('Hello')
assert(others.s == "Hello")
others.i = nil
others.s = nil
assert(others.i == 1)
assert(others.s == nil)

assert(others.collection == nil)
others.collection = {1, 2, 3}
assert(others.collection ~= nil)
assert(others.collection:size() == 3)

assert(others.array1 == nil)
others.array1 = {1, 2, 3}
assert(others.array1 ~= nil)
assert(#(others.array1) == 3)

assert(others.array2 == nil)
others.array2 = {1, 2, 3}
assert(others.array2 == nil)

assert(others.map == nil)
others.map = {1, 2, 3}
assert(others.map ~= nil)
