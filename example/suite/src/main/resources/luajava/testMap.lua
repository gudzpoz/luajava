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

map     = {}
local t = {}

function map.size()

    local i = 0
    for k,v in pairs(t) do
        i = i + 1
    end

    return i
end

function map.clear()

    t = {}
end

function map.isEmpty()

    local i = 0
    for k,v in pairs(t) do
        return false
    end
    return true
end

function map.containsKey(key)

    return t[key] ~= nil
end

function map.containsValue(value)

    for k,v in pairs(t) do
        if v == value then
            return true
        end
    end
    return false
end

function map.putAll(outMap)

    local i = outMap:keySet():iterator()

    while i:hasNext() do
        local key = i:next()
        map.put(key, outMap:get(key))
    end
end

function map.get(key)

    return t[key]
end

function map.remove(key)

    local obj = t[key]
    t[key] = nil
    
    return obj
end

function map.put(key, value)

    local obj = t[key]
    
    t[key] = value
    
    return obj
end

local startTime = os.clock()
for i=1,10000 do
    map.put(i, tostring(i))
end
for i=1,10000 do
    map.put(i, tostring(i+1))
end
for i=1,10000 do
    map.put(tostring(i), i)
end
for i=1,10000 do
    map.put(tostring(i), i+1)
end
local endTime = os.clock()

map.clear()

io.write("execution time in lua " .. (endTime - startTime) .. "\n")
io.flush()
