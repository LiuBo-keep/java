package demo02;

/**
 * @ClassName demo02.Parcel2
 * @Description TODO
 * @Author liubo
 * @Date 2021/3/16 12:33
 */
public class Parcel2 {
    class Contents {
        private int i = 11;

        public int value() {
            return i;
        }
    }

    class Destination {
        private String label;

        Destination(String whereTo) {
            this.label = whereTo;
        }

        String readLabel() {
            return label;
        }
    }

    public Destination to(String s) {
        return new Destination(s);
    }

    public Contents contents(){
        return new Contents();
    }

    public void ship(String dest){
        Contents contents = contents();
        Destination to = to(dest);
        System.out.println(to.readLabel());
    }
}
