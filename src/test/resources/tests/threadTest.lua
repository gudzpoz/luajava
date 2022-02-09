function run()
    local o = java.require('java/lang/System')
    o.out:println('test')
    local t = java.require('java/lang/Thread')
    t:sleep(100)
end

tb={run=run}