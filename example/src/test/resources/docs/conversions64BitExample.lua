POW_2_60 = 1152921504606846976
Long = java.import('java.lang.Long')
l = Long(POW_2_60 + 1)
assert(l:toString() == "1152921504606846977")
assert(l:longValue() == 1152921504606846977)
