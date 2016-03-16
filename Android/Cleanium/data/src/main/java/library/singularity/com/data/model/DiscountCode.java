package library.singularity.com.data.model;

public class DiscountCode {

    private String id;
    private String code;
    private double amount;
    private boolean isPercentage;

    public DiscountCode(){
        setId(null);
        setCode(null);
        setAmount(0);
        setIsPercentage(false);
    }

    public String getCode() {return code;}

    public void setCode(String code) { this.code = code;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setIsPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }
}
