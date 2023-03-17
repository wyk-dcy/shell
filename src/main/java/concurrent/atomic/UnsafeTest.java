package concurrent.atomic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author wuyongkang
 * @date 2023年03月16日 20:39
 */
public class UnsafeTest {
    public static void main(String[] args) throws NoSuchFieldException {
       /* User user=new User();
        long fieldOffset = unsafe.objectFieldOffset(User.class.getDeclaredField("age"));
        System.out.println("offset:"+fieldOffset);
        unsafe.putInt(user,fieldOffset,20);
        System.out.println("age:"+unsafe.getInt(user,fieldOffset));
        System.out.println("age:"+user.getAge());*/
    }

    public static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        //Field unsafeField = Unsafe.class.getDeclaredFields()[0]; //也可以这样，作用相同
        unsafeField.setAccessible(true);
        Unsafe unsafe =(Unsafe) unsafeField.get(null);
        return unsafe;
    }
}