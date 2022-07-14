package party.iroiro.luajava.printproxy;

public class ObjPrint implements Printable {
    private final StringBuilder output;
    Printable p;

    public ObjPrint(StringBuilder output) {
        this(null, output);
    }

    public ObjPrint(Printable p, StringBuilder output) {
        this.output = output;
        this.p = p;
    }

    public void print(String str) {
        if (p != null) {
            p.print(str);
        } else {
            System.out.println("Printing from Java1..." + str);
            output.append("Printing from Java1...").append(str).append('\n');
        }
    }

    public void print(String str, int i) {
        if (p != null) {
            p.print(str, 1);
        } else {
            System.out.println("Printing from Java2..." + str);
            output.append("Printing from Java2...").append(str).append('\n');
        }
    }
}
