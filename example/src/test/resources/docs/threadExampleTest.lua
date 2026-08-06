local System = java.import('java.lang.System')
local Thread = java.import('java.lang.Thread')

local thread = Thread(function()
    for _ = 0, 1000 do
      System.out:println('2')
    end
end)

thread:start()
-- The created thread must not access this Lua state now,
-- since *this thread* is still running.
-- Therefore, the calling Java code must synchronize
-- on the main state to prevent concurrent access.

for _ = 0, 1000 do
  System.out:println('1')
end
