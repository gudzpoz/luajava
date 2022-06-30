BEGIN {
  print("package party.iroiro.luajava;");
  print("");
  print("/**");
  print(" * Generated with <code>generate-consts.awk</code>:");
  print(" * <pre><code>awk -f scripts/generate-consts.awk \\");
  print(" *     .../lua.h \\");
  print(" *     .../lauxlib.h \\");
  print(" *     &gt; .../party/iroiro/jua/...Consts.java</code></pre>");
  print(" */");
  print("public abstract class Consts {");
}

/^#define\s+\w+\s+((-?(0x)?[0-9a-fA-F]+)|(\(-?(0x)?[0-9a-fA-F]+\)))$/ {
  print("    /**");
  print("     * Generated from " FILENAME " (line " NR "):");
  print("     * <pre><code>" $0 "</code></pre>");
  print("     */");
  print("    public static final int " $2 " = " $3";");
  print("");
}

/^#define\s+\w+\s+"[^"]+"$/ {
  print("    /**");
  print("     * Generated from " FILENAME " (line " NR "):");
  print("     * <pre><code>" $0 "</code></pre>");
  print("     */");
  $1 = "    public static final String";
  $2 = $2 " =";
  print($0 ";");
  print("");
}

END {
  print("}");
}