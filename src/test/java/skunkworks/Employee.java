package skunkworks;

/**
 * @author mstipanov
 * @since 15.11.10. 10:23
 */
public interface Employee {
    Department getDepartment();

    void setDepartment(Department department);

    String getName();

    void setName(String name);
}
