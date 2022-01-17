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
import organizer.schedule.schedule.service.ExtenderService;
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
import java.util.Map;

import static organizer.schedule.event.dtos.EventDTO.getFromScheduleEvent;

@Named("scheduleBean")
@ViewScoped
@Getter
@Setter
public class ScheduleBean implements Serializable {


    @Inject
    private ExtenderService extenderService;

    private ScheduleModel eventModel;


    private ScheduleEvent<?> event = new DefaultScheduleEvent<>();

    private boolean slotEventOverlap = true;
    private boolean showWeekNumbers = false;
    private boolean showHeader = true;
    private boolean draggable = true;
    private boolean resizable = true;
    private boolean showWeekends = true;
    private boolean tooltip = true;
    private boolean allDaySlot = true;
    private boolean rtl = false;

    private double aspectRatio = Double.MIN_VALUE;

    private String leftHeaderTemplate = "prev,next today";
    private String centerHeaderTemplate = "title";
    private String rightHeaderTemplate = "dayGridMonth,timeGridWeek,timeGridDay,listYear";
    private String nextDayThreshold = "09:00:00";
    private String weekNumberCalculation = "local";
    private String weekNumberCalculator = "date.getTime()";
    private String displayEventEnd;
    private String timeFormat;
    private String slotDuration = "00:30:00";
    private String slotLabelInterval;
    private String slotLabelFormat;
    private String scrollTime = "06:00:00";
    private String minTime = "04:00:00";
    private String maxTime = "20:00:00";
    private String locale = "en";
    private String serverTimeZone = ZoneId.systemDefault().toString();
    private String timeZone = "";
    private String clientTimeZone = "local";
    private String columnHeaderFormat = "";
    private String view = "timeGridWeek";
    private String height = "auto";

    private String extenderCode = "// Write your code here or select an example from above";
    private String selectedExtenderExample = "";

    private Map<String, ExtenderService.ExtenderExample> extenderExamples;

    private List<MealDTO> userMeals;

    private DataModel userMealDataModel;

    private List<MealDTO> eventMeals;

    private DataModel eventMealsDataModel;


    @Inject
    private UserBean userBean;


    @PostConstruct
    public void init() {

        if (this.userBean.getDto().getUserID() == null) {
            this.allowedAndLoggedIn();
        } else {
            eventModel = new DefaultScheduleModel();


            List<EventDTO> userEvents = new EventDAO().selectByUserDto(userBean.getDto());


            for (EventDTO eventDTO : userEvents) {

                event = DefaultScheduleEvent.builder()
                        .editable(true)
                        .id(String.valueOf(eventDTO.geteID()))
                        .title(eventDTO.getName())
                        .startDate(eventDTO.getStart())
                        .endDate(eventDTO.getEnd())
                        .description(eventDTO.getName())
                        .overlapAllowed(true)
                        .data(eventDTO.getMealDTOList())
                        .build();

                eventModel.addEvent(event);

            }

            this.createTotals();


            MealDAO mealDAO = new MealDAO();
            this.userMeals = mealDAO.getMealsByUserDTO(this.userBean.getDto());
            this.userMealDataModel = new ListDataModel(this.userMeals);


            this.event = this.eventModel.getEvent(this.userBean.getCurrentEventId());

            EventDAO eventDAO = new EventDAO();
            EventDTO eventDTO = new EventDTO();
            if (this.userBean.getCurrentEventId() != null) {
                eventDTO.seteID(Integer.parseInt(this.userBean.getCurrentEventId()));

                this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
                this.eventMealsDataModel = new ListDataModel(this.eventMeals);

            }


            extenderExamples = extenderService.createExtenderExamples();
        }

    }

    private void createTotals() {
        HashMap<LocalDate, List<ScheduleEvent>> hashMap = new HashMap();

        for (ScheduleEvent event : this.eventModel.getEvents()) {

            if (!hashMap.containsKey(event.getStartDate().toLocalDate())) {

                hashMap.put(event.getStartDate().toLocalDate(), new ArrayList<>());
            }
            hashMap.get(event.getStartDate().toLocalDate()).add(event);

        }

        for (LocalDate localDate : hashMap.keySet()) {
            ScheduleEvent e = createAllEvent(hashMap.get(localDate));
            eventModel.addEvent(e);
        }

    }


    public ScheduleModel getEventModel() {
        if (eventModel == null) {
            eventModel = new DefaultScheduleModel();
        }

        return eventModel;
    }


    public void addEvent() {

        if (event.isAllDay()) {
            // see https://github.com/primefaces/primefaces/issues/1164
            if (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate())) {
                event.setEndDate(event.getEndDate().plusDays(1));
            }
        }
        EventDAO eventDAO = new EventDAO();
        if (event.getId() == null) {
            try {

                EventDTO toAdd = getFromScheduleEvent(event);
                toAdd.setDate(event.getStartDate().toLocalDate());
                toAdd.seteID(0);

                eventDAO.createNewEvent(toAdd, this.userBean.getDto());
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
            eventModel.addEvent(event);

        } else {
            eventModel.updateEvent(event);
            EventDTO toAdd = getFromScheduleEvent(event);
            try {
                eventDAO.updateEvent(toAdd);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }

        }

        this.init();


        event = new DefaultScheduleEvent<>();
    }

    private static ScheduleEvent createAllEvent(List<ScheduleEvent> scheduleEvents) {

        List<MealDTO> allMealsOfDay = new ArrayList<>();

        for (ScheduleEvent event : scheduleEvents) {
            List<MealDTO> mealsOfEvent = (List<MealDTO>) event.getData();
            MealDTO all = createTotal(mealsOfEvent);
            allMealsOfDay.add(all);
        }

        MealDTO all = createTotal(allMealsOfDay);

        ScheduleEvent toReturn = DefaultScheduleEvent.builder()

                .title("Total: " + all.getFats() + "F, " + all.getProtein() + "P, " + all.getCarbs() + "C, " + all.getCalories() + "Calories")
                .startDate(scheduleEvents.get(0).getStartDate())
                .endDate(scheduleEvents.get(0).getStartDate())
                .draggable(false)
                .resizable(false)
                .editable(false)
                .overlapAllowed(true)
                .allDay(true)
                .backgroundColor("#25e712")
                .build();


        return toReturn;
    }

    public void removeMealFromEvent() {
        EventDTO eventDTO = new EventDTO();
        eventDTO.seteID(Integer.parseInt(this.event.getId()));


        MealDTO mealDTO = (MealDTO) this.eventMealsDataModel.getRowData();

        System.out.println(mealDTO.getmID());
        EventDAO eventDAO = new EventDAO();


        eventDAO.deleteMealFromEvent(eventDTO, mealDTO);


        this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
        this.eventMealsDataModel = new ListDataModel(this.eventMeals);


        this.userBean.setCurrentEventId(event.getId());

    }

    public void addMealToEvent() {

        EventDTO eventDTO = new EventDTO();
        eventDTO.seteID(Integer.parseInt(this.event.getId()));

        MealDTO mealDTO = (MealDTO) this.userMealDataModel.getRowData();

        EventDAO eventDAO = new EventDAO();

        try {
            eventDAO.addMealToEvent(eventDTO, mealDTO);
        } catch (DuplicateException e) {
            e.printStackTrace();
        }


        this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
        this.eventMealsDataModel = new ListDataModel(this.eventMeals);


        MealDAO mealDAO = new MealDAO();
        this.userMeals = mealDAO.getMealsByUserDTO(this.userBean.getDto());
        this.userMealDataModel = new ListDataModel(this.userMeals);

    }


    public void onViewChange(SelectEvent<String> selectEvent) {
        view = selectEvent.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "View Changed", "View:" + view);
        addMessage(message);
    }


    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }


    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {

        event = DefaultScheduleEvent.builder()
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
        if (event != null) {
            ScheduleEvent<?> event = eventModel.getEvent(eventId);
            eventModel.deleteEvent(event);
        }
    }


    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }


    public void allowedAndLoggedIn() {

        FacesContext fc = FacesContext.getCurrentInstance();

        if (this.userBean.getDto().getUserID() == null) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication()
                    .getNavigationHandler();
            nav.performNavigation(FaceletPath.LOGIN.getPath());
        }


    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        if (event.isEditable()) {


            EventDAO eventDAO = new EventDAO();
            EventDTO eventDTO = new EventDTO();
            eventDTO.seteID(Integer.parseInt(this.event.getId()));
            this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
            this.eventMealsDataModel = new ListDataModel(this.eventMeals);


            MealDAO mealDAO = new MealDAO();
            this.userMeals = mealDAO.getMealsByUserDTO(this.userBean.getDto());
            this.userMealDataModel = new ListDataModel(this.userMeals);


            PrimeFaces.current().executeInitScript("showDialog();");
            this.userBean.setCurrentEventId(event.getId());


        }


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


}
