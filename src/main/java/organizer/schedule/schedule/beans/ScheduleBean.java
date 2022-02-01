package organizer.schedule.schedule.beans;/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.schedule.event.daos.EventDAO;
import organizer.schedule.event.dtos.EventDTO;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static organizer.schedule.event.dtos.EventDTO.getFromScheduleEvent;

@Named("scheduleBean")
@ViewScoped
@Getter
@Setter
public class ScheduleBean implements Serializable {

    private String serverTimeZone = ZoneId.systemDefault().toString();


    private ScheduleEvent<?> e;
    private ScheduleModel s_M;

    private List<MealDTO> u_M_L;
    private DataModel<MealDTO> u_M_DM;
    private List<MealDTO> e_M_L;
    private DataModel<MealDTO> e_M_DM;

    private EventDAO e_DAO;
    private MealDAO m_DAO;

    @Inject
    private UserBean u_Bean;

    @PostConstruct
    public void init() {


        if (this.u_Bean.getU_DTO().getUserID() == null) {
            this.allowedAndLoggedIn();
        } else {
            this.s_M = new DefaultScheduleModel();
            this.e = new DefaultScheduleEvent<>();

            this.e_M_L = new ArrayList<>();

            this.e_DAO = new EventDAO();
            this.m_DAO = new MealDAO();


            List<EventDTO> u_E_L = new EventDAO().selectByUserDto(u_Bean.getU_DTO());


            for (EventDTO eventDTO : u_E_L) {

                e = DefaultScheduleEvent.builder()
                        .editable(true)
                        .id(String.valueOf(eventDTO.getEID()))
                        .title(eventDTO.getName())
                        .startDate(eventDTO.getStart())
                        .endDate(eventDTO.getEnd())
                        .description(eventDTO.getName())
                        .overlapAllowed(true)
                        .data(eventDTO.getM_DTO_L())
                        .build();

                s_M.addEvent(e);

            }

            this.createTotals();

            this.buildUserMeals();
            this.e = this.s_M.getEvent(this.u_Bean.getC_E_ID());

            EventDTO e_DTO = new EventDTO();
            if (this.u_Bean.getC_E_ID() != null) {
                e_DTO.setEID(Integer.parseInt(this.u_Bean.getC_E_ID()));

                this.buildEventMeals(e_DTO);

            }


        }

    }


    public void addEvent() {

        if (e.getId() == null) {
            try {

                EventDTO t_A = getFromScheduleEvent(e);
                t_A.setDate(e.getStartDate().toLocalDate());
                t_A.setEID(0);
                this.e_DAO.createNewEvent(t_A, this.u_Bean.getU_DTO());
            } catch (DuplicateException e) {
                e.printStackTrace();
            }


        } else {
            s_M.updateEvent(e);
            EventDTO toAdd = getFromScheduleEvent(e);
            try {
                this.e_DAO.updateEvent(toAdd);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }

        }

        this.init();


        e = new DefaultScheduleEvent<>();
    }


    public void removeMealFromEvent() {
        EventDTO e_DTO = new EventDTO();
        e_DTO.setEID(Integer.parseInt(this.e.getId()));


        MealDTO m_DTO = this.e_M_DM.getRowData();


        this.e_DAO.deleteMealFromEvent(e_DTO, m_DTO);

        this.buildEventMeals(e_DTO);

        this.u_Bean.setC_E_ID(e.getId());

    }

    public void addMealToEvent() {

        EventDTO e_DTO = new EventDTO();
        e_DTO.setEID(Integer.parseInt(this.e.getId()));

        MealDTO mealDTO = this.u_M_DM.getRowData();

        EventDAO eventDAO = new EventDAO();

        try {
            eventDAO.addMealToEvent(e_DTO, mealDTO);
        } catch (DuplicateException e) {
            e.printStackTrace();
        }


        this.buildEventMeals(e_DTO);
        this.buildUserMeals();

    }


    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {

        e = DefaultScheduleEvent.builder()
                .editable(true)
                .startDate(selectEvent.getObject())
                .endDate(selectEvent.getObject().plusHours(1))
                .data(new ArrayList<MealDTO>())
                .build();

    }


    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
                "Delta:" + event.getDeltaAsDuration());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
                "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

        addMessage(message);
    }

    public void onEventDelete() {
        String eventId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eventId");
        if (e != null) {
            ScheduleEvent<?> event = s_M.getEvent(eventId);
            s_M.deleteEvent(event);
        }
    }


    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        e = (ScheduleEvent) selectEvent.getObject();
        if (e.isEditable()) {


            EventDTO eventDTO = new EventDTO();
            eventDTO.setEID(Integer.parseInt(this.e.getId()));
            this.buildEventMeals(eventDTO);

            this.buildUserMeals();

            PrimeFaces.current().executeInitScript("showDialog();");
            this.u_Bean.setC_E_ID(e.getId());


        }


    }

    private void buildEventMeals(EventDTO e_DTO) {
        this.e_M_L = this.e_DAO.selectMealsByEventDTO(e_DTO);
        this.e_M_DM = new ListDataModel(this.e_M_L);

    }


    private static MealDTO createTotal(List<MealDTO> mealDTOList) {
        MealDTO toReturn = new MealDTO();
        float fats = 0;
        float protein = 0;
        float carbs = 0;
        float calories = 0;
        if (mealDTOList != null) {
            for (MealDTO mealDTO : mealDTOList) {
                fats += mealDTO.getFats();
                protein += mealDTO.getProtein();
                carbs += mealDTO.getCarbs();
                calories += mealDTO.getCalories();

            }
        }


        toReturn.setName("Total");
        toReturn.setFats(fats);
        toReturn.setProtein(protein);
        toReturn.setCarbs(carbs);
        toReturn.setCalories(calories);

        return toReturn;
    }


    private void createTotals() {
        HashMap<LocalDate, List<ScheduleEvent>> hashMap = new HashMap();

        for (ScheduleEvent event : this.s_M.getEvents()) {

            if (!hashMap.containsKey(event.getStartDate().toLocalDate())) {

                hashMap.put(event.getStartDate().toLocalDate(), new ArrayList<>());
            }
            hashMap.get(event.getStartDate().toLocalDate()).add(event);

        }

        for (LocalDate localDate : hashMap.keySet()) {
            ScheduleEvent e = createAllEvent(hashMap.get(localDate));
            s_M.addEvent(e);
        }

    }

    private void buildUserMeals() {
        this.u_M_L = this.m_DAO.getMealsByUserDTO(this.u_Bean.getU_DTO());
        this.u_M_DM = new ListDataModel(this.u_M_L);

    }


    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }


    public void allowedAndLoggedIn() {

        FacesContext fc = FacesContext.getCurrentInstance();

        if (this.u_Bean.getU_DTO().getUserID() == null) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication()
                    .getNavigationHandler();
            nav.performNavigation(FaceletPath.LOGIN.getPath());
        }


    }


    public ScheduleModel getS_M() {
        if (s_M == null) {
            s_M = new DefaultScheduleModel();
        }

        return s_M;
    }

    private static ScheduleEvent createAllEvent(List<ScheduleEvent> s_E_L) {

        List<MealDTO> allMealsOfDay = new ArrayList<>();

        for (ScheduleEvent event : s_E_L) {
            List<MealDTO> mealsOfEvent = (List<MealDTO>) event.getData();
            MealDTO all = createTotal(mealsOfEvent);
            allMealsOfDay.add(all);
        }

        MealDTO total = createTotal(allMealsOfDay);

        ScheduleEvent t_R = DefaultScheduleEvent.builder()

                .title("Total: " + total.getFats() + "F, " + total.getProtein() + "P, " + total.getCarbs() + "C, " + total.getCalories() + "Calories")
                .startDate(s_E_L.get(0).getStartDate())
                .endDate(s_E_L.get(0).getStartDate())
                .draggable(false)
                .resizable(false)
                .editable(false)
                .overlapAllowed(true)
                .allDay(true)
                .backgroundColor("#25e712")
                .build();


        return t_R;
    }

}
