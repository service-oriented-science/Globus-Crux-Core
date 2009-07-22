package org.globus.crux.stateful.jcr;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * @author turtlebender
 */
public class HashcodeUtil {
    public final static int SEED = 23;
    private static final int ODD_PRIME_NUMBER = 17;

    public static int hash(int seed, boolean b) {
        return firstTerm(seed) + (b ? 1 : 0);
    }

    public static int hash(int seed, char c) {
        return firstTerm(seed) + (int) c;
    }

    public static int hash(int seed, int i) {
        return firstTerm(seed) + i;
    }

    public static int hash(int seed, long l) {
        return firstTerm(seed) + (int) (l ^ (l >>> 32));
    }

    public static int hash(int seed, float f) {
        return hash(seed, Float.floatToIntBits(f));
    }

    public static int hash(int seed, double d) {
        return hash(seed, Double.doubleToLongBits(d));
    }

    public static int hash(Object o) {

        if (o.getClass().isPrimitive() | o.getClass().equals(String.class)) {
            return o.hashCode();
        }
        return hash(SEED, o);
    }

    public static int hash(int seed, Object o) {
        int result = seed;
        if (o == null) {
            result = hash(result, 0);
        } else if (!isArray(o)) {
            result = hashObject(result, o, o.getClass());
        } else {
            int length = Array.getLength(o);
            for (int idx = 0; idx < length; ++idx) {
                Object item = Array.get(o, idx);
                result = hash(result, item);
            }
        }
        return result;
    }

    public static int hashObject(int seed, Object o, Class<?> oClass) {
        Field[] fields = oClass.getFields();
        int hash = seed;
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    if (field.getType().equals(int.class)) {
                        hash += hash(hash, field.getInt(o));
                    } else if (field.getType().equals(byte.class)) {
                        hash += hash(hash, field.getByte(o));
                    } else if (field.getType().equals(char.class)) {
                        hash += hash(hash, field.getChar(o));
                    } else if (field.getType().equals(long.class)) {
                        hash += hash(hash, field.getLong(o));
                    } else if (field.getType().equals(short.class)) {
                        hash += hash(hash, field.getShort(o));
                    } else if (field.getType().equals(double.class)) {
                        hash += hash(hash, field.getDouble(o));
                    } else if (field.getType().equals(boolean.class)) {
                        hash += hash(hash, field.getBoolean(o));
                    } else if (field.getType().equals(float.class)) {
                        hash += hash(hash, field.getFloat(o));
                    }
                } else if (field.getType().equals(String.class)) {
                    hash += firstTerm(seed) + field.get(o).hashCode();
                } else {
                    hash += hash(hash, field.get(o));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (!oClass.getSuperclass().equals(Object.class)) {
            hash = hashObject(hash, o, oClass.getSuperclass());
        }
        return hash;
    }


    private static int firstTerm(int seed) {
        return ODD_PRIME_NUMBER * seed;
    }

    private static boolean isArray(Object o) {
        return o.getClass().isArray();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(HashcodeUtil.hash("hello"));
        System.out.println("hello".hashCode());
        System.out.println("hello".hashCode());
    }
}
