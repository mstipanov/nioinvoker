package skunkworks;

/**
 * @author mstipanov
 * @since 15.11.10. 10:23
 */
public class DefaultDepartment implements Department {
    private String name;

    public DefaultDepartment() {
    }

    public DefaultDepartment(String name) {
        this.name = name;
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
        sb.append("DefaultDepartment");
        sb.append("{name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
