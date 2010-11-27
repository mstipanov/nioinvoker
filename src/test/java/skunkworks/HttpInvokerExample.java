package skunkworks;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction;
import skunkworks.config.HtmlInvokerConfig;

import java.io.IOException;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:32:34
 */
public class HttpInvokerExample {
    public static void main(String[] args) throws IOException {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HtmlInvokerConfig.class);
        ExampleAction exampleAction = (ExampleAction) applicationContext.getBean("exampleActionHttpProxy");

        for (int i = 1; i <= 10000; i++) {
            String s = "Foo " + i;
            System.out.println(exampleAction.execute(s));
        }
    }
}
