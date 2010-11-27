package skunkworks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.remoting.nioinvoker.NioInvokerProxyFactoryBean;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:27:35
 */
@Configuration
public class HtmlInvokerConfig {

    @Lazy
    @Bean
    public NioInvokerProxyFactoryBean exampleActionHttpProxy() {
        NioInvokerProxyFactoryBean proxy = new NioInvokerProxyFactoryBean();
        proxy.setServiceUrl("http://127.0.0.1:8080/ExampleAction");
        proxy.setServiceInterface(ExampleAction.class);
        return proxy;
    }
}
