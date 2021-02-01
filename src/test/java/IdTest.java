import cn.hutool.core.util.RandomUtil;
import org.junit.Test;

/**
 * @author htc
 * @date 2020-09-08
 */
public class IdTest {
    @Test
    public void randomTest(){
        String baseStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTVUWXYZ1234567890";
        System.out.println(RandomUtil.randomString(baseStr, 10));
    }
}
