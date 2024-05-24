AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
Constructor = java.method(AtomicInteger, 'new', 'int')
integer = Constructor(100)
compareAndSet = java.method(integer, 'compareAndSet', 'int,int')
compareAndSet(100, 200)
compareAndSet(200, 400)
assert(integer:get() == 400)

iter = java.proxy('java.util.Iterator', {
  remove = function(this)
    java.method(iter, 'java.util.Iterator:remove')()
  end
})
-- iter:remove() -- This throws an exception
