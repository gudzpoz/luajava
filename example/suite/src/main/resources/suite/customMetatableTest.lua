local Integer = java.import('java.lang.Integer')
local i = Integer(1024)
local metatable = getmetatable(i)

local gc = metatable.__gc
local index = metatable.__index
local newindex = metatable.__newindex
debug.setmetatable(i, nil)

if gc ~= nil then
    assertThrows("bad argument #1 to 'gc' %(expecting a jarray/jclass/jobject%)",
                 function() gc({}) end)
    assertThrows("bad argument #1 to 'index' %(expecting a jarray/jclass/jobject%)",
                 function() index({}, '') end)
    assertThrows("bad argument #1 to 'newindex' %(expecting a jarray/jclass/jobject%)",
                 function() newindex({}, '', '') end)
else
    assertThrows("java.lang.ClassCastException",
                 function() index({}, '') end)
    assertThrows("java.lang.ClassCastException",
                 function() newindex({}, '', '') end)
end

assert('1024' == index(i, 'toString')(i))

if gc ~= nil then
    gc(i)
end
