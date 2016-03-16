package library.singularity.com.data.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private TimeSlot pickUpSchedule;
    private TimeSlot deliverySchedule;
    private String status;
    private float rating;
    private String discountCode;
    private Address pickUpAddress;
    private Address deliveryAddress;
    private boolean washAndDry;
    private String id;
    private Bill bill;
    private String color;
    private String notes;
    private List<Driver> driverList;

    public Order(){
        setId(null);
        setPickUpAddress(null);
        setDeliveryAddress(null);
        setStatus(null);
        setRating(0);
        setDiscountCode(null);
        setPickUpSchedule(null);
        setDeliverySchedule(null);
        setWashAndDry(false);
        bill = null;
        notes = null;
        driverList = new ArrayList<>();
    }

    public TimeSlot getPickUpSchedule() {
        return pickUpSchedule;
    }

    public void setPickUpSchedule(TimeSlot pickUpSchedule) {
        this.pickUpSchedule = pickUpSchedule;

        if (pickUpSchedule == null) return;

        this.pickUpSchedule.setOrderId(id);
    }

    public TimeSlot getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(TimeSlot deliverySchedule) {
        this.deliverySchedule = deliverySchedule;

        if (this.deliverySchedule == null) return;
        this.deliverySchedule.setOrderId(id);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public Address getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(Address pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isWashAndDry() {
        return washAndDry;
    }

    public void setWashAndDry(boolean washAndDry) {
        this.washAndDry = washAndDry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void addDriver(Driver driver) {
        if (driverList == null) {
            driverList = new ArrayList<>();
        }

        driverList.add(driver);
    }

    public Driver getDriver(String currentStatus) {
        if (driverList == null) return null;

        for (Driver driver : driverList) {
            if (driver.getStatusAssigned().equals(currentStatus)) {
                return driver;
            }
        }

        return null;
    }

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }
}
