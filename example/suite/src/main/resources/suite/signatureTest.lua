name = 'java.lang.String'
String = java.import(name)
s = String('Hello World')
m = java.method(s, 'getClass')
assert(type(m) == 'function')
c = m()
assert(type(c) == 'userdata')
n = java.method(c, 'getName')
assert(type(n) == 'function')
assert(n() == name)

AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
integer = AtomicInteger(100)
compareAndSet = java.method(integer, 'compareAndSet', 'int,int')
compareAndSet(100, 200)
compareAndSet(200, 400)
assert(integer:get() == 400)

compareAndSet = java.method(integer, 'compareAndSet', 'double,double')
assertThrows('no matching method found', compareAndSet, 400, 800)
assert(integer:get() == 400)
compareAndSet = java.method(integer, 'compareAndSet', 'int,java.lang.NoSuchClass')
assertThrows('no matching method found', compareAndSet, 400, 800)
assert(integer:get() == 400)

compareAndSet = java.method(integer, 'compareAndSet', 'int,int')
assertThrows('no matching method found', compareAndSet, { 400 }, { 800 })
assert(integer:get() == 400)

assert(java.method(integer, 'toString')() == '400')
assert(java.method(integer, 'toString', nil)() == '400')
assert(java.method(integer, 'toString', '')() == '400')

I = java.import('java.lang.Integer')
assert(I:parseInt('1024') == 1024)
assert(java.method(I, 'parseInt', 'java.lang.String')('1024') == 1024)

assert(java.method(I, 'new', 'int')(1024):equals(I(1024)))
assertThrows('no matching constructor found', java.method(I, 'new', 'int'), {})
assertThrows('no matching constructor found', java.method(I, 'new', 'double'), 1024)
assertThrows('bad argument to constructor',
             java.method(I(1024), 'new', 'int'), 1024)

-- Varargs
assertThrows('no matching method found', String.format, String)
format = java.method(String, 'format', 'java.lang.String,java.lang.Object[]')
assert(format('%s', { 'content' }) == 'content')
assert(String:format('>>> %s', { 'content' }) == '>>> content')

