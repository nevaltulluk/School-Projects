import java.util.Scanner;

public class Dbms {
    public static String byteToString(byte[] bytes) {
        String returnString = "";
        for (int i = 0; i < bytes.length; i++) {
            returnString = returnString + (char)bytes[i];
        }
        return returnString;
    }

    public static int byteToInt(byte[] bytes) {
        Scanner scanner = new Scanner(byteToString(bytes));
        int ret = scanner.nextInt();
        scanner.close();
        return ret;

    }
    public static void main(String[] args) {
        byte[] bytes = {48,32,32,32};
        int a = byteToInt(bytes);
        System.out.print(a);
    }


}




