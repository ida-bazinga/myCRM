package com.haulmont.thesis.crm.web.ui.contactperson;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserUtils;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.ExtContactPerson;
import com.haulmont.thesis.crm.entity.LineOfBusiness;
import com.haulmont.thesis.crm.entity.LineOfBusinessContactPerson;
import com.haulmont.thesis.crm.web.ui.avatarimage.ExtAvatarImageFrame;
import com.haulmont.thesis.web.ui.entitylogframe.EntityLogFrame;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ContactPersonEditor<T extends ExtContactPerson> extends AbstractEditor<T> {

    protected ExtAvatarImageFrame embeddedImageFrame;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("tabSheet")
    protected TabSheet tabSheet;

    @Named("contactDs")
    protected Datasource<ExtContactPerson> contactDs;

    @Named("communicationsTable")
    protected GroupTable communicationsTable;

    @Named("lineOfBusinessContactPersonDs")
    protected CollectionDatasource<LineOfBusinessContactPerson, UUID> lineOfBusinessContactPersonDs;

    @Inject
    protected Metadata metadata;


    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(1000).setHeight(800).setResizable(true);
        contactDs.addListener(new DsListenerAdapter<ExtContactPerson>() {
            @Override
            public void valueChanged(ExtContactPerson source, String property, Object prevValue, Object value) {
                if (!"firstName".equals(property) && !"lastName".equals(property) && !"middleName".equals(property)) {
                    return;
                }

                String firstName = contactDs.getItem().getValue("firstName");
                String lastName = contactDs.getItem().getValue("lastName");
                String middleName = contactDs.getItem().getValue("middleName");

                try {
                    source.setName(UserUtils.formatName("{LL| }{FF| }{MM}", firstName, lastName, middleName));
                    source.setFullName(UserUtils.formatName("{LL| }{F|.}{M|.}", firstName, lastName, middleName));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        communicationsTable.addGeneratedColumn("maskedAddress", new Table.ColumnGenerator<Communication>() {
            @Override
            public Component generateCell(Communication entity) {

                String linkT = (entity.getCommKind().getLinkTemplate() != null) ? entity.getCommKind().getLinkTemplate() : "about:blank";
                Link field = componentsFactory.createComponent(Link.NAME);

                field.setCaption(entity.getMaskedAddress());
                field.setUrl(String.format(linkT, entity.getMainPart()));
                field.setTarget("_blank");

                return field;
            }
        });
        initLazyTabs();
        //if (params.get("company") != null) {
        //    getItem().setCompany((Company) params.get("company"));
        //}
    }

    @Override
    public void ready() {
        communicationsTable.expandAll();
    }

    @Override
    protected void postInit() {
        super.postInit();
        initLazyTabs();
        initPhoto();

        this.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (WINDOW_CLOSE.equals(actionId) && embeddedImageFrame.isAvatarNew()) {
                    embeddedImageFrame.removeAvatar();
                }
            }
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (getItem().getFirstName() == null && getItem().getLastName() == null) {
            errors.add(getMessage("emptyNames"));
        }
    }

    @Override
    public void commitAndClose() {
        if (embeddedImageFrame != null)
            if (!embeddedImageFrame.commit()) {
                showNotification(getMessage("avatarMsg"), NotificationType.WARNING);
                return;
            }
        super.commitAndClose();
    }

    public void initPhoto() {
        embeddedImageFrame = getComponent("embeddedImageFrame");
        embeddedImageFrame.setMessagesPack(getMessagesPack());
        embeddedImageFrame.setImageProperty("photo");
        embeddedImageFrame.setImageProperty("avatar");
        embeddedImageFrame.init();
        embeddedImageFrame.setImageSize(195, 195);
        embeddedImageFrame.setItem(getItem());
    }

    private void initLazyTabs() {
        tabSheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                if ("contactLogTab".equals(newTab.getName())) {
                    EntityLogFrame contactLogFrame = getComponentNN("contactLogFrame");
                    contactLogFrame.init(Collections.<String, Object>singletonMap(WindowParams.ITEM.toString(), getItem()));
                }
            }
        });
    }

    public void createLineOfBusines() {
        openLookup("crm$LineOfBusiness.browse", new Lookup.Handler() {
            public void handleLookup(Collection items) {
                if (items != null && items.size() > 0) {
                    for (Object obj:items) {
                        LineOfBusiness lineOfBusiness = (LineOfBusiness)obj;
                        if(!isLineOfBusinessCompaniesFlags(lineOfBusiness.getId())) {
                            LineOfBusinessContactPerson newLineOfBusinessContactPerson = metadata.create(LineOfBusinessContactPerson.class);
                            newLineOfBusinessContactPerson.setContactPerson(contactDs.getItem());
                            newLineOfBusinessContactPerson.setLineOfBusiness(lineOfBusiness);
                            lineOfBusinessContactPersonDs.addItem(newLineOfBusinessContactPerson);
                        }
                    }
                }
            }
        }, WindowManager.OpenType.NEW_TAB);
    }

    private boolean isLineOfBusinessCompaniesFlags(UUID Id) {
        boolean isResult = false;
        for (LineOfBusinessContactPerson lineOfBusinessContactPerson:lineOfBusinessContactPersonDs.getItems()) {
            if (lineOfBusinessContactPerson.getLineOfBusiness() != null) {
                if (lineOfBusinessContactPerson.getLineOfBusiness().getId().compareTo(Id) == 0) {
                    isResult = true;
                }
            }
        }
        return isResult;
    }


}