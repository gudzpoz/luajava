-- java.require
APITest = java.require('party/iroiro/jua/APITest')
System = java.require('java/lang/System')
Thread = java.require('java/lang/Thread')
Class = java.require('java/lang/Class')
Short = java.require('java/lang/Short')
APITest:assertTrue(Class ~= nil)

-- static memebers
Thread:sleep(1000)
APITest:assertTrue(true)
System.out:println('Print')

-- static fields
APITest:assertTrue(Short.MAX_VALUE == 0x7fff)

-- java.new
instance = java.new(APITest)
class = instance:getClass()
APITest:assertTrue(class:equals(APITest))
APITest:assertTrue(instance.testPublic == 443)

-- array
a = java.luaify(APITest.array)
b = java.luaify(APITest.arrays)
APITest:assertTrue(type(a) == 'table')
APITest:assertTrue(type(b) == 'table')
total = 0
for i, v in ipairs(a) do
  total = v + total
end
APITest:assertTrue(total == sum)

total = 0
for i, v in ipairs(b) do
  for j, u in ipairs(v) do
    total = u + total
  end
end
error = sum * #b - total
APITest:assertTrue(#a == #b)
APITest:assertTrue(error * error < 0.000000001)
if (error ~= 0) then
  System.out:println(string.format("Calculation Error: %.20f", error))
end
