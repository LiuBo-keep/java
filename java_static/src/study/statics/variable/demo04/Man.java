package study.statics.variable.demo04;

/**
 * @ClassName Man
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 15:28
 */
public class Man {
    //身份证号
    int idCard;
    //性别（所有男人的性别都是“男”）
    //true表示男，false表示女
    static boolean sex = true;

    public Man(int idCard) {
        this.idCard = idCard;
    }

    //静态方法
    public static void printInfo() {
        System.out.println("-----" + (Man.sex ? "男" : "女") + "------");
    }
}
