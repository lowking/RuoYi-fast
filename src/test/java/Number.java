import cn.hutool.core.map.MapUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author htc
 * @date 2020-07-27
 */
public class Number {

    @Test
    public void test() {
        String str = "2767222,2888210,8882428,8883438,8884348,8884548,8884948,8887478,2455548,2455549,2466641,2467776,2469996,2477748,2479997,2488849,2433345,2433347,2434442,2438883,2444205,2444206,2444208,2444211,2444269,2444279,2444280,2341114,2344436,2344437,2344439,2411147,2416661,2430003,2111245,2111246,2144415,2144416,2146664,2304440,2324442,1944497,2028882,2044409,2077704,2077705,1704440,1749994,1784448,1800087,1844485,1844486,1848881,1540004,1542224,1555142,1628882,1644460,1644462,1644463,1648884,1666142,1666149,1458885,1466647,1466648,1472227,1473337,1483338,1493339,1494111,1496669,1444125,1444126,1444152,1444153,1444156,1444193,1444194,1277725,1288827,1425552,1427772,1119449,1148884,1193339,1202221,1222142,1222143,1117337,1115351,1115751,1113231,1113931,1066604,1075557,1084448,1099903,1099904,1024442,1035553";
        String[] strings = str.split(",");
        List<HashMap<String, Object>> result = getTopNumber(strings, 20);
        boolean isShowWT = false;
        result.forEach(n -> {
            System.out.println(new StringBuffer(String.valueOf(n.get("number"))) + " -  " + n.get("WT"));
            if (isShowWT) {
                List<Object> detail = (List<Object>) n.get("detail");
                detail.forEach(d -> {
                    System.out.println("\t\t\t\t"+d);
                });
            }
        });
    }

    private List<HashMap<String, Object>> getTopNumber(String[] numbers, int top) {
        HashMap<String, String> regWT = new HashMap<>();
        //1234 1234
        regWT.put("(\\w)(\\w)(\\w)(\\w)\\1{1}\\2{1}\\3{1}\\4{1}", "80");
        //1234 4321
        regWT.put("(\\w)(\\w)(\\w)(\\w)\\4{1}\\3{1}\\2{1}\\1{1}", "80");
        //123 123
        regWT.put("(\\w)(\\w)(\\w)\\1{1}\\2{1}\\3{1}", "60");
        //123 321
        regWT.put("(\\w)(\\w)(\\w)\\3{1}\\2{1}\\1{1}", "60");
        //12 21
        regWT.put("(\\w)(\\w)\\2{1}\\1{1}", "30");
        //12 12
        regWT.put("(\\w)(\\w)\\1{1}\\2{1}", "30");
        //1122
        regWT.put("(\\w)\\1(\\w)\\2", "30");
        //1234321
        regWT.put("(\\w)(\\w)(\\w)(\\w)\\3{1}\\2{1}\\1{1}", "70");
        //123454321
        regWT.put("(\\w)(\\w)(\\w)(\\w)(\\w)\\4{1}\\3{1}\\2{1}\\1{1}", "90");
        //aa
        regWT.put("(\\w)\\1", "5*n");
        //重复数字
        for (int i = 2; i < 10; i++) {
            String regStr = "([\\d])\\1{" + i + "}";
            regWT.put(regStr, String.valueOf(10 * (i + 1)));
        }
        List<HashMap<String, Object>> list = new ArrayList<>();
        Stream.of(numbers).forEach(s -> {
            //每个号码处理
            HashMap<String, Object> num = new HashMap<>();
            num.put("number", Long.parseLong(s));
            long wt = 0;
            List<Object> detail = new ArrayList<>();
            List<String> items;
            //匹配正则
            for (String reg : regWT.keySet()) {
                items = new ArrayList<>();
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(s);
                int count = 0;
                while (matcher.find()) {
                    //记录匹配到的
                    items.add(matcher.group(0));
                    count++;
                }
                if (count > 0) {
                    //匹配到的处理详情
                    long wtl =Long.valueOf(regWT.get(reg).replaceAll("\\*n", ""));
                    wtl = regWT.get(reg).contains("*n") ? wtl * count : wtl;
                    wt += wtl;
                    detail.add(wtl + " █ " + count + " █ " + reg);
                    detail.add(items);
                }
            }
            num.put("WT", wt);
            num.put("detail", detail);

            list.add(num);
        });

        return list.stream().sorted(Comparator.comparing(Number::comparingByWT).reversed()).limit(top).collect(Collectors.toList());
    }

    private static Long comparingByWT(HashMap<String, Object> map) {
        return MapUtil.getLong(map, "WT");
    }
}
