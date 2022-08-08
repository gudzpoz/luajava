assert(Private ~= nil)
assertThrows('no matching constructor found', Private)
assert(Abstract ~= nil)
assertThrows('java.lang.InstantiationException', Abstract)
assert(Throws ~= nil)
assertThrows('java.lang.Exception', Throws)

assertThrows('no matching constructor found', Throws, 'no match')
assert(type(Throws.class) == 'userdata')

assert(Abstract:returnsNull() == nil)

Integer = java.import('java.lang.Integer')
assert(Integer:parseInt('1024') == 1024)

function errs() Integer:parseInt(1, 2, 3) end

ok, e = pcall(errs)
assert(ok == false)

function errs1() Private:privateFunc() end

ok, e = pcall(errs1)
assert(ok == false)

function errs2() Throws:throwsFunc() end

ok, e = pcall(errs2)
assert(ok == false)
