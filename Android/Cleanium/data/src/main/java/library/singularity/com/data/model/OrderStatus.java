package library.singularity.com.data.model;

import java.util.List;

public class OrderStatus {

    private String name;
    private String text;
    private String color;
    private boolean showOnHistory;
    private boolean cancellable;
    private String driverFrom;
    private String mode;

    public OrderStatus() {
        showOnHistory = false;
        color = "#000000";
        cancellable = false;
        mode = "PROGRESS";
        driverFrom = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isShowOnHistory() {
        return showOnHistory;
    }

    public void setShowOnHistory(boolean showOnHistory) {
        this.showOnHistory = showOnHistory;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public String getDriverFrom() {
        return driverFrom;
    }

    public void setDriverFrom(String driverFrom) {
        this.driverFrom = driverFrom;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
