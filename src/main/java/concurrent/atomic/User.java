package concurrent.atomic;

/**
 * @author wuyongkang
 * @date 2023年03月16日 20:39
 */
public class User {
    private int age;
    private int bar;

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getAge() {
        return age;
    }

    public User(int age, int bar) {
        this.age = age;
        this.bar = bar;
    }

    public void setAge(int age) {
        this.age = age;
    }
}