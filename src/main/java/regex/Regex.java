package regex;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author wuyongkang
 * @date 2022年01月20日 9:44
 */
public class Regex {
    public static void main(String[] args) {
        String str = "1231";
        boolean contains = str.contains("-");
        if (contains) {
            str = str.replace("-", "");
        }
        str = str.replaceAll("^0+(.*?([.].*?)?)0+$", "$1");
        if (str.indexOf(".") == 0) {
            str = 0 + str;
        }
        if (contains) {
            str = "-" + str;
        }
        System.out.println("xix bufg -wykgit2");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setGroupingUsed(false);
        System.out.println(nf.format(Double.parseDouble(str)));

    }
}
