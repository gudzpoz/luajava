assert(Static ~= nil)

for i = 1, 100 do
    Static.i = i
    assert(Static.i == i)
end

AA = java.import('party.iroiro.luajava.suite.AA')
assert(AA.B == 'B')
assert(AA.C.class:getName() == 'party.iroiro.luajava.suite.AA$C')
assertThrows('Hey d', AA.d, AA)

B = java.import('party.iroiro.luajava.suite.AA.B')
assert(B.class:getName() == 'party.iroiro.luajava.suite.AA$B')
B_method = java.method(AA, 'B')
C_method = java.method(AA, 'C')
assertThrows('Hey B', B_method)
assertThrows('Hey C', C_method)
