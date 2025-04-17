package kartingRM.Entities;


import jakarta.persistence.*;

@Entity
@Table(name = "pricing")
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int pricingCode;
    private int price;
    private String bookingDuration;

    public Pricing(){
    }

    public Pricing(int pricingCode, int price, String bookingDuration) {
        this.pricingCode = pricingCode;
        this.price = price;
        this.bookingDuration = bookingDuration;
    }
    public int getPricingCode() {
        return pricingCode;
    }
    public void setPricingCode(int pricingCode) {
        this.pricingCode = pricingCode;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getBookingDuration() {
        return bookingDuration;
    }
    public void setBookingDuration(String bookingDuration) {
        this.bookingDuration = bookingDuration;
    }
}
