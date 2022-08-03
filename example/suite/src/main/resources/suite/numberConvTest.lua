assert(numbers ~= nil)
assert(numbers.c == 99) -- 'c'
assert(numbers.b == 1)
assert(numbers.s == 2)
assert(numbers.i == 3)
assert(numbers.l == 4)
assert(numbers.f == 5)
assert(numbers.d == 6)
assert(numbers.cc == 67) -- 'C'
assert(numbers.bb == 7)
assert(numbers.ss == 8)
assert(numbers.ii == 9)
assert(numbers.ll == 10)
assert(numbers.ff == 11)
assert(numbers.dd == 12)
numbers.c = 30
numbers.b = 31
numbers.s = 32
numbers.i = 33
numbers.l = 34
numbers.f = 35
numbers.d = 36
numbers.cc = 47
numbers.bb = 37
numbers.ss = 38
numbers.ii = 39
numbers.ll = 40
numbers.ff = 41
numbers.dd = 42
assert(numbers.c == 30)
assert(numbers.b == 31)
assert(numbers.s == 32)
assert(numbers.i == 33)
assert(numbers.l == 34)
assert(numbers.f == 35)
assert(numbers.d == 36)
assert(numbers.cc == 47)
assert(numbers.bb == 37)
assert(numbers.ss == 38)
assert(numbers.ii == 39)
assert(numbers.ll == 40)
assert(numbers.ff == 41)
assert(numbers.dd == 42)

assert(type(numbers.big) == 'userdata')

assert(numbers.bool == false)
assert(numbers.BOOL == false)
numbers.bool = true
assert(numbers.bool == true)
numbers.bool = 0
assert(numbers.bool == false)
numbers.bool = 1
assert(numbers.bool == true)
numbers.BOOL = true
assert(numbers.BOOL == true)
numbers.BOOL = 0
assert(numbers.BOOL == false)
numbers.BOOL = 1
assert(numbers.BOOL == true)

-- Incorrect access
assertThrows('java.lang.IllegalArgumentException', function() numbers.i = false end)
assert(numbers.i == 33)

assertThrows('java.lang.IllegalArgumentException', function() numbers.big = 4096 end)
assert(numbers.big:intValue() == 1024)

Integer = java.import('java.lang.Integer')
Double = java.import('java.lang.Double')
-- new Integer(1024).equals(new Double(1024))
assert(not Integer(1024):equals(1024))
-- new Double(1024).equals(new Double(1024))
assert(Double(1024):equals(1024))
