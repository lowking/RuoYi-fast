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
        String str = "17689415477,13110871322,17689410433,17689411799,17659056876,13276017876,17689419500,17689417366,17689415321,17659058033,18649835766,13067407033,17689415955,17689255876,17689417088,17689417488,17689417233,17689251722,18695700133,18650734922,17689972567,13055740733,17659056500,17659057321,17689414022,17659054678,18695730255,18558757466,17689413233,17689414377,17689412210,17689415488,17659050200,13043545755,17689411055,17689412677,17689251622,17659056099,17689970155,17689971233,17689416200,17659050987,13290891355,17659056488,17689410422,18506945811,17689251677,18506906477,13067467200,13043540876,13295018122,17689410533,18506901466,17689253677,17689979422,13295015833,13003898422,13055741077,17689412511,17689976100,17689410299,17689250322,18506903477,18506949855,18506947833,17689417388,17659051799,17689975322,17659051477,13067467155,17659051488,18558756422,13067467900,17689410611,17659059411,17689251233,13067453622,18650732455,17689415799,18506940788,18506942765,17689978321,17689410799,17659051655,17689410133,18506943877,17689413811,13075913876,17689417533,17689250678,17689970133,17689410567,17689415699,18506948012,17689974733,17689413855,13159429533,17689416211,17689253055,17689256100";
        String[] strings = str.split(",");
        List<HashMap<String, Object>> result = getTopNumber(strings, 20);
        result.forEach(n -> {
            System.out.println(new StringBuffer(String.valueOf(n.get("number"))) + " -  " + n.get("WT"));
            List<Object> detail = (List<Object>) n.get("detail");
            detail.forEach(d -> {
                System.out.println("\t\t\t\t"+d);
            });
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
