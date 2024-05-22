--[[

Copyright (C) 2003-2007 Kepler Project.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

]]--

------------------------------------------------------
--    Creates a lot of objects to check if the
--    used memory keeps stable
------------------------------------------------------

local runtime = java.import('java.lang.Runtime'):getRuntime()

function doGc(should_print)
  collectgarbage()
  runtime:gc()
  collectgarbage()
  if should_print then
    k, _ = collectgarbage('count')
    print('(kB)\t\tTotal Memory\tFree Memory\tMax Memory')
    print('Lua\t\t' .. tostring(k))
    print('Java\t\t' ..
          tostring(runtime:totalMemory()/1024) .. "\t\t" ..
          tostring(runtime:freeMemory()/1024) .. "\t" ..
          tostring(runtime:maxMemory()/1024))
  end
end

local testMemory = function()

  doGc(true)

  print ('-----------------------------')
  print('testing java.new')
  for j = 1, 3 do
    for i = 1, 20000 do
        java.import('java.lang.Object')():toString()
        -- -- No java.awt.Frame on Android
        -- local s = java.import('java.awt.Frame')('test' .. i)
        -- s:toString()
    end

    doGc()
  end

  doGc(true)

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

  doGc(true)

  print ('-----------------------------')
  print('testing threads')
  for j = 1, 10 do
    for i = 1, 10000 do
      local t = coroutine.create(function()
        java.import('java.lang.Object')():toString()
      end)
      coroutine.resume(t)
      java.detach(t)
    end
    doGc()
  end

  doGc(true)
end

testMemory()

return testMemory
