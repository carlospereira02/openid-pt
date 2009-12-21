import java.applet.Applet;


public class Teste extends Applet {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int[] returns123() {
	        return new int[] { 1, 2, 3 };
	    }

	    public boolean expects321(int[] array) {
	        if (array[0] != 3 ||
	            array[1] != 2 ||
	            array[2] != 1) {
	            System.out.print("expects321: FAILED: expected [ 3, 2, 1 ], got [ ");
	            for (int i = 0; i < array.length; i++) {
	                if (i > 0)
	                    System.out.print(", ");
	                System.out.print(array[i]);
	            }
	            System.out.println(" ]");
	            return false;
	        } else {
	            System.out.println("expects321: Passed");
	            return true;
	        }
	    }

	    public int[][] returns1Through9() {
	        return new int[][] { { 1, 2, 3 },
	                             { 4, 5, 6 },
	                             { 7, 8, 9 } };
	    }

	    public boolean expects9Through1(int[][] array) {
	        if (array[0][0] != 9 ||
	            array[0][1] != 8 ||
	            array[0][2] != 7 ||
	            array[1][0] != 6 ||
	            array[1][1] != 5 ||
	            array[1][2] != 4 ||
	            array[2][0] != 3 ||
	            array[2][1] != 2 ||
	            array[2][2] != 1) {
	            System.out.println("expects9Through1: FAILED");
	            return false;
	        } else {
	            System.out.println("expects9Through1: Passed");
	            return true;
	        }
	    }

	    public String[] returnsHelloWorld() {
	        return new String[] { "Hello", "world" };
	    }

	    public void expectsString(String val) {
	        if (val == null) {
	            System.out.println("got null");
	        } else {
	            System.out.println("got non-null: " + val);
	        }
	    }
    }
