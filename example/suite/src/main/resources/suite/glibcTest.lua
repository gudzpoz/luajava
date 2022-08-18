--[[
// We have the following settings for Linux builds.
// Hopefully they work out alright.
__asm__(".symver exp,exp@GLIBC_2.2.5");
__asm__(".symver log,log@GLIBC_2.2.5");
__asm__(".symver log2,log2@GLIBC_2.2.5");
__asm__(".symver pow,pow@GLIBC_2.2.5");
  ]]--

function assertEquals(expected, actual)
  return math.abs(expected - actual) < 0.000001
end

-- pow@GLIBC_2.2.5
assert(math.pow(2, 10) == 1024)
assert(math.pow(10, 4) == 10000)
assertEquals(2.6399177953006, math.pow(1.3, 3.7))

-- exp,exp@GLIBC_2.2.5
assertEquals(148.41315910258, math.exp(5))

-- log,log@GLIBC_2.2.5
assertEquals(9.3105641912419, math.log(1000.1, 2.1))
-- log2,log2@GLIBC_2.2.5
assertEquals(10, math.log(1024, 2))
