package demo01;

/**
 * @ClassName demo01.Parcel1
 * @Description TODO
 * @Author liubo
 * @Date 2021/3/16 12:27
 */
public class Parcel1 {
    class Contents {
        private int i = 11;

        public int vlaue() {
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

    public void ship(String dests) {
        Contents contents = new Contents();
        Destination dest = new Destination(dests);
        System.out.println(dest.readLabel());
    }
}
