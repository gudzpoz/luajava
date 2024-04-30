local f = coroutine.wrap(function()
    java.import('java.lang.System').out:println("Hello Coroutines!!!")
end)

f()
