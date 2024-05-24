String = java.import('java.lang.String')

--         new String   ("This is the content of the String")
str = java.new(String,   'This is the content of the String')
assert(str:toString() == 'This is the content of the String')
