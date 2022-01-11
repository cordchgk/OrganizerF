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


import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.schedule.event.daos.EventDAO;
import organizer.schedule.event.dtos.EventDTO;
import organizer.schedule.schedule.service.ExtenderService;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static organizer.schedule.event.dtos.EventDTO.getFromScheduleEvent;

@Named("scheduleBean")
@ViewScoped
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
                    .borderColor("#CB4335")
                    .data(eventDTO.getMealDTOList())
                    .build();

            eventModel.addEvent(event);

        }
        HashMap<LocalDate, List<ScheduleEvent>> hashMap = new HashMap();

        for (ScheduleEvent event : this.eventModel.getEvents()) {

            if (hashMap.containsKey(event.getStartDate().toLocalDate())) {
                hashMap.get(event.getStartDate().toLocalDate()).add(event);
            } else {

                hashMap.put(event.getStartDate().toLocalDate(), new ArrayList<>());
                hashMap.get(event.getStartDate().toLocalDate()).add(event);
            }

        }

        for (LocalDate localDate : hashMap.keySet()) {
            ScheduleEvent e = createAllEvent(hashMap.get(localDate));

            eventModel.addEvent(e);
        }


        MealDAO mealDAO = new MealDAO();
        this.userMeals = mealDAO.getMealsByUserDTO(this.userBean.getDto());
        this.userMealDataModel = new ListDataModel(this.userMeals);




        this.event = this.eventModel.getEvent(this.userBean.getCurrentEventId());

        EventDAO eventDAO = new EventDAO();
        EventDTO eventDTO = new EventDTO();
        if (this.userBean.getCurrentEventId() != null){
            eventDTO.seteID(Integer.parseInt(this.userBean.getCurrentEventId()));

            this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
            this.eventMealsDataModel = new ListDataModel(this.eventMeals);

        }


        extenderExamples = extenderService.createExtenderExamples();
    }


    private void newStuff() {

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
                .borderColor("#CB4335")

                .build();


        return toReturn;
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


    public DataModel getEventMealsDataModel() {
        return eventMealsDataModel;
    }

    public void setEventMealsDataModel(DataModel eventMealsDataModel) {
        this.eventMealsDataModel = eventMealsDataModel;
    }

    public ExtenderService getScheduleExtenderService() {
        return extenderService;
    }

    public void setScheduleExtenderService(ExtenderService extenderService) {
        this.extenderService = extenderService;
    }

    public LocalDateTime getRandomDateTime(LocalDateTime base) {
        LocalDateTime dateTime = base.withMinute(0).withSecond(0).withNano(0);
        return dateTime.plusDays(((int) (Math.random() * 30)));
    }


    public ScheduleModel getEventModel() {
        if (eventModel == null) {
            eventModel = new DefaultScheduleModel();
        }

        return eventModel;
    }

    public DataModel getUserMealDataModel() {
        return userMealDataModel;
    }

    public void setUserMealDataModel(DataModel userMealDataModel) {
        this.userMealDataModel = userMealDataModel;
    }

    private LocalDateTime previousDay8Pm() {
        return LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime previousDay11Pm() {
        return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today1Pm() {
        return LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime theDayAfter3Pm() {
        return LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today6Pm() {
        return LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay9Am() {
        return LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay11Am() {
        return LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime fourDaysLater3pm() {
        return LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime sevenDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime eightDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public LocalDate getInitialDate() {
        return LocalDate.now().plusDays(1);
    }

    public ScheduleEvent<?> getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent<?> event) {
        this.event = event;
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

    public void onEventSelect(SelectEvent selectEvent) {


        event = (ScheduleEvent) selectEvent.getObject();

        EventDAO eventDAO = new EventDAO();
        EventDTO eventDTO = new EventDTO();
        eventDTO.seteID(Integer.parseInt(this.event.getId()));
        this.eventMeals = eventDAO.selectMealsByEventDTO(eventDTO);
        this.eventMealsDataModel = new ListDataModel(this.eventMeals);


        MealDAO mealDAO = new MealDAO();
        this.userMeals = mealDAO.getMealsByUserDTO(this.userBean.getDto());
        this.userMealDataModel = new ListDataModel(this.userMeals);

        if (event.isEditable()) {


            PrimeFaces.current().executeInitScript("showDialog();");
        }
        this.userBean.setCurrentEventId(event.getId());


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


    public ExtenderService getExtenderService() {
        return extenderService;
    }

    public void setExtenderService(ExtenderService extenderService) {
        this.extenderService = extenderService;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void setExtenderExamples(Map<String, ExtenderService.ExtenderExample> extenderExamples) {
        this.extenderExamples = extenderExamples;
    }

    public List<MealDTO> getUserMeals() {
        return userMeals;
    }

    public void setUserMeals(List<MealDTO> userMeals) {
        this.userMeals = userMeals;
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {

        event = DefaultScheduleEvent.builder()
                .editable(true)
                .startDate(selectEvent.getObject())
                .endDate(selectEvent.getObject().plusHours(1))
                .data(new ArrayList<MealDTO>())
                .build();

    }

    public List<MealDTO> getEventMeals() {
        return eventMeals;
    }

    public void setEventMeals(List<MealDTO> eventMeals) {
        this.eventMeals = eventMeals;
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

    public void onExtenderExampleSelect(AjaxBehaviorEvent event) {
        ExtenderService.ExtenderExample example = (ExtenderService.ExtenderExample) getExtenderExample();
        if (!"custom".equals(selectedExtenderExample) && example != null) {
            if (example.getDetails() != null && !example.getDetails().isEmpty()) {
                FacesMessage message = new FacesMessage(example.getName(), example.getDetails());
                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), message);
            }
            this.extenderCode = example.getValue();
        }
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public boolean isShowWeekends() {
        return showWeekends;
    }

    public void setShowWeekends(boolean showWeekends) {
        this.showWeekends = showWeekends;
    }

    public boolean isSlotEventOverlap() {
        return slotEventOverlap;
    }

    public void setSlotEventOverlap(boolean slotEventOverlap) {
        this.slotEventOverlap = slotEventOverlap;
    }

    public boolean isShowWeekNumbers() {
        return showWeekNumbers;
    }

    public void setShowWeekNumbers(boolean showWeekNumbers) {
        this.showWeekNumbers = showWeekNumbers;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public boolean isTooltip() {
        return tooltip;
    }

    public void setTooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isRtl() {
        return rtl;
    }

    public void setRtl(boolean rtl) {
        this.rtl = rtl;
    }

    public boolean isAllDaySlot() {
        return allDaySlot;
    }

    public void setAllDaySlot(boolean allDaySlot) {
        this.allDaySlot = allDaySlot;
    }

    public double getAspectRatio() {
        return aspectRatio == 0 ? Double.MIN_VALUE : aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getLeftHeaderTemplate() {
        return leftHeaderTemplate;
    }

    public void setLeftHeaderTemplate(String leftHeaderTemplate) {
        this.leftHeaderTemplate = leftHeaderTemplate;
    }

    public String getCenterHeaderTemplate() {
        return centerHeaderTemplate;
    }

    public void setCenterHeaderTemplate(String centerHeaderTemplate) {
        this.centerHeaderTemplate = centerHeaderTemplate;
    }

    public String getRightHeaderTemplate() {
        return rightHeaderTemplate;
    }

    public void setRightHeaderTemplate(String rightHeaderTemplate) {
        this.rightHeaderTemplate = rightHeaderTemplate;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getNextDayThreshold() {
        return nextDayThreshold;
    }

    public void setNextDayThreshold(String nextDayThreshold) {
        this.nextDayThreshold = nextDayThreshold;
    }

    public String getWeekNumberCalculation() {
        return weekNumberCalculation;
    }

    public void setWeekNumberCalculation(String weekNumberCalculation) {
        this.weekNumberCalculation = weekNumberCalculation;
    }

    public String getWeekNumberCalculator() {
        return weekNumberCalculator;
    }

    public void setWeekNumberCalculator(String weekNumberCalculator) {
        this.weekNumberCalculator = weekNumberCalculator;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(String slotDuration) {
        this.slotDuration = slotDuration;
    }

    public String getSlotLabelInterval() {
        return slotLabelInterval;
    }

    public void setSlotLabelInterval(String slotLabelInterval) {
        this.slotLabelInterval = slotLabelInterval;
    }

    public String getSlotLabelFormat() {
        return slotLabelFormat;
    }

    public void setSlotLabelFormat(String slotLabelFormat) {
        this.slotLabelFormat = slotLabelFormat;
    }

    public String getDisplayEventEnd() {
        return displayEventEnd;
    }

    public void setDisplayEventEnd(String displayEventEnd) {
        this.displayEventEnd = displayEventEnd;
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getClientTimeZone() {
        return clientTimeZone;
    }

    public void setClientTimeZone(String clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
    }

    public String getColumnHeaderFormat() {
        return columnHeaderFormat;
    }

    public void setColumnHeaderFormat(String columnHeaderFormat) {
        this.columnHeaderFormat = columnHeaderFormat;
    }

    public ExtenderService.ExtenderExample getExtenderExample() {
        return extenderExamples.get(selectedExtenderExample);
    }

    public String getSelectedExtenderExample() {
        return selectedExtenderExample;
    }

    public void setSelectedExtenderExample(String selectedExtenderExample) {
        this.selectedExtenderExample = selectedExtenderExample;
    }

    public String getExtenderCode() {
        return extenderCode;
    }

    public void setExtenderCode(String extenderCode) {
        this.extenderCode = extenderCode;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public List<SelectItem> getExtenderExamples() {
        return extenderExamples.values().stream() //
                .sorted(Comparator.comparing(ExtenderService.ExtenderExample::getName)) //
                .map(example -> new SelectItem(example.getKey(), example.getName())) //
                .collect(Collectors.toList());
    }

    public String getServerTimeZone() {
        return serverTimeZone;
    }

    public void setServerTimeZone(String serverTimeZone) {
        this.serverTimeZone = serverTimeZone;
    }


    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public void allowedAndLoggedIn() {

        FacesContext fc = FacesContext.getCurrentInstance();

        if (this.userBean.getDto().getUserID() == null) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication()
                    .getNavigationHandler();
            nav.performNavigation("/access/access-denied.xhtml");
        }


    }


}
