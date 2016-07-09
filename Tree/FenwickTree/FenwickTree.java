import java.util.*;
import java.io.*;
import java.math.*;


public class FenwickTree {

    int[] array; 

    public FenwickTree(int size) {
        array = new int[size + 1];
    }

    public int rsq(int ind) {
        assert ind > 0;
        int sum = 0;
        while (ind > 0) {
            sum += array[ind];
            ind -= ind & (-ind);
        }

        return sum;
    }

    public int rsq(int a, int b) {
        assert b >= a && a > 0 && b > 0 && b <= size() && a <= size();

        return rsq(b) - rsq(a - 1);
    }

    public void update(int ind, int value) {
        assert ind > 0;
        while (ind < array.length) {
            array[ind] += value;
            //Extracting the portion up to the first significant one of the binary representation of 'ind' and incrementing ind by that number
            ind += ind & (-ind);
        }
    }

    public int size() {
        return array.length - 1;
    }


    public static void main(String[] args) throws Exception {

        PrintWriter out = new PrintWriter(System.out, true);
        FenwickTree ft = null;
        Scanner scanner =new Scanner(new FileInputStream(args[0]));
        String cmd = "cmp";
        while (true) {
            String[] line = scanner.nextLine().split(" ");

            if (line[0].equals("exit")) break;

            int arg1 = 0, arg2 = 0;

            if (line.length > 1) {
                arg1 = Integer.parseInt(line[1]);
            }
            if (line.length > 2) {
                arg2 = Integer.parseInt(line[2]);
            }
            if ((!line[0].equals("set") && !line[0].equals("init")) && ft == null) {
                out.println("FenwickTree not initialized");
                continue;
            }

            if (line[0].equals("init")) {
                ft = new FenwickTree(arg1);
                arg2 = Math.min(arg2, ft.size() );
                for (int i = 1; i <= ft.size(); i++) {
                    out.println(i);
                    out.print(ft.rsq(i, i) + " ");
                }
                out.println();
            }
            else if (line[0].equals("set")) {
                ft = new FenwickTree(line.length - 1);
                arg2 = Math.min(arg2, ft.size() );
                for (int i = 1; i <= line.length - 1; i++) {
                    ft.update(i, Integer.parseInt(line[i]));
                }
            }

            else if (line[0].equals("up")) {
                ft.update(arg1, arg2);
                arg2 = Math.min(arg2, ft.size() );
                for (int i = 1; i <= ft.size(); i++) {
                    out.println(i);
                    out.print(ft.rsq(i, i) + " ");
                }
                out.println();
            }
            else if (line[0].equals("rsq")) {
                arg2 = Math.min(arg2, ft.size() );
                out.printf("Sum from %d to %d = %d%n", arg1, arg2, ft.rsq(arg1, arg2));
            }
            else {
                out.println("Invalid command");
            }

        }
    }


}