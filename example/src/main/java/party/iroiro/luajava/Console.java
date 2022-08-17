package party.iroiro.luajava;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;
import party.iroiro.luajava.value.LuaValueSuite;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static party.iroiro.luajava.LuaVersion.forEachTest;

public class Console {
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
            if (args.length == 0) {
                String version = requestLuaVersion(reader);
                startInteractive(version, terminal);
            } else {
                execute(reader, args);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void execute(LineReader reader, String[] args) {
        new CommandLine(new ConsoleRunner(reader)).execute(args);
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
            injectLicense(L, reader);
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

    private static void injectLicense(Lua L, LineReader reader) {
        reader.printAbove("Use license() to print licensing info.");
        L.push(l -> {
            reader.printAbove("This appliation is licensed under GPL 3.0.");
            reader.printAbove("Use license_gpl() to read full text of GPL 3.0.");
            printAll(Lua.class, reader, "/LICENSE-luajava-console");
            printAll(Lua.class, reader, "/META-INF/LICENSE-luajava");
            return 0;
        });
        L.setGlobal("license");
        L.push(l -> {
            reader.printAbove("This appliation is licensed under GPL 3.0.");
            reader.printAbove("Use license_gpl() to read full text of GPL 3.0.");
            printAll(Console.class, reader, "/gpl-3.0.txt");
            return 0;
        });
        L.setGlobal("license_gpl");
    }

    private static void printAll(Class<?> c, LineReader reader, String resource) {
        try (InputStream s = c.getResourceAsStream(resource)) {
            Scanner scanner = new Scanner(Objects.requireNonNull(s));
            while (scanner.hasNextLine()) {
                reader.printAbove(scanner.nextLine());
            }
        } catch (Exception e) {
            reader.printAbove("IOException during license reading");
        }
    }

    private static Lua getLua(String version) {
        LuaVersion luaVersion = LuaVersion.from(version.toLowerCase());
        if (luaVersion == null) {
            throw new RuntimeException("Unable to find matching version");
        } else {
            return luaVersion.supplier.get();
        }
    }

    private static String requestLuaVersion(LineReader reader) {
        String version;
        do {
            version = reader.readLine("Lua Version: ");
        } while (LuaVersion.from(version.toLowerCase()) == null);
        return version;
    }

    private static class ConsoleRunner implements Callable<Integer> {
        private static class Command {
            @CommandLine.Option(names = {"-f", "--file"}, description = "The Lua file to run")
            String file;

            @CommandLine.Option(names = {"-e", "--expr"}, description = "The Lua expression to run")
            String expression;
        }

        private final LineReader reader;

        public ConsoleRunner(LineReader reader) {
            this.reader = reader;
        }

        @CommandLine.Option(names = {"-t", "--test"}, help = true, description = "Run built-in tests")
        boolean test;

        @CommandLine.Option(names = {"-l", "--lua"}, required = true, description = "Specify the Lua version")
        String lua;

        @SuppressWarnings("DefaultAnnotationParam")
        @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
        Command command;

        @Override
        public Integer call() {
            if (test) {
                reader.printAbove("----- Running LuaTestSuite -----");
                forEachTest((L, v) -> {
                    reader.printAbove("----- Testing " + L.getClass().getSimpleName() + " -----");
                    new LuaTestSuite<>(L, v.supplier).test();
                });
                reader.printAbove("----- Running LuaScriptSuite -----");
                forEachTest((L, v) -> {
                    reader.printAbove("----- Testing " + L.getClass().getSimpleName() + " -----");
                    new LuaScriptSuite<>(L).test();
                });
                reader.printAbove("----- Running LuaValueSuite -----");
                forEachTest((L, v) -> {
                    reader.printAbove("----- Testing " + L.getClass().getSimpleName() + " -----");
                    new LuaValueSuite<>(L).test();
                });
                reader.printAbove("----- Completed -----");
            } else {
                try (Lua L = getLua(lua)) {
                    L.openLibraries();
                    L.setExternalLoader(new ClassPathLoader());
                    if (command.file != null) {
                        Path path = Paths.get(command.file);
                        byte[] bytes = Files.readAllBytes(path);
                        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
                        okOrFail(L.load(buffer.put(bytes).flip(), path.getFileName().toString()),
                                L, "loading file buffer");
                        okOrFail(L.pCall(0, Consts.LUA_MULTRET),
                                L, "running file");
                    } else if (command.expression != null) {
                        okOrFail(L.load(command.expression),
                                L, "loading expression");
                        okOrFail(L.pCall(0, Consts.LUA_MULTRET),
                                L, "running expression");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return 0;
        }

        private void okOrFail(Lua.LuaError error, Lua L, String message) {
            if (error != Lua.LuaError.OK) {
                reader.printAbove("Error " + error + " when " + message);
                throw new RuntimeException(L.toString(-1));
            }
        }
    }
}
