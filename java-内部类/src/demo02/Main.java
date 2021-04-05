package demo02;

/**
 * @ClassName Main
 * @Description TODO
 * @Author liubo
 * @Date 2021/3/16 12:39
 */
public class Main {
    public static void main(String[] args) {
        Parcel2 parcel2 = new Parcel2();
        parcel2.ship("Tasmania");
        System.out.println("*********************************");

        /*
        其他类想使用某个类方法创建其内部类对象时，必须在内部类类型前加上：外部类.
        */
        Parcel2 parcel21 = new Parcel2();
        Parcel2.Contents contents = parcel21.contents();
        Parcel2.Destination borneo = parcel21.to("Borneo");
        System.out.println(borneo.readLabel());

    }
}
