package us.codecraft.wifesays.wife;

import org.junit.Test;

import java.io.IOException;

/**
 * User: cairne
 * Date: 13-5-13
 * Time: 上午10:35
 */
public class WifeSaysTest {

    @Test
    public void test() throws InterruptedException {
        WifeSays wifeSays = new WifeSays();
        try {
            wifeSays.connect();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for(int i=0;i<1000;i++) {

            wifeSays.say("hello");
            try {
                System.out.println(wifeSays.hear());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Thread.sleep(1000);
            if (i>5){
                wifeSays.say("shutdown");
            }
        }
    }
}
