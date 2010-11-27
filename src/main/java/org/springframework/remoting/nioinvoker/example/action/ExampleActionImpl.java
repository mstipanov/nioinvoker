package org.springframework.remoting.nioinvoker.example.action;

/**
 * @author Marko Stipanov
 * @since 08.10.2010. 07:45:36
 */
public class ExampleActionImpl implements ExampleAction {
    @Override
    public String execute(String s) {
        return s;
    }
}
