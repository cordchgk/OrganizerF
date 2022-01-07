package organizer.schedule.event.dtos;

import org.primefaces.model.ScheduleEvent;
import organizer.diet.meal.dtos.MealDTO;
import organizer.user.dtos.UserDTO;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class EventDTO {

    private int eID;


    List<UserDTO> userDTOList;
    List<MealDTO> mealDTOList;


    String name;


    LocalDateTime start;
    LocalDateTime end;
    private LocalDate date;


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<UserDTO> getUserDTOList() {
        return userDTOList;
    }

    public void setUserDTOList(List<UserDTO> userDTOList) {
        this.userDTOList = userDTOList;
    }

    public List<MealDTO> getMealDTOList() {
        return mealDTOList;
    }

    public void setMealDTOList(List<MealDTO> mealDTOList) {
        this.mealDTOList = mealDTOList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStart() {

        return this.start;
    }


    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }


    public int geteID() {
        return eID;
    }

    public void seteID(int eID) {
        this.eID = eID;
    }

    public static EventDTO getFromScheduleEvent(ScheduleEvent scheduleEvent) {
        EventDTO toReturn = new EventDTO();

        if (scheduleEvent.getId() != null) {
            toReturn.seteID(Integer.parseInt(scheduleEvent.getId()));


        } else {
            toReturn.seteID(0);
        }


        toReturn.setName(scheduleEvent.getTitle());
        toReturn.setStart(scheduleEvent.getStartDate());
        toReturn.setEnd(scheduleEvent.getEndDate());


        return toReturn;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String n = "Name: ";
        String s = "Start: ";
        String e = "End: ";

        stringBuilder.append(n + this.name + "\n");
        stringBuilder.append(s + this.start + "\n");
        stringBuilder.append(e + this.end + "\n");

        return stringBuilder.toString();

    }
}
