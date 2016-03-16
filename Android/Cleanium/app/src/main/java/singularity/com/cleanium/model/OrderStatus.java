package singularity.com.cleanium.model;

public class OrderStatus {
    private String name;
    private String text;

    public OrderStatus() {}

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
