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
compareAndSet(400, 800)
assert(integer:get() == 400)
compareAndSet = java.method(integer, 'compareAndSet', 'int,java.lang.NoSuchClass')
compareAndSet(400, 800)
assert(integer:get() == 400)

compareAndSet = java.method(integer, 'compareAndSet', 'int,int')
compareAndSet({ 400 }, { 800 })
assert(integer:get() == 400)

assert(java.method(integer, 'toString')() == '400')
assert(java.method(integer, 'toString', nil)() == '400')
assert(java.method(integer, 'toString', '')() == '400')

I = java.import('java.lang.Integer')
assert(I:parseInt('1024') == 1024)
assert(java.method(I, 'parseInt', 'java.lang.String')('1024') == 1024)

assert(java.method(I, 'new', 'int')(1024):equals(I(1024)))
assert(java.method(I, 'new', 'int')({}) == nil)
assert(java.method(I, 'new', 'double')(1024) == nil)
assert(java.method(I(1024), 'new', 'int')(1024) == nil)

-- Varargs
assertThrows('', function() String:format() end)
format = java.method(String, 'format', 'java.lang.String,java.lang.Object[]')
assert(format('%s', { 'content' }) == 'content')
assert(String:format('>>> %s', { 'content' }) == '>>> content')

