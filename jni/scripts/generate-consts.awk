BEGIN {
  print("package party.iroiro.jua;");
  print("");
  print("/**");
  print(" * Generated with <code>generate-consts.awk</code>:");
  print(" * <pre><code>awk -f jni/scripts/generate-consts.awk \\");
  print(" *     jni/luajit/src/lua.h \\");
  print(" *     jni/luajit/src/lauxlib.h \\");
  print(" *     > src/main/java/party/iroiro/jua/Consts.java</code></pre>");
  print(" */");
  print("public class Consts {");
}

/^#define\s+\w+\s+((-?(0x)?[0-9a-fA-F]+)|(\(-?(0x)?[0-9a-fA-F]+\)))$/ {
  print("    /**");
  print("     * Generated from " FILENAME " (line " NR "):");
  print("     * <code>" $0 "</code>");
  print("     */");
  print("    public static final int " $2 " = " $3";");
  print("");
}

/^#define\s+\w+\s+"[^"]+"$/ {
  print("    /**");
  print("     * Generated from " FILENAME " (line " NR "):");
  print("     * <code>" $0 "</code>");
  print("     */");
  $1 = "    public static final String";
  $2 = $2 " =";
  print($0 ";");
  print("");
}

END {
  print("}");
}