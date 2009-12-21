import java.applet.Applet;
public class MethodInvocation extends Applet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void noArgMethod() {
        System.out.println("MethodInvocation.noArgMethod()");
    }
    public void someMethod(String arg) {
        System.out.println("MethodInvocation.someMethod(String " + arg + ")");
    }
    public void someMethod(int arg) {
        System.out.println("MethodInvocation.someMethod(int " + arg + ")");
    }
    public int  methodReturningInt() { return 5; }
    public String methodReturningString() { return "Hello"; }
    
}
