package org.springframework.remoting.nioinvoker.example.action;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:45:36
 */
public class ExampleAction2Impl implements ExampleAction2 {
    @Override
    public String execute(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
