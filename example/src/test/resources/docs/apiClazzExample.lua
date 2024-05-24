Integer = java.import('java.lang.Integer')
-- Accessing a static member
print(Integer.TYPE)
-- Calling a static method
print(Integer:parseInt('1024'))
-- Construct an instance
print(Integer('1024'))
-- Get a Class<Integer> instance
print(Integer.class:getName())
