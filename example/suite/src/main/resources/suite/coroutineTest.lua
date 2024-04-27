local functions = {}
for i = 1, 10 do
  functions[#functions + 1] = coroutine.wrap(function(prefix)
    local i = 1
    for i = 1, 5 do
      prefix = coroutine.yield(prefix .. i)
    end
  end)
end

function assertFunctions(prefix, yields)
    for _, f in ipairs(functions) do
        assert(f(prefix) == yields)
    end
end

assert("A", "A1")
assert("B", "B2")
assert("C", "C3")
assert("D", "D4")
assert("E", "E5")
assert("F", nil)
