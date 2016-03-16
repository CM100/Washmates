package singularity.com.cleanium.model;

public class UserAddressInfo {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postCode;
    private int checkedRadioButtonId;
    private String accessNotes;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }


    public String getAccessNotes() {
        return accessNotes;
    }

    public void setAccessNotes(String accessNotes) {
        this.accessNotes = accessNotes;
    }

    public int getCheckedRadioButtonId() {
        return checkedRadioButtonId;
    }

    public void setCheckedRadioButtonId(int checkedRadioButtonId) {
        this.checkedRadioButtonId = checkedRadioButtonId;
    }
}
