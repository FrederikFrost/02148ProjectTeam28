package common.src.main;

import java.lang.reflect.Array;
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

    public static int[] cleanCast(Object obj){
        // ArrayList<Integer> result = new ArrayList<Integer>();
        int[] result;
        if (obj instanceof ArrayList<?>) {
            ArrayList<?> al = (ArrayList<?>) obj;
            result = new int[al.size()];
            if (al.size() > 0) {
                for (int i = 0; i < al.size(); i++) {
                    Object o = al.get(i);
                    if (o instanceof Integer) {
                        int v = (int) (Integer) o;
                        result[i] = v;
                    }  
                    else if (o instanceof Double) {
                        int z = (int) ((Double) o).doubleValue();
                        result[i] = z;
                    }
                }
            }
            return result;
        }
        return new int[1];

        // ArrayList<Integer> result = new ArrayList<Integer>();
        // if (obj instanceof ArrayList<?>) {
        //     ArrayList<?> al = (ArrayList<?>) obj;
        //     if (al.size() > 0) {
        //         for (int i = 0; i < al.size(); i++) {
        //             Object o = al.get(i);
        //             if (o instanceof Integer) {
        //                 int v = (int) (Integer) o;
        //                 result.add(v);
        //             }  
        //             else if (o instanceof Double) {
        //                 int z = (int) ((Double) o).doubleValue();
        //                 result.add(z);
        //             }
        //         }
        //     }
        // }
        // return result;

        // ArrayList<Integer> res = new ArrayList<Integer>();
        // for (Object obj1 : (ArrayList<?>) obj) {
        //     res.add((int) (double) (Double) obj1);
        // }

        // return res;
    }

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = ((Integer) integers.get(i)).intValue();
        }

        return ret;
    }

//     public static Object add(final Object array, final Object obj){
//         final int length = Array.getLength(array);
//         final Object result = Array.newInstance(array.getClass().getComponentType(), length + 1);
//         System.arraycopy(array, 0, result, 0, length);
//        //add element to the new array
//        return newArray;
//    } 

    private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    public static int[] add(final int[] array, final int element) {
        final int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    // public static int[] add(final int[] array, final Object obj) {
    //         return (int[]) add((Object) array, (Object) obj);
    //     }   

    public static Object remove(final Object array, final int index) {
        final int length = Array.getLength(array);
        if (index < 0 || index >= length) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    public static int[] remove(final int[] array, final int index) {
        return (int[]) remove((Object) array, index);
    }   

    public static void appendAndSend(String msg){
        //MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: "+ msg + "\n", true);
        Menu.game.sendMessage(msg, Menu.chatHandler, true);
    }
}
