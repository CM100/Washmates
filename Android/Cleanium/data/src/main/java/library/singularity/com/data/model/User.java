package library.singularity.com.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import library.singularity.com.data.utils.BasicValidator;

public class User implements Parcelable {

    private static final byte MINIMAL_PASSWORD_LENGTH = 6;
    private static final long PHONE_NUMBER_MINIMAL = 10000000;
    private static final long PHONE_NUMBER_MAXIMAL = 99999999;

    private String id;
    private String username;
    private String password;
    private boolean emailVerified;
    private String email;
    private String imagePath;
    private boolean isStripeClient;
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private List<Address> addresses;
    private String confirmPassword;

    public User() {
        id = null;
        username = null;
        password = null;
        emailVerified = false;
        email = null;
        imagePath = null;
        isStripeClient = false;
        firstName = null;
        lastName = null;
        phoneNumber = -1;
        addresses = null;
        confirmPassword = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isStripeClient() {
        return isStripeClient;
    }

    public void setIsStripeClient(boolean isStripeClient) {
        this.isStripeClient = isStripeClient;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddresses(List<Address> addresses) {
        if (addresses == null || (addresses != null && addresses.size() == 0)) return;

        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }

        this.addresses.addAll(addresses);
    }

    public void addAddress(Address address) {
        if (address == null) return;

        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }

        this.addresses.addAll(addresses);
    }

    public boolean isUsernameValid() {
        return BasicValidator.isValidEmail(username);
    }

    public boolean isPasswordValid() {
        return BasicValidator.isValidString(password, MINIMAL_PASSWORD_LENGTH);
    }

    public boolean isEmailValid() {
        return BasicValidator.isValidEmail(email);
    }

    public boolean isFirstNameValid() {
        return BasicValidator.isValidString(firstName);
    }

    public boolean isLastNameValid() {
        return BasicValidator.isValidString(lastName);
    }

    public boolean isPhoneNumberValid() {
        return BasicValidator.isNumberInRange(phoneNumber, PHONE_NUMBER_MINIMAL, PHONE_NUMBER_MAXIMAL);
    }

    public boolean isPhoneNumberToShort(){
        if(phoneNumber < 10000000){
          return true;
        }
        return false;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeInt(emailVerified ? 1 : 0);
        parcel.writeString(email);
        parcel.writeString(imagePath);
        parcel.writeInt(isStripeClient ? 1 : 0);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeLong(phoneNumber);
    }

    public void constructFromParcel(Parcel parcel) {
        id = parcel.readString();
        username = parcel.readString();
        password = parcel.readString();
        emailVerified= parcel.readInt() == 1;
        email = parcel.readString();
        imagePath = parcel.readString();
        isStripeClient = parcel.readInt() == 1;
        firstName = parcel.readString();
        lastName = parcel.readString();
        phoneNumber = parcel.readLong();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            User user = new User();
            user.constructFromParcel(in);
            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
