AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
i = AtomicInteger(1024)

co = coroutine.create(function()
    assert(i:get() == 1024)
end)

coroutine.resume(co)
