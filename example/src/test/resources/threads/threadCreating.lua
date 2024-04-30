AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
count = AtomicInteger(0)

Thread = java.import('java.lang.Thread')
run = java.proxy('java.lang.Runnable', {
    run = function()
        for i = 1, 30 do
            count:incrementAndGet()
            Thread:sleep(1)
        end
    end
})
threads = {}
for i = 1, 30 do
    t = Thread(run)
    threads[#threads + 1] = t
    t:setName("LuaCreated" .. i)
    t:start()
end

for _, t in ipairs(threads) do
    while t:isAlive() do
        -- Thread:sleep(...) or t:join() here dead locks!
        coroutine.yield()
    end
end

assert(count:get() == 30 * 30)
