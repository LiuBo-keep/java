package anli.demo2;

//资源类
public class Student {

    private String name;
    private String age;
    //标志位
    private boolean flag;//true：可以读；false：可以写

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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
