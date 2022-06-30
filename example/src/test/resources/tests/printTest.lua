a = 'campo a';
b = 'campo b';
c = 'campo c';
tab = {
    a='tab a';
    b='tab b';
    c='tab c',
    d={ e='tab d e' }
};
function imprime (str) print(str); return 'joao', 1 end;
luaPrint = {
    implements='org.keplerproject.luajava.test.Printable',
    print=function(self, str)
      print('Printing from lua :'..str)
    end
}