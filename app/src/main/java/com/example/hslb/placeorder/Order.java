package com.example.hslb.placeorder;
public class Order {
    private String Order_Name;
    private String Order_Status;

    public Order(){

    }
    public Order(String Order_Name,String Order_Status){
        this.Order_Name = Order_Name;
        this.Order_Status = Order_Status;
    }

    public String getOrder_Name(){
        return Order_Name;
    }
    public String getOrder_Status(){
        return Order_Status;
    }
    public void setOrder_Name(String Order_Name){
        this.Order_Name = Order_Name;
    }
    public void setOrder_status(String Order_Status){
        this.Order_Status = Order_Status;
    }
}
