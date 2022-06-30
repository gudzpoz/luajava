package party.iroiro.luajava;

import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * An external resource loader to load external module file
 */
public interface ExternalLoader {
    /**
     * Reads an external Lua module file into a direct buffer
     *
     * @param module the module
     * @param L the Lua state requesting the module
     * @return a direct buffer containing the module file, with position at zero, limit as the length
     */
    @Nullable Buffer load(String module, Lua L);
}
