package skunkworks;

/**
 * @author mstipanov
 * @since 15.11.10. 10:23
 */
public class DefaultEmployee {
    private String name;
    private Department department;

    public DefaultEmployee() {
    }

    public DefaultEmployee(Department department, String name) {
        this.department = department;
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DefaultEmployee");
        sb.append("{department=").append(department);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
