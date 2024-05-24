package party.iroiro.luajava.docs;

import party.iroiro.luajava.Lua;

@SuppressWarnings("unused")
public class JavaSideExampleModule {
    public static int open(Lua L) {
L.createTable(0, 1);
L.push(l -> {
    l.push(1024);
    return 1;
});
L.setField(-2, "getNumber");
return 1;
    }
}
