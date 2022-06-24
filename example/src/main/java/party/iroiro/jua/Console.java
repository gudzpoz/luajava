package party.iroiro.jua;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Console {
    public final static String[] VERSIONS = {
            "5.1", "5.2",
    };

    public static void main(String[] args) {
        try (Terminal terminal = TerminalBuilder.builder()
                .jansi(true)
                .nativeSignals(true)
                .build()
        ) {
            LineReader reader = LineReaderBuilder.builder()
                    .appName("lua")
                    .terminal(terminal)
                    .highlighter(LuaHighlighter.get())
                    .build();
            String version = requestLuaVersion(reader);
            startInteractive(version, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void startInteractive(String version, LineReader reader) {
        try (Lua L = getLua(version)) {
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

    private static Lua getLua(String version) {
        switch (version) {
            case "5.1":
                return new Lua51();
            case "5.2":
                return new Lua52();
            default:
                throw new RuntimeException("Unable to find matching version");
        }
    }

    private static String requestLuaVersion(LineReader reader) {
        List<String> strings = Arrays.asList(VERSIONS);
        String version;
        do {
            version = reader.readLine("Lua Version: ");
        } while (!strings.contains(version));
        return version;
    }
}
