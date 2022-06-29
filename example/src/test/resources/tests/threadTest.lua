function run(this)
    local o = java.import('java.lang.System')
    o.out:println('test')
    local t = java.import('java.lang.Thread')
    t:sleep(3)
end

tb={run=run}