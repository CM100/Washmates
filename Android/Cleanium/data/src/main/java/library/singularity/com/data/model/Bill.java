package library.singularity.com.data.model;

import java.util.List;

public class Bill {

    DiscountCode discount;
    List<Service> services;
    double total;

    public Bill() { }

    public DiscountCode getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountCode discount) {
        this.discount = discount;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
