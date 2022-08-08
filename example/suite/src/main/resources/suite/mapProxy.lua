-- The sole purpose of this Map is to verify the default method `remove(key, value)`.
if not JAVA8 then
  return
end

map = java.proxy('java.util.Map', {
  containsKey = function(self, key)
    return java.unwrap(self)[key] ~= nil
  end,
  get = function(self, key)
    return java.unwrap(self)[key]
  end,
  remove = function(self, key, o)
    if o ~= nil then
      -- The default version
      return java.method(self, 'java.util.Map:remove', 'java.lang.Object,java.lang.Object')(key, o)
    end
    self = java.unwrap(self)
    local v = self[key]
    self[key] = nil
    return v
  end
})

assert(not map:remove(1, 1))
java.unwrap(map)[1] = 2
assert(not map:remove(1, 1))
assert(not map:remove(1, 3))
assert(map:remove(1, 2))
