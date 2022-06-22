package party.iroiro.jua;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Console {
    public static void main(String[] args) throws IOException {
        try (Terminal terminal = TerminalBuilder.builder()
                .jansi(true)
                .nativeSignals(true)
                .build();
             Jua L = new Jua()
        ) {
            LineReader reader = LineReaderBuilder.builder()
                    .appName("luajit")
                    .terminal(terminal)
                    .highlighter(LuaHighlighter.get())
                    .build();
            reader.printAbove(Consts.LUA_RELEASE);
            reader.printAbove(Consts.LUA_AUTHORS);
            reader.printAbove(Consts.LUA_COPYRIGHT);
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
                if (L.run(s) == 1) {
                    if (L.gettop() != 0 && L.isstring(-1)) {
                        reader.printAbove(L.toString(-1));
                    }
                    L.settop(0);
                }
            }
        }

    }
}
