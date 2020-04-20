package anli.demo2;

public class SetRunnable implements Runnable{

    private Student student;

    public SetRunnable(Student student) {
        this.student = student;
    }

    @Override
    public void run() {
        while (true){
            synchronized (student){
                //判断标志位
                if (student.isFlag()){
                    try {
                        //如果现在不可用写就让当前线程睡眠
                        student.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                student.setName("李四");
                student.setAge("12");

                //修改标志位
                student.setFlag(true);
                student.notify();
            }
        }
    }
}
