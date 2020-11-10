package com.example.mecanicoorganizer.model;

public class Vehicle {
    String model;
    String brand;
    String regNumber;
    String year;
    String color;
    String customerComments;
    String diagnosis;
    double price;
    String status;
    String additionDate;
    String id;
    boolean edited;
    String customerPhoneNumber;
    String vehiclePhotoUri;

    public Vehicle() {}

    public Vehicle(String model, String brand, String regNumber, String year, String color, String customerComments,
                   String diagnosis, double price, String status, boolean edited,String customerPhoneNumber,
                   String vehiclePhotoUri ,String additionDate, String id) {
        this.model = model;
        this.brand = brand;
        this.regNumber = regNumber;
        this.year = year;
        this.color = color;
        this.customerComments = customerComments;
        this.diagnosis = diagnosis;
        this.price = price;
        this.status = status;
        this.edited = edited;
        this.customerPhoneNumber = customerPhoneNumber;
        this.vehiclePhotoUri = vehiclePhotoUri;
        this.additionDate = additionDate;
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCustomerComments() {
        return customerComments;
    }

    public void setCustomerComments(String customerComments) {
        this.customerComments = customerComments;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getEdited() { return edited; }

    public void setEdited(boolean edited) { this.edited = edited; }

    public String getCustomerPhoneNumber() { return customerPhoneNumber; }

    public void setCustomerPhoneNumber(String customerPhoneNumber) { this.customerPhoneNumber = customerPhoneNumber; }

    public String getVehiclePhotoUri() { return vehiclePhotoUri; }

    public void setVehiclePhotoUri(String vehiclePhotoUri) { this.vehiclePhotoUri = vehiclePhotoUri; }

    public String getAdditionDate() {
        return additionDate;
    }

    public void setAdditionDate(String additionDate) {
        this.additionDate = additionDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
