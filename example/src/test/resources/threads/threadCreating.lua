Thread = java.import('java.lang.Thread')
run = java.proxy('java.lang.Runnable', {
    run = function()
        for i = 1, 100 do
            print('Hello!')
            java.import('java.lang.System').out:println('Hello?')
            Thread:sleep(200)
        end
    end
})
Thread(run):start()
