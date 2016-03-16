package library.singularity.com.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import library.singularity.com.data.utils.BasicValidator;

public class Address implements Parcelable {

    private String id;
    private String street;
    private int postCode;
    private String suburb;
    private String number;
    private String streetTwo;
    private String notes;
    private String location;
    private String state;
    private String city;

    public Address() {
        id = null;
        street = null;
        postCode = 0;
        suburb = null;
        number = null;
        streetTwo = null;
        notes = null;
        location = null;
        state = null;
        city = null;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPostalCode() {
        return postCode;
    }

    public void setPostalCode(int postalCode) {
        this.postCode = postalCode;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreetTwo() {
        return streetTwo;
    }

    public void setStreetTwo(String streetTwo) {
        this.streetTwo = streetTwo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isStreetValid() {
        return BasicValidator.isValidString(street);
    }

    public boolean isPostalCodeValid(List<Integer> validPostCodes) {
           for(Integer code : validPostCodes){
               if(code == postCode){
                   return true;
               }
           }
        return false;
    }

    public boolean isSuburbValid() {
        return BasicValidator.isValidString(suburb);
    }

    public boolean isNumberValid() {
        return BasicValidator.isValidString(number);
    }

    public boolean isAddressLocationValid() {
        return BasicValidator.isValidString(location);
    }

    public boolean isStateValid() {
        return BasicValidator.isValidString(state);
    }

    public boolean isCityValid() {
        return BasicValidator.isValidString(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(street);
        parcel.writeInt(postCode);
        parcel.writeString(suburb);
        parcel.writeString(number);
        parcel.writeString(streetTwo);
        parcel.writeString(notes);
        parcel.writeString(location);
        parcel.writeString(state);
        parcel.writeString(city);
    }

    public void constructFromParcel(Parcel parcel) {
        id = parcel.readString();
        street = parcel.readString();
        postCode = parcel.readInt();
        suburb = parcel.readString();
        number = parcel.readString();
        streetTwo = parcel.readString();
        notes = parcel.readString();
        location = parcel.readString();
        state = parcel.readString();
        city = parcel.readString();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            Address address = new Address();
            address.constructFromParcel(in);
            return address;
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
