package file;

import java.io.File;

/**
 * @ClassName FileDemo08
 * @Description TODO
 * @Author liubo
 * @Date 2021/4/10 11:28
 */
public class FileDemo08 {

    public static void main(String[] args) {
        File file = new File("Demo");
        if (!file.exists()){
            boolean mkdir = file.mkdir();
            System.out.println(mkdir);
        }
    }
}
