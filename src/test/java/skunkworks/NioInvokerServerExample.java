package skunkworks;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import skunkworks.config.NioInvokerServerConfig;

import java.io.IOException;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:32:34
 */
public class NioInvokerServerExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NioInvokerServerConfig.class);
    }
}
