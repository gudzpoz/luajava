package party.iroiro.jua;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Console {
    public static void main(String[] args) {
        try (Terminal terminal = TerminalBuilder.builder()
                .jansi(true)
                .nativeSignals(true)
                .build();
             Lua L = new Lua51()
        ) {
            L.openLibraries();
            LineReader reader = LineReaderBuilder.builder()
                    .appName("lua")
                    .terminal(terminal)
                    .highlighter(LuaHighlighter.get())
                    .build();
            reader.printAbove(Consts.LUA_RELEASE + '\t' + Consts.LUA_COPYRIGHT);
            while (true) {
                String s;
                try {
                    s = reader.readLine(">>> ");
                } catch (EndOfFileException ignored) {
                    break;
                } catch (UserInterruptException ignored) {
                    reader.printAbove("UserInterrupt");
                    continue;
                }
                if (L.run(s) != Lua.LuaError.NONE) {
                    if (L.getTop() != 0 && L.isString(-1)) {
                        reader.printAbove(L.toString(-1));
                    }
                    L.setTop(0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
