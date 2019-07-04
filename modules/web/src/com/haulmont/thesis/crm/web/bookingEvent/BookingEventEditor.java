package com.haulmont.thesis.crm.web.bookingEvent;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DatasourceListener;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.BookingEventDetailStatusEnum;
import com.haulmont.thesis.crm.enums.YearEnum;
import com.haulmont.thesis.web.ui.basicdoc.editor.AbstractDocEditor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class BookingEventEditor<T extends BookingEvent> extends AbstractDocEditor<T> {

    @Inject
    protected Label areaLabel, globalLab, optionDateLabel, publishAllowedDateLabel;
    @Inject
    protected BoxLayout docInfo;
    @Inject
    protected TabSheet detailsTabsheet;

    @Named("detailsDs")
    protected CollectionDatasource<BookingEventDetail, UUID> detailsDs;
    @Named("project")
    protected SearchPickerField projectField;
    @Named("detailTable")
    protected Table detailTable;
    @Named("createBtn")
    protected Button createBtn;
    @Named("removeBtn")
    protected Button removeBtn;
    @Named("globalCheckBox")
    protected CheckBox globalCheckBox;
    @Named("hOptionDate")
    protected BoxLayout hOptionDate;
    @Named("option")
    protected CheckBox option;
    @Named("optionDate")
    protected DateField optionDate;
    @Named("publishAllowed")
    protected CheckBox publishAllowed;
    @Named("publishAllowedDate")
    protected DateField publishAllowedDate;

    @Inject
    protected Metadata metadata;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    protected void initListeners(){

        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, @Nullable Object prevValue, @Nullable final Object value) {
                if ("project".equals(property) && PersistenceHelper.isNew(getItem())){
                    if (value != null && !getItem().getTemplate()) {
                        Project project = (Project) value;
                        loadBookingEvent(project);
                        loadBookingEventDetail(project);
                        sumArea();
                    }
                }
            }
        });

        detailsDs.addListener(new DatasourceListener<BookingEventDetail>() {
            @Override
            public void itemChanged(Datasource<BookingEventDetail> ds, @Nullable BookingEventDetail prevItem, @Nullable BookingEventDetail item) {
                sumArea();
            }

            @Override
            public void stateChanged(Datasource<BookingEventDetail> ds, Datasource.State prevState, Datasource.State state) {
            }

            @Override
            public void valueChanged(BookingEventDetail source, String property, @Nullable Object prevValue, @Nullable Object value) {
                if ("area".equals(property)) {
                    sumArea();
                }

                if ("room".equals(property)) {
                    source.setArea(((Room) value).getTotalGrossArea());
                    initOttionDate(source);
                }

                if (source.getStatus() != null && (prevValue == null || !prevValue.equals(value)) &&
                        (source.getStatus().equals(BookingEventDetailStatusEnum.APPROVED) ||
                        source.getStatus().equals(BookingEventDetailStatusEnum.REMOVED))) {
                        source.setStatus(BookingEventDetailStatusEnum.EDITED);
                }
            }
        });

        option.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                boolean v = value != null ? (Boolean)value : false;

                if (!v) getItem().setOptionDate(null);

                visibleOption(v);
            }
        });

        publishAllowed.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                boolean v = value != null ? (Boolean)value : false;

                if (!v) getItem().setPublishAllowedDate(null);

                visiblePublishAllowed(v);
            }
        });
    }



    @Override
    protected void postInit() { //Обработчик: после загрузки карточки
        super.postInit();
        initListeners();
        visibleOption(getItem().getIsOption());
        visiblePublishAllowed(getItem().getIsPublishAllowed());
        removeBtn.setAction(RemoveAction());
        globalCheckBox.setVisible(getItem().getTemplate());
        globalLab.setVisible(getItem().getTemplate());
    }


    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        if (item.getOwner() == null) {
            item.setOwner(orgStructureService.getEmployeeByUser(userSession.getCurrentOrSubstitutedUser()));
        }
    }

    @Override
    public void ready() {
        applyAccessDataEditable();
    }

    protected boolean superRole() {
        return  (getAccessData().getGlobalEditable() || userSessionTools.isCurrentUserInRole("bookingSpecialist"));
    }

    protected void visibleOption(boolean isVisible) {
        optionDateLabel.setVisible(isVisible);
        optionDate.setVisible(isVisible);
        optionDate.setRequired(isVisible);
        option.setValue(isVisible);
    }

    protected void applyAccessDataEditable() {
        boolean editable = superRole();
        enabled(editable);
        detailTable.setEditable(editable);
        createBtn.setEnabled(editable);
        removeBtn.setEnabled(editable);
        projectField.setEditable(PersistenceHelper.isNew(getItem()));
    }

    protected void visiblePublishAllowed(boolean isVisible) {
        publishAllowedDateLabel.setVisible(isVisible);
        publishAllowedDate.setVisible(isVisible);
        publishAllowedDate.setRequired(isVisible);
        publishAllowed.setValue(isVisible);
    }

    public void createDetailItem() {
        BookingEventDetail bookingEventDetail = metadata.create(BookingEventDetail.class);
        bookingEventDetail.setBookingEvent(getItem());
        bookingEventDetail.setStatus(BookingEventDetailStatusEnum.NEW);
        bookingEventDetail.setProject((ExtProject) getItem().getProject());
        detailsDs.addItem(bookingEventDetail);
    }

    protected void loadBookingEvent(Project project) {
        ExtProject extProject = getProject(project.getId());
        getItem().setCompany(extProject.getOrganizer());
        getItem().setThemes(extProject.getThemes());
        getItem().setExhibitSpace(extProject.getExhibitSpace());
        getItem().setName(extProject.getName());
        getItem().setFullName_ru(extProject.getFullName_ru());
        getItem().setFullName_en(extProject.getFullName_en());
        getItem().setInstallationDate(extProject.getInstallationDateFact());
        getItem().setDateStart(extProject.getDateStartFact());
        getItem().setDateFinish(extProject.getDateFinishFact());
        getItem().setDeinstallationDate(extProject.getDeinstallationDateFact());
        getItem().setYear(getYearEnum(extProject.getDateStartFact()));
    }

    protected void loadBookingEventDetail(Project project) {
        if (detailsDs.size() > 0)
            detailsDs.clear();

        ExtProject extProject = getProject(project.getId());

        for (ProjectRoom projectRoom:extProject.getRoom()) {
            BookingEventDetail bookingEventDetail = metadata.create(BookingEventDetail.class);
            bookingEventDetail.setBookingEvent(getItem());
            bookingEventDetail.setStatus(BookingEventDetailStatusEnum.APPROVED);
            bookingEventDetail.setRoom(projectRoom.getRoom());
            bookingEventDetail.setArea(projectRoom.getArea());
            bookingEventDetail.setInstallationDate(projectRoom.getInstallationDate());
            bookingEventDetail.setStartDate(projectRoom.getStartDate());
            bookingEventDetail.setEndDate(projectRoom.getEndDate());
            bookingEventDetail.setDeinstallationDate(projectRoom.getDeinstallationDate());
            bookingEventDetail.setOptionDate(projectRoom.getOptionDate());
            detailsDs.addItem(bookingEventDetail);
        }
    }

    protected ExtProject getProject(UUID id) {
        LoadContext loadContext = new LoadContext(ExtProject.class).setView("ext-edit").setId(id);
        return getDsContext().getDataSupplier().load(loadContext);
    }

    protected YearEnum getYearEnum(Date date) {
        YearEnum result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);

            result = YearEnum.fromId(year);
        }
        return result != null ? result : YearEnum.Y_2025;
    }

    private void initOttionDate(BookingEventDetail source) {
        if (getItem().getIsOption())
            source.setOptionDate(getItem().getOptionDate());
    }

    protected Action RemoveAction()  {
        return new RemoveAction(detailTable) {
            @Override
            public void actionPerform(Component component) {
                optionDialog();
            }
        };
    }

    protected void optionDialog() {
        final String messagesPackage = AppConfig.getMessagesPack();
        showOptionDialog(
                messages.getMessage(messagesPackage, "dialogs.Confirmation"),
                messages.getMessage(messagesPackage, "dialogs.Confirmation.Remove"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY) {
                            @Override
                            public void actionPerform(Component component) {
                                removeItem();
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL) {
                            @Override
                            public void actionPerform(Component component) {
                                // move focus to owner
                                //selected.requestFocus();
                            }
                        }
                }
        );
    }

    private void removeItem() {
        BookingEventDetail bookingEventDetail = detailTable.getSingleSelected();
        if (!bookingEventDetail.getStatus().equals(BookingEventDetailStatusEnum.NEW)) {
            bookingEventDetail.setStatus(BookingEventDetailStatusEnum.REMOVED);
        } else {
            detailsDs.removeItem(bookingEventDetail);
        }
    }

    @Override
    protected boolean preCommit() {

        if (getItem().getTemplate()) {
            return super.preCommit();
        }

        for (BookingEventDetail bookingEventDetail:detailsDs.getItems()) {
            if (bookingEventDetail.getRoom() == null) {
                messageInfo("Заполните поле \"Помещение\"");
                return false;
            }
            if (bookingEventDetail.getArea() == null) {
                messageInfo("Заполните поле \"Площадь\"");
                return false;
            }
            if (bookingEventDetail.getInstallationDate() == null) {
                messageInfo("Заполните поле \"Дата начала монтажа\"");
                return false;
            }
            if (bookingEventDetail.getStartDate() == null) {
                messageInfo("Заполните поле \"Дата начала проведения\"");
                return false;
            }
            if (bookingEventDetail.getEndDate() == null) {
                messageInfo("Заполните поле \"Дата завершения\"");
                return false;
            }
            if (bookingEventDetail.getDeinstallationDate() == null) {
                messageInfo("Заполните поле \"Дата окончания демонтажа\"");
                return false;
            }
            if (bookingEventDetail.getBookingEvent() == null) {
                messageInfo("Заполните поле \"Заявка на загрузку\"");
                return false;
            }
        }

        getItem().setTheme(buildTheme());

        return super.preCommit();
    }

    protected void messageInfo(String masage) {
        showNotification(masage, NotificationType.TRAY);
    }

    protected void sumArea() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (BookingEventDetail bookingEventDetail:detailsDs.getItems()) {
            if (bookingEventDetail.getArea() != null && !BookingEventDetailStatusEnum.REMOVED.equals(bookingEventDetail.getStatus())) {
                sum = sum.add(bookingEventDetail.getArea());
            }
        }
        getItem().setArea(sum);
    }

    protected String buildTheme() {
        StringBuilder buildTheme = new StringBuilder();
        buildTheme.append(getItem().getDocKind().getName());
        if (getItem().getNumber() != null) {
            buildTheme.append(String.format(" №%s от %s", getItem().getNumber(), formatDate()));
        }
        if (getItem().getProject() != null) {
            buildTheme.append(String.format(" [%s]", getItem().getProject().getName()));
        }

        return buildTheme.toString();
    }

    protected String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(getItem().getDateTime());
    }

    @Override
    protected void initDocHeader(Doc doc){
        super.initDocHeader(doc);
        if (doc.getTemplate()) {
            if (getAccessData() != null && getAccessData().getTemplate()) {
                removeTabIfExist(detailsTabsheet, "detailTab");
            }
        } else {
            String[] fildsRequired = {"project", "year", "company", "themes", "exhibitSpace", "name", "fullName_ru",
                    "installationDate", "dateFinish", "dateStart", "deinstallationDate"};
            required(fildsRequired, true);
        }
    }

    protected void required(String[] filds, boolean required) {
        for (Component comp : docInfo.getComponents()) {
            if (comp instanceof Field) {
                Field field = (Field) comp;
                if (filds.length > 0) {
                    for (String f:filds) {
                        if (f.equals(field.getId())) {
                            field.setRequired(required);
                        }
                    }
                } else {
                    field.setRequired(required);
                }
            }
        }
    }

    protected void enabled(boolean enabled) {
        for (Component comp : docInfo.getComponents()) {
            if (comp instanceof Field) {
                Field field = (Field) comp;
                field.setEditable(enabled);
            }
        }
    }


}