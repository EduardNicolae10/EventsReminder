package entities;

public class EventModel {
    private String eventName;
    private String date;
    private String time;
    private int iduser;
    private int id;

    public EventModel(int id,String eventName, String date, String time, int iduser) {
        this.id = id;
        this.eventName = eventName;
        this.date = date;
        this.time = time;
        this.iduser = iduser;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event: " + eventName + '\n' +
                "Date: " + date + '\n' +
                "Time: " + time + '\n';
    }
}
