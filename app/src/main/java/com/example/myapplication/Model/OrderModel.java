package com.example.myapplication.Model;
import java.util.ArrayList;
public class OrderModel {
    private ArrayList<String> locationList;
    private String orderNumber;
    private String NumberOfAmountWeight;
    private String Note;
    private ArrayList<String> itemsList;
    private String radioValue;
    private String status;
    public ArrayList<String> getLocationList() { return locationList; }
    public void setLocationList(ArrayList<String> value) { this.locationList = value; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String value) { this.orderNumber = value; }
    public String getNumberOfAmountWeight() { return NumberOfAmountWeight; }
    public void setNumberOfAmountWeight(String value) { this.NumberOfAmountWeight = value; }
    public String getNote() { return Note; }
    public void setNote(String value) { this.Note = value; }
    public ArrayList<String> getItemsList() { return itemsList; }
    public void setItemsList(ArrayList<String> value) { this.itemsList = value; }
    public String getRadioValue() { return radioValue; }
    public void setRadioValue(String value) { this.radioValue = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
