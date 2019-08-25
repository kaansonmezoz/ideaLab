package idealab.api.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login", nullable = false)
    @Length (min = 1, max = 254)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @OneToMany(targetEntity=PrintStatus.class, mappedBy="employeeId")   
    private Set<PrintStatus> printStatus;

    @Column(name = "first_name", nullable = false)
    @Length(min = 1, max = 254)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Length(min = 1,  max = 254)
    private String lastName;

    public Employee() {
    }

    public Employee(@Length(min = 1, max = 254) String login,
                    String passwordHash,
                    EmployeeRole role,
                    Set<PrintStatus> printStatus,
                    @Length(min = 1, max = 254) String firstName,
                    @Length(min = 1, max = 254) String lastName) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.printStatus = printStatus;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    //getter & setter
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}