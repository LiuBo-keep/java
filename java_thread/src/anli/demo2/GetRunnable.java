package anli.demo2;

public class GetRunnable implements Runnable{

    private Student student;

    public GetRunnable(Student student) {
        this.student = student;
    }

    @Override
    public void run() {
        while (true){
            synchronized (student){
                //判断标志位
                if (!student.isFlag()){
                    try {
                        student.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("姓名："+student.getName());
                System.out.println("年龄："+student.getAge());

                //修改标志位
                student.setFlag(false);
                student.notify();
            }
        }
    }
}
