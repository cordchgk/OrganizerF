package organizer.schedule.event.dtos;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.ScheduleEvent;
import organizer.diet.meal.dtos.MealDTO;
import organizer.schedule.event.daos.EventDAO;
import organizer.user.dtos.UserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class EventDTO {

    private int eID;


    private List<UserDTO> u_DTO_L;
    private List<MealDTO> m_DTO_L;


    private String name;


    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDate date;


    public static EventDTO getFromScheduleEvent(ScheduleEvent s_Event) {
        EventDTO t_R = new EventDTO();

        if (s_Event.getId() != null) {
            t_R.setEID(Integer.parseInt(s_Event.getId()));


        } else {
            t_R.setEID(0);
        }


        t_R.setName(s_Event.getTitle());
        t_R.setStart(s_Event.getStartDate());
        t_R.setEnd(s_Event.getEndDate());


        EventDAO eventDAO = new EventDAO();
        t_R.setM_DTO_L(eventDAO.selectMealsByEventDTO(t_R));

        return t_R;
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
