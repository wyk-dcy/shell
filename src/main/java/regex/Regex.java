package regex;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author wuyongkang
 * @date 2022年01月20日 9:44
 */
public class Regex extends AbstractQueuedSynchronizer {
    public static void main(String[] args) {
        String str = "1231";
        boolean contains = str.contains("-");
        if (contains) {
            str = str.replace("-", "");
        }
        if (str.indexOf(".") == 0) {
            str = 0 + str;
        }
        if (contains) {
            str = "-" + str;
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        System.out.println(nf.format(Double.parseDouble(str)));

    }

    private void delete() {
        String a = "l";
    }
}

