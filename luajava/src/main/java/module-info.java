module party.iroiro.luajava {
    exports party.iroiro.luajava;
    exports party.iroiro.luajava.cleaner;
    exports party.iroiro.luajava.value;
    exports party.iroiro.luajava.util to party.iroiro.luajava.jsr223lua;

    requires org.jetbrains.annotations;
    requires com.google.errorprone.annotations;
}