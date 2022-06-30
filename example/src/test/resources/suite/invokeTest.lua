assert(Private ~= nil)
assert(Private() == nil)
assert(Abstract ~= nil)
assert(Abstract() == nil)
assert(Throws ~= nil)
assert(Throws() == nil)

assert(Throws('no match') == nil)
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