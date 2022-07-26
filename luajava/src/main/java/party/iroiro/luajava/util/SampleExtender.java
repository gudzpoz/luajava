package party.iroiro.luajava.util;

import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles;

@SuppressWarnings("unused")
public interface SampleExtender extends MethodHandleInfo {
    static MethodHandles.Lookup getLookup() {
        return MethodHandles.lookup();
    }
}
