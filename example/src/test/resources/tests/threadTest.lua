function run(this)
    assert(stringbuilder ~= nil)
    stringbuilder:append('test'):append('\n')
    local t = java.import('java.lang.Thread')
    t:sleep(1)
end

tb={run=run}