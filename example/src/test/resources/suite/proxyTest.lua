assertThrows('bad argument #1 to \'java.proxy\'',
             java.proxy, 'java.lang.AbsolutelyNotAClass', {})
assertThrows('bad argument #1 to \'java.proxy\'',
             java.proxy, 'java.lang.String', {})
t = {
    value = 1,
    run = function(this)
        this.value = 2
    end
}
runnable = java.proxy('java.lang.Runnable', t)
assert(t.value == 1)
runnable:run()
assert(t.value == 2)

i = 10
iterImpl = {
  next = function()
    i = i - 1
    return i + 1
  end,
  hasNext = function()
    return i > 0
  end
}

iter = java.proxy('java.util.Iterator', iterImpl)
assertThrows('java.lang.UnsupportedOperationException', iter.remove, iter)
res = {}
iter:forEachRemaining(function(this, e) res[e] = true end)
assert(#res == 10)

iter2 = java.import('java.util.Iterator')(iterImpl)
assertThrows('java.lang.UnsupportedOperationException', iter2.remove, iter2)
assert(java.catched():toString() == 'java.lang.UnsupportedOperationException: remove')

assertThrows('Expecting a table and interfaces', java.import('java.util.Iterator'), 1024)

called = false
B = java.proxy('party.iroiro.luajava.suite.B', 'party.iroiro.luajava.DefaultProxyTest.A', {
  b = function(this)
    called = true
    return java.method(B, 'party.iroiro.luajava.suite.B:b')()
  end
})
assert(not called)
assert(B:b() == 3)
assert(called)

called = false
iter = java.proxy('java.util.Iterator', 'java.lang.Runnable', {
  hasNext = function(this)
    return false
  end,
  next = function(this)
    return nil
  end,
  remove = function(this)
    called = true
  end,
  run = function(this)
    java.method(iter, 'java.util.Iterator:remove')()
  end
})
assert(not called)
iter:remove()
assert(called)
assertThrows('java.lang.UnsupportedOperationException', iter.run, iter)

obj = java.proxy('party.iroiro.luajava.DefaultProxyTest.D', {
  noReturn = function()
    assert(java.method(iter, 'party.iroiro.luajava.DefaultProxyTest.D:noReturn')() == nil)
  end
})
obj:noReturn()

assertThrows('java.lang.ClassNotFoundException: party.iroiro.luajava.DefaultProxyTest.NoSuchClass',
             java.method(iter, 'party.iroiro.luajava.DefaultProxyTest.NoSuchClass:noReturn'))

Collections = java.import('java.util.Collections')
assert(2 == Collections:binarySearch({1, 2, 3, 4, 5}, 3))
assert(1 == Collections:binarySearch({1, 2, 3, 4, 5}, 2, function(this, a, b) return a - b end))
assert(3 == Collections:binarySearch({5, 4, 3, 2, 1}, 2, function(this, a, b) return b - a end))
