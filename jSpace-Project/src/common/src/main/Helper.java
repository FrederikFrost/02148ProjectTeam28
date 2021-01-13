package common.src.main;

public class Helper {
    public static void printArray(String name, Object[] array, boolean trim) {
        System.out.print(name.equals("")? "" : name + ": ");
        System.out.print("[");
        for (int i = 0; i < array.length; ++i) {
            System.out.print(trim? array[i].toString().substring(0,1) : array[i]);
            if (i != array.length-1){
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void printArray(String name, Object[] array) {
        printArray(name, array, false);
    }
}
