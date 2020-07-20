package com.example.android.skoob.model;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String bookName;
    private int isbnNumber;
    private int price;
    private String department;
    private String subject;
    private List<String> photoUrl = new ArrayList<String>();
    private String emailOfSeller;
    private String placeAddress; //shown in the detail page
    private String place; //used to sort the books based on location by buyer
    private String bookPostedTime;

    //Firebase requires your POJO have to have an empty constructor
    public Book(){
    }

    public Book(String bookName, int isbnNumber, int price, String department, String subject,
                List<String> photoUrl, String emailOfSeller, String placeAddress, String place,
                String bookPostedTime) {
        this.bookName = bookName;
        this.isbnNumber = isbnNumber;
        this.price = price;
        this.department = department;
        this.subject = subject;
        this.photoUrl = photoUrl;
        this.emailOfSeller = emailOfSeller;
        this.placeAddress = placeAddress;
        this.place = place;
        this.bookPostedTime = bookPostedTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getIsbnNumber() {
        return isbnNumber;
    }

    public void setIsbnNumber(int isbnNumber) {
        this.isbnNumber = isbnNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmailOfSeller() {
        return emailOfSeller;
    }

    public void setEmailOfSeller(String emailOfSeller) {
        this.emailOfSeller = emailOfSeller;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getBookPostedTime() {
        return bookPostedTime;
    }

    public void setBookPostedTime(String bookPostedTime) {
        this.bookPostedTime = bookPostedTime;
    }

    @Override
    public String toString() {
        return "{" +
                "bookName='" + bookName + '\'' +
                ", isbnNumber=" + isbnNumber +
                ", price=" + price +
                ", department='" + department + '\'' +
                ", subject='" + subject + '\'' +
                ", photoUrl=" + photoUrl +
                ", emailOfSeller='" + emailOfSeller + '\'' +
                ", placeAddress='" + placeAddress + '\'' +
                ", place='" + place + '\'' +
                ", bookPostedTime'" + bookPostedTime + '\'' +
                '}';
    }
}
