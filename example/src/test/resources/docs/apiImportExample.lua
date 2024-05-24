lang = java.import('java.lang.*')
print(lang.System:currentTimeMillis())

R = java.import('android.R.*')
print(R.id.input)

j = java.import('java.*.*')
print(j.lang.System:currentTimeMillis())
-- Both works
j = java.import('java.*')
print(j.lang.System:currentTimeMillis())

System = java.import('java.lang.System')
print(System:currentTimeMillis())
