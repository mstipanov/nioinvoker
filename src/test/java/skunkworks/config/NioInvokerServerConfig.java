package skunkworks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.remoting.nioinvoker.NioInvokerServiceExporter;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction2;
import org.springframework.remoting.nioinvoker.example.action.ExampleAction2Impl;
import org.springframework.remoting.nioinvoker.example.action.ExampleActionImpl;
import org.springframework.remoting.nioinvoker.io.monitor.RmiPacketCommunicatorMonitor;
import org.springframework.remoting.nioinvoker.io.serialization.JsonByteArraySerializer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:27:35
 */
@Configuration
public class NioInvokerServerConfig {

    @Lazy
    @Bean
    public RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor() throws IOException {
        RmiPacketCommunicatorMonitor rmiPacketCommunicatorMonitor = new RmiPacketCommunicatorMonitor();
        rmiPacketCommunicatorMonitor.setPoolSize(10);
        rmiPacketCommunicatorMonitor.setByteArraySerializer(new JsonByteArraySerializer());
        return rmiPacketCommunicatorMonitor;
    }

    @Bean
    public NioInvokerServiceExporter nioInvokerServiceExporter() throws IOException {
        NioInvokerServiceExporter exporter = new NioInvokerServiceExporter();
        exporter.setRmiPacketCommunicatorMonitor(rmiPacketCommunicatorMonitor());
        exporter.setInetSocketAddress(new InetSocketAddress("127.0.0.1", 8080));
        exporter.setContextPath("ExampleAction");
        exporter.setService(exampleAction());
        exporter.setServiceInterface(ExampleAction.class);
        return exporter;
    }

    @Bean
    public NioInvokerServiceExporter nioInvokerServiceExporter2() throws IOException {
        NioInvokerServiceExporter exporter = new NioInvokerServiceExporter();
        exporter.setRmiPacketCommunicatorMonitor(rmiPacketCommunicatorMonitor());
        exporter.setInetSocketAddress(new InetSocketAddress("127.0.0.1", 8080));
        exporter.setContextPath("ExampleAction2");
        exporter.setService(exampleAction2());
        exporter.setServiceInterface(ExampleAction2.class);
        return exporter;
    }

    @Lazy
    @Bean
    public ExampleAction exampleAction() {
        return new ExampleActionImpl();
    }

    @Lazy
    @Bean
    public ExampleAction2 exampleAction2() {
        return new ExampleAction2Impl();
    }
}
