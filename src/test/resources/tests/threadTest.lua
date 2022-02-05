require('java')

function run()
    print('test')
    local t = java.require('java.lang.Thread')
    t:sleep(100)
end

tb={run=run}