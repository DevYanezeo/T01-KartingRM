package kartingRM.Entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Cliente")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;
    private String clientName;
    private String clientEmail;
    private int monthlyVisits;
    private Date birthDate;

    public Client() {
    }

    public Client(String clientName, String clientEmail, int monthlyVisits, Date birthDate) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.monthlyVisits = monthlyVisits;
        this.birthDate = birthDate;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public int getMonthlyVisits() {
        return monthlyVisits;
    }
    public void setMonthlyVisits(int monthlyVisits) {
        this.monthlyVisits = monthlyVisits;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}
