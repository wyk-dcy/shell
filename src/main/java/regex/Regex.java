package regex;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author wuyongkang
 * @date 2022年01月20日 9:44
 */
public class Regex {
    public static void main(String[] args) {
        String b = "c";
        String str = "1231";
        boolean contains = str.contains("-");
        if (contains) {
            b = b.replace("-", "");
        }
        b = str.replaceAll("^0+(.*?([.].*?)?)0+$", "$1");
        if (str.indexOf(".") == 0) {
            str = 0 + str;
        }
        if (contains) {
            str = "-" + str;
        }
        System.out.println("xix bufg -wykgit2");
        NumberFormat nf = NumberFormat.getNumberInstance();
        NumberFormat nf2 = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setGroupingUsed(false);
        NumberFormat nf3 = NumberFormat.getNumberInstance();
        System.out.println(nf.format(Double.parseDouble(str)));
    }
    private void add(){
        String k = "哪门子爱好检索";

        System.out.println("git1-fun");

    }
}
