package library.singularity.com.data.model;

import java.util.Date;

import library.singularity.com.data.utils.BasicValidator;

public class TimeSlot {

    private Date fromDate;
    private Date toDate;
    private int postCode;
    private String id;
    private String orderId;

    public TimeSlot() {
        fromDate = null;
        toDate = null;
        postCode = 0;
        setId(null);
        orderId = null;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
