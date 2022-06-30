package party.iroiro.luajava;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.SyntaxError;
import org.jline.reader.impl.DefaultParser;
import party.iroiro.luajava.parser.LuaLexer;
import party.iroiro.luajava.parser.LuaParser;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LuaConsoleParser implements Parser {
    private final static DefaultParser DEFAULT_PARSER = new DefaultParser();

    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
        LuaLexer lexer = new LuaLexer(CharStreams.fromString(line));
        lexer.removeErrorListeners();
        LuaParser parser = new LuaParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.setErrorHandler(new DefaultErrorStrategy());
        AtomicInteger lineNumber = new AtomicInteger();
        AtomicInteger position = new AtomicInteger();
        AtomicReference<String> message = new AtomicReference<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                message.set(msg);
                lineNumber.set(line);
                position.set(charPositionInLine);
            }
        });
        LuaBracketListener bracketListener = new LuaBracketListener();
        new ParseTreeWalker().walk(bracketListener, parser.chunk());
        if (message.get() != null && message.get().contains("'<EOF>'")) {
            throw new EOFError(lineNumber.get(), position.get(), message.get(),
                    message.get(), bracketListener.brackets.get(), "");
        } else {
            return DEFAULT_PARSER.parse(line, cursor, context);
        }
    }
}
