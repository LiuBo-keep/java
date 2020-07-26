package tree;

import org.junit.Test;

/**
 * @ClassName Demo
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/4 10:03
 */
public class Demo {
    public static void main(String[] args) {
      if (TreeFactory.exists()){
          TreeFactory.setSize(20);
          System.out.println(TreeFactory.getLength());
      }
    }
}
