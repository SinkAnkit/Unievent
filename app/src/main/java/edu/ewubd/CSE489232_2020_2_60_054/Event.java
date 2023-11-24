package edu.ewubd.CSE489232_2020_2_60_054;

public class Event {
    String id = "";
    String name = "";
    String place = "";
    String datetime = "";
    String capacity = "";
    String budget = "";
    String email = "";
    String phone = "";
    String description = "";
    String eventType = "";
    String reminder = "";

    public Event(String id, String name, String place, String datetime,String capacity,String budget,String email,String phone,String description,String eventType, String reminder){
        this.id = id;
        this.name = name;
        this.place = place;
        this.datetime = datetime;
        this.capacity = capacity;
        this.budget = budget;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.eventType = eventType;
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", datetime='" + datetime + '\'' +
                ", capacity='" + capacity + '\'' +
                ", budget='" + budget + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", description='" + description + '\'' +
                ", eventType='" + eventType + '\'' +
                ", reminder='" + reminder + '\'' +
                '}';
    }
}