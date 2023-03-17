package concurrent.atomic;

/**
 * @author wuyongkang
 * @date 2023年03月16日 20:39
 */
public class User {
    private int age;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}