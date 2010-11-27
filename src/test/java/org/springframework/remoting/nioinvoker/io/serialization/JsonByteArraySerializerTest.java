package org.springframework.remoting.nioinvoker.io.serialization;

import org.junit.Before;
import org.junit.Test;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationResultWrapper;
import org.springframework.remoting.nioinvoker.io.RemoteInvocationWrapper;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import skunkworks.DefaultDepartment;
import skunkworks.DefaultEmployee;

import static org.junit.Assert.*;

/**
 * @author mstipanov
 * @since 24.10.10. 19:15
 */
public class JsonByteArraySerializerTest {
    private JsonByteArraySerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new JsonByteArraySerializer();
    }

    @Test
    public void testSerialize() throws Exception {
        byte[] bytes = serializer.serialize(new RemoteInvocation("test", new Class[]{String.class, Integer.class}, new Object[]{"lala", 5}));
        assertNotNull(bytes);
        assertEquals(202, bytes.length);
    }

    @Test
    public void testDeserialize() throws Exception {
        byte[] bytes = serializer.serialize(new RemoteInvocation("test", new Class[]{String.class, Integer.class}, new Object[]{"lala", 5}));
        RemoteInvocation ri = serializer.deserialize(bytes, RemoteInvocation.class);
        assertNotNull(ri);
        assertEquals("test", ri.getMethodName());
        assertEquals(String.class, ri.getParameterTypes()[0]);
        assertEquals(Integer.class, ri.getParameterTypes()[1]);
        assertEquals("lala", ri.getArguments()[0]);
        assertEquals(5, ri.getArguments()[1]);
    }

    @Test
    public void testSerialize2() throws Exception {
        byte[] bytes = serializer.serialize(new RemoteInvocationResult("test"));
        assertNotNull(bytes);
        assertEquals(97, bytes.length);
    }

    @Test
    public void testDeserialize2() throws Exception {
        byte[] bytes = serializer.serialize(createRemoteInvocationResultWrapper("test", null));
        RemoteInvocationResultWrapper riw = serializer.deserialize(bytes, RemoteInvocationResultWrapper.class);
        assertNotNull(riw);
        assertEquals("test", riw.getResult());
    }

    private RemoteInvocationResultWrapper createRemoteInvocationResultWrapper(String result, RuntimeException exception) {
        RemoteInvocationResultWrapper resultWrapper = new RemoteInvocationResultWrapper();
        resultWrapper.setResult(result);
        resultWrapper.setException(exception);
        return resultWrapper;
    }

    @Test
    public void testDeserialize2_exception() throws Exception {
        byte[] bytes = serializer.serialize(createRemoteInvocationResultWrapper(null, new RuntimeException("lala")));
        System.out.println(new String(bytes));

        RemoteInvocationResultWrapper ri = serializer.deserialize(bytes, RemoteInvocationResultWrapper.class);
        assertNotNull(ri);
        assertNull(ri.getResult());
        assertEquals("lala", ri.getException().getMessage());
    }

    @Test
    public void testDeserialize_wrapper() throws Exception {
        RemoteInvocation rib = new RemoteInvocation("execute", new Class[]{String.class}, new Object[]{"Foo 1"});
        byte[] bytes = serializer.serialize(new RemoteInvocationWrapper(1L, rib, "testPath"));
        System.out.println(new String(bytes));

        RemoteInvocationWrapper riw = serializer.deserialize(bytes, RemoteInvocationWrapper.class);
        assertNotNull(riw);
        RemoteInvocation ri = riw.getRemoteInvocation();
        assertNotNull(ri);
        assertEquals("execute", ri.getMethodName());
        assertEquals(String.class, ri.getParameterTypes()[0]);
        assertEquals("Foo 1", ri.getArguments()[0]);
    }

    @Test
    public void testSerialize_interfaces() throws Exception {
        byte[] bytes = serializer.serialize(new DefaultEmployee(new DefaultDepartment("CEO"), "Mujo"));
        Object emp = serializer.deserialize(bytes, Object.class);

        assertTrue(emp instanceof DefaultEmployee);
        DefaultEmployee employee = (DefaultEmployee) emp;
        assertEquals("Mujo", employee.getName());

        assertTrue(employee.getDepartment() instanceof DefaultDepartment);
        DefaultDepartment department = (DefaultDepartment) employee.getDepartment();
        assertEquals("CEO", department.getName());
    }
}
