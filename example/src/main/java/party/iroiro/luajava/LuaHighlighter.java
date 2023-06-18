package party.iroiro.luajava;

import org.jline.builtins.SyntaxHighlighter;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;

import java.util.regex.Pattern;

public class LuaHighlighter implements Highlighter {
    private final SyntaxHighlighter highlighter;

    public static Highlighter get() {
        return new LuaHighlighter();
    }

    private LuaHighlighter() {
        highlighter = SyntaxHighlighter.build("classpath:/lua.nanorc");
    }

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        return highlighter.highlight(buffer);
    }

    @Override
    public void setErrorPattern(Pattern errorPattern) {
    }

    @Override
    public void setErrorIndex(int errorIndex) {
    }
}
