package party.iroiro.luajava;

import org.antlr.v4.runtime.ParserRuleContext;
import party.iroiro.luajava.parser.LuaBaseListener;

import java.util.concurrent.atomic.AtomicInteger;

public class LuaBracketListener extends LuaBaseListener {
    public final AtomicInteger brackets = new AtomicInteger(0);

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
        brackets.incrementAndGet();
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
        if (ctx.exception == null) {
            brackets.decrementAndGet();
        }
    }
}
