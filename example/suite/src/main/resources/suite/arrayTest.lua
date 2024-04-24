assert(arr ~= nil)

assert(#arr == 5)
for i = 1, 5 do
    assert(arr[i] == i)
    arr[i] = 100 + arr[i]
end

for i = 1, 5 do
    assert(arr[i] == i + 100)
end
assert(#arr == 5)

assertThrows('java.lang.ArrayIndexOutOfBoundsException', function() print(arr[6]) end)
assertThrows('java.lang.IllegalArgumentException', function() arr[5] = 's' end)
assert(arr[5] == 5 + 100)

i = java.import('int')
iArray = java.array(i, 2, 3, 4)
assert(iArray:getClass():isArray())
assertThrows('bad argument #1', iArray.test, 'aaa')
assertThrows('no matching method found', iArray.noSuchMethod, iArray, 1, 2, 3)
assertThrows('bad argument', function() print(iArray[{}]) end)
