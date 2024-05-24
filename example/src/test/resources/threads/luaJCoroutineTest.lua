local f = coroutine.wrap(function()
    assert(java.import('java.lang.String')('string'):toString() == 'string')
end)

f()
