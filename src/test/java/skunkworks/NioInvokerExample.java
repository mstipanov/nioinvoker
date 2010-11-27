package skunkworks;

import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:32:34
 */
public class NioInvokerExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        NioInvokerServerExample.main(args);
        Thread.sleep(200);
        NioInvokerClientExample.main(args);
    }
}
