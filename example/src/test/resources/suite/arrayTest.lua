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