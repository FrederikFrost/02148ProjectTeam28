package common.src.main;

import java.util.ArrayList;

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

    public static ArrayList<Integer> cleanCast(Object obj){
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (obj instanceof ArrayList<?>) {
            ArrayList<?> al = (ArrayList<?>) obj;
            if (al.size() > 0) {
                for (int i = 0; i < al.size(); i++) {
                    Object o = al.get(i);
                    if (o instanceof Integer) {
                        Integer v = (Integer) o;
                        result.add(v);
                    }  
                }
            }
        }
        return result;//
    }

}
