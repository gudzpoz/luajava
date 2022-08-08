package party.iroiro.luajava;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class Console {
    public final static String[] VERSIONS = {
            "5.1", "5.2", "5.3", "5.4", "jit",
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
                    .build();
            String version = requestLuaVersion(reader);
            startInteractive(version, terminal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void startInteractive(String version, Terminal terminal) {
        try (Lua L = getLua(version)) {
            LineReader reader = LineReaderBuilder.builder()
                    .appName("lua")
                    .terminal(terminal)
                    .highlighter(LuaHighlighter.get())
                    .parser(new LuaConsoleParser())
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "  > ")
                    .variable(LineReader.INDENTATION, 2)
                    .build();
            AutopairWidgets autopairWidgets = new AutopairWidgets(reader, false);
            autopairWidgets.enable();
            L.openLibraries();
            L.setExternalLoader(new ClassPathLoader());
            L.run("print('Running ' .. _VERSION)");
            while (true) {
                String s;
                try {
                    s = reader.readLine(">>> ");
                } catch (EndOfFileException ignored) {
                    break;
                } catch (UserInterruptException ignored) {
                    reader.printAbove("UserInterrupt");
                    continue;
                } catch (Throwable ignored) {
                    s = "";
                }
                synchronized (L.getMainState()) {
                    if (L.run(s) != Lua.LuaError.OK) {
                        if (L.getTop() != 0 && L.isString(-1)) {
                            reader.printAbove(L.toString(-1));
                        }
                        L.setTop(0);
                        Throwable e = L.getJavaError();
                        if (e != null) {
                            reader.printAbove("Last Java side exception:");
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            PrintStream print = new PrintStream(output);
                            e.printStackTrace(print);
                            print.flush();
                            reader.printAbove(output.toString());
                            L.error((Throwable) null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Lua getLua(String version) {
        switch (version.toLowerCase()) {
            case "5.1":
                return new Lua51();
            case "5.2":
                return new Lua52();
            case "5.3":
                return new Lua53();
            case "5.4":
                return new Lua54();
            case "jit":
                return new LuaJit();
            default:
                throw new RuntimeException("Unable to find matching version");
        }
    }

    private static String requestLuaVersion(LineReader reader) {
        List<String> strings = Arrays.asList(VERSIONS);
        String version;
        do {
            version = reader.readLine("Lua Version: ");
        } while (!strings.contains(version.toLowerCase()));
        return version;
    }
}
