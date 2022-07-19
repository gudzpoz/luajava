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