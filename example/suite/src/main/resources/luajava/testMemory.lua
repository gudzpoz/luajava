------------------------------------------------------
--    Creates a lot of objects to check if the
--    used memory keeps stable
------------------------------------------------------

function doGc()
  collectgarbage()
  runtime:gc()
  collectgarbage()
  k, _ = collectgarbage('count')
  print('(kB)\t\tTotal Memory\tFree Memory\tMax Memory')
  print('Lua\t\t' .. string.format('%.2f', k))
  print('Java\t\t' ..
        tostring(runtime:totalMemory()/1024) .. "\t\t" ..
        tostring(runtime:freeMemory()/1024) .. "\t" ..
        tostring(runtime:maxMemory()/1024))
end

local testMemory = function()
  local runtime = java.import('java.lang.Runtime'):getRuntime()

  doGc()

  print ('-----------------------------')
  print('testing java.new')
  for j = 1, 3 do
    for i = 1, 20000 do
        java.import('java.lang.Object')()
        local s = java.import('java.awt.Frame')('test' .. i)
        s:toString()
    end

    doGc()
  end

  print ('-----------------------------')
  print('testing java.import')
  for j = 1, 3 do
    for i = 1, 40000 do
        java.import('java.awt.BorderLayout')
    end

    doGc()
  end

  print ('-----------------------------')
  print('testing java.proxy')
  -- Proxy uses references (with luaL_ref)
  -- whose storage space is not reclaimed but reserved for later uses.
  -- So we need some more loops to see if the final memory usage stablizes.
  for j = 1, 10 do
    local t = { run = function() print('run') end }
    for i = 1, 40000 do
        java.proxy('java.lang.Runnable', t)
    end
    doGc()
  end
end

testMemory()
doGc()

return testMemory
