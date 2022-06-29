assert(Static ~= nil)

for i = 1, 100 do
    Static.i = i
    assert(Static.i == i)
end