package skunkworks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.remoting.nioinvoker.NioInvokerProxyFactoryBean;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction2;
import org.springframework.remoting.nioinvoker.io.monitor.RmiPacketCommunicatorMonitor;
import org.springframework.remoting.nioinvoker.io.serialization.JsonByteArraySerializer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:27:35
 */
@Configuration
public class NioInvokerClientConfig {

    @Lazy
    @Bean
    public RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor() throws IOException {
        RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor = new RmiPacketCommunicatorMonitor();
        rmiPacketCommunicatorMonitor.setPoolSize(10);
        rmiPacketCommunicatorMonitor.setByteArraySerializer(new JsonByteArraySerializer());
        return rmiPacketCommunicatorMonitor;
    }

    @Lazy
    @Bean
    public NioInvokerProxyFactoryBean exampleAction() throws IOException {
        NioInvokerProxyFactoryBean proxy = new NioInvokerProxyFactoryBean();
        proxy.setRmiPacketCommunicatorMonitor(rmiPacketCommunicatorMonitor());
        proxy.setServiceUrl("nio://127.0.0.1:8080/ExampleAction");
        proxy.setServiceInterface(ExampleAction.class);
        proxy.setTimeout(0, TimeUnit.MILLISECONDS);
        return proxy;
    }

    @Lazy
    @Bean
    public NioInvokerProxyFactoryBean exampleAction2() throws IOException {
        NioInvokerProxyFactoryBean proxy = new NioInvokerProxyFactoryBean();
        proxy.setRmiPacketCommunicatorMonitor(rmiPacketCommunicatorMonitor());
        proxy.setServiceUrl("nio://127.0.0.1:8080/ExampleAction2");
        proxy.setServiceInterface(ExampleAction2.class);
        proxy.setTimeout(0, TimeUnit.MILLISECONDS);
        return proxy;
    }
}
