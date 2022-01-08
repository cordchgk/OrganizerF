package organizer.schedule.event.dtos;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.ScheduleEvent;
import organizer.diet.meal.dtos.MealDTO;
import organizer.schedule.event.daos.EventDAO;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class EventDTO {

    private int eID;


    List<UserDTO> userDTOList;
    List<MealDTO> mealDTOList;


    String name;


    LocalDateTime start;
    LocalDateTime end;
    private LocalDate date;


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


        EventDAO eventDAO = new EventDAO();
        toReturn.setMealDTOList(eventDAO.selectMealsByEventDTO(toReturn));

        System.out.println("size: " + toReturn.mealDTOList.size());

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
