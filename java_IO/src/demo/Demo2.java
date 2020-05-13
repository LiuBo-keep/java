package demo;

import java.io.File;

/**
 * @ClassName Demo2
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/12 20:38
 */

//递归删除多级文件
public class Demo2 {
    public static void main(String[] args) {
        File file = new File("11");

        del(file);
    }

    private static void del(File file) {
        //每次的文件路径
        System.out.println(file.getAbsolutePath());

        //结束条件
        if (null==file&&!file.exists()){
            return;
        }

        //循环删除
        File[] files = file.listFiles();
        for (File f:files
             ) {
            System.out.println(f);
            if (f.isDirectory()){
                del(f);
            }
        }
    }
}
