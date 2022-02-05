package party.iroiro.jua.printproxy;

public class ObjPrint implements Printable
{
    Printable p;
    public ObjPrint()
    {
        this(null);
    }
    public ObjPrint(Printable p)
    {
        this.p = p;
    }
    public void print(String str)
    {
        if (p != null)
            p.print(str);
        else
            System.out.println("Printing from Java1..."+str);
    }

    public void print(String str, int i)
    {
        if (p != null)
            p.print(str, 1);
        else
            System.out.println("Printing from Java2..."+str);
    }
}
