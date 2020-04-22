package reflection.demo5;

/**
 * @ClassName User
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/22 23:40
 */
public class User {

    private String name;
    private String age;
    public String sex;


    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public User(String sex) {
        this.sex = sex;
        }
        public void add(){
        System.out.println("add");
        }

        private void del(){
        System.out.println("del");
        }
}
