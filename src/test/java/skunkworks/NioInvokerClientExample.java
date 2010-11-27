package skunkworks;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction2;
import skunkworks.config.NioInvokerClientConfig;

import java.io.IOException;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:32:34
 */
public class NioInvokerClientExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NioInvokerClientConfig.class);
        ExampleAction exampleAction = (ExampleAction) applicationContext.getBean("exampleAction");
        ExampleAction2 exampleAction2 = (ExampleAction2) applicationContext.getBean("exampleAction2");

        long start = System.currentTimeMillis();
        int count = 1000;
        for (int i = 1; i <= count; i++) {
            String r1 = exampleAction.execute("Foo " + i);
            String r2 = exampleAction2.execute("Bar " + i);
            System.out.println(r1);
            System.out.println(r2);
//            assert r.equals(s) : "Error invoking";
        }
        long stop = System.currentTimeMillis();
        System.out.println("Total [ms]: " + (stop - start));
        System.out.println("Average [ms]: " + ((stop - start) * 1. / count));
    }
}
