package com.haulmont.thesis.crm.web.ordDoc;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.entity.PermissionType;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.crm.web.invDoc.surcharge.SurchargeInvoice;
import com.haulmont.thesis.crm.web.ui.basicdoc.SalesAbstractDocEditor;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesActAction;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesInvoiceAction;
import com.haulmont.thesis.crm.core.app.mathematic.ProductCalculate;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class OrdDocEditor<T extends OrdDoc> extends SalesAbstractDocEditor<T> {

    @Named("detailsDs")
    protected CollectionDatasource<OrderDetail, UUID> detailsDs;
    @Named("project")
    protected SearchPickerField project;
    @Inject
    protected LookupPickerField owner;
    @Named("createSubDocButton")
    protected PopupButton createSubDocButton;

    @Inject
    private DataManager dataManager;
    @Inject
    protected LookupField currency, docCategory;
    @Inject
    protected PickerField company, contract;
    @Inject
    protected SplitPanel split;
    @Inject
    protected GroupTable orderDetailTable;
    @Inject
    private Label cur1, cur2, cur3;
    @Inject
    protected DocumentCopySupport documentCopySupport;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected HBoxLayout mainPane;
    @Inject
    private TextField searchSimpleText;
    @Inject
    private ButtonsPanel detailBtnPnl;
    @Inject
    private VBoxLayout docInfo;
    @Inject
    private ScrollBoxLayout actionsFrameScroll;
    @Inject
    protected CrmConfig crmConfig;

    private boolean isCopy;
    private int priority;



    @Override
    protected void initDocHeader(Doc doc){
        super.initDocHeader(doc);
        if (doc.getTemplate()) {
            setHeaderComponentVisible("procStateBox", false);
            setHeaderComponentVisible("companyLabel", false);
            setHeaderComponentVisible("company", false);
            setHeaderComponentVisible("companyAccountableLabel", false);
            setHeaderComponentVisible("companyAccountable", false);
            setHeaderComponentVisible("contractLabel", false);
            setHeaderComponentVisible("contract", false);
            setHeaderComponentVisible("parentCardLabel", false);
            setHeaderComponentVisible("parentCard", false);
            setHeaderComponentVisible("taxSumLabel", false);
            setHeaderComponentVisible("taxSum", false);
            setHeaderComponentVisible("fullSumLabel", false);
            setHeaderComponentVisible("fullSum", false);
            setHeaderComponentVisible("printInEnglishLabel", false);
            setHeaderComponentVisible("printInEnglish", false);
            setHeaderComponentVisible("printSingleLineLabel", false);
            setHeaderComponentVisible("printSingleLine", false);
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        isCopy = BooleanUtils.isTrue((Boolean) params.get("copy"));
        orderDetailTable.addAction(EditAction());
        orderDetailTable.addAction(RemoveAction());
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "correspondenceHistoryTab,docLogTab,office,cardLinksTab,securityTab,versionsTab";
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        if (!isTemplate) {
            printButton.addAction(new PrintReportAction("printExecutionList", this, "printDocExecutionListReportName"));
            createSubDocButton.addAction(new CreateSalesInvoiceAction(this));
            createSubDocButton.addAction(new CreateSalesActAction(this));
            createSubDocButton.addAction(SurchargeAction());
        }
        createSubDocButton.setVisible(!isTemplate);

    }

    @Override
    protected Component createState() {
        if (WfUtils.isCardInState(getItem(), "New") || StringUtils.isEmpty(getItem().getState())) {
            Label label = componentsFactory.createComponent(Label.NAME);
            label.setValue(StringUtils.isEmpty(getItem().getState()) ? "" : getItem().getLocState());
            return label;
        } else {
            return super.createState();
        }
    }

    @Override
    public void ready(){
        super.ready();
        orderDetailTable.expandAll();
        copyOrdDocAndCommit(); //копирование заказа. Загрузка услуг из копируемого заказа.
    }

    @Override
    protected void createCreateSubCardActions() {
        super.createCreateSubCardActions();
        createSubCardButton.setVisible(false);
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("office", getMessage("office"));
        hiddenTabs.put("attachmentsTab", getMessage("attachmentsTab"));
        hiddenTabs.put("docTreeTab", getMessage("docTreeTab"));
        hiddenTabs.put("cardCommentTab", getMessage("cardCommentTab"));
        super.fillHiddenTabs();
    }

    @Override
    protected void postInit() { //Обработчик: после загрузки карточки
        super.postInit();       ;
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1; //приоритет отправки в 1С
        project.setRequired(!isTemplate);
        company.setRequired(!isTemplate);

        owner.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                OrdDoc item = getItem();
                if (item.getOwner() != null) {
                    item.setDepartment(item.getOwner().getDepartment());
                }
            }
        }
        );

        //Проверка на дочерние документы и статус в 1С
        //if (hasDependentDocs() || hasExternals()) {
        //    company.setEditable(false);
        //}
        initEditWindow();
    }

    protected void copyOrdDocAndCommit() { //копирование заказ и сохранеие
        OrdDoc item = getItem();
        if(isCopy && PersistenceHelper.isNew(item)) { //копирование заказа. Загрузка услуг из копируемого заказа.
             //загрузка услуг
            if(item.getOrderDetails() != null) {
                for (OrderDetail orderDetail : item.getOrderDetails()) {
                    detailsDs.addItem(orderDetail);
                }
            }
            super.commit(); //сохрание заказ
        }
    }

    private void initProject(OrdDoc item) {
        project.removeAction(project.getAction(PickerField.LookupAction.NAME));
        final PickerField.LookupAction projectLookupAction = new PickerField.LookupAction(project) {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
            }
        };
        projectLookupAction.setLookupScreen("tm$Project.lookup");
        projectLookupAction.setLookupScreenParams(Collections.<String, Object>singletonMap("organization", item.getOrganization().getId()));
        project.addAction(projectLookupAction);
        project.addOpenAction();
    }

    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        currency.setEnabled(true);
        item.setPrintInEnglish(false);
        item.setPrintSingleLine(false);
        item.setParentCardAccess(true);
        if (!isCopy) {
            item.setCurrency(crmConfig.getCurrencyRub());//Валюта
            item.setOwner(orgStructureService.getEmployeeByUser(userSession.getCurrentOrSubstitutedUser()));
        }
    }

    @Override
    protected void adjustForVersion() {
        if (getAccessData() != null) {
            Component resolutionsPane = getComponentNN("resolutionsPane");
            if (getAccessData().getTemplate()) {
                removeTabIfExist(tabsheet, "docLogTab");
                removeTabIfExist(tabsheet, "openHistoryTab");
                removeTabIfExist(tabsheet, "versionsTab");
                removeTabIfExist(tabsheet, "cardCommentTab");
                removeTabIfExist(tabsheet, "hierarchy");
                removeTabIfExist(tabsheet, "docTreeTab");
                removeTabIfExist(tabsheet, "cardLinksTab");
                resolutionsPane.setVisible(false);
                //((ThesisWebSplitPanel) split).hide();
                if (getComponent("print") != null) {
                    getComponentNN("print").setVisible(false);
                }
                disabledOtherActions();
                removedComment = true;
            }
            if (!getAccessData().getNotVersion()) {
                removeTabIfExist(tabsheet, "docLogTab");
                removeTabIfExist(tabsheet, "openHistoryTab");
                removeTabIfExist(tabsheet, "versionsTab");
                removeTabIfExist(tabsheet, "processTab");
                removeTabIfExist(tabsheet, "cardCommentTab");
                if (actionsFrame != null)
                    actionsFrame.setVisible(false);
                if (getComponent("actionsPane") != null)
                    getComponentNN("actionsPane").setVisible(false);
                if (getComponent("startProc") != null)
                    getComponentNN("startProc").setVisible(false);
                disabledOtherActions();
                resolutionsPane.setVisible(false);
                cardAttachmentsFrame.setEditable(false);
            }
        }
    }

    //создать договор
    public void standartContractButton() {
        if (validCreateContract("standartContractButton")) {
            OrdDoc item = getItem();
            boolean isContract = work1C.validate(item.getContract());
            Contract contract = createContract();
            if (work1C.validate(contract)) {
                //устанавливаем созданный договор и сохраняем заказ
                item.setContract(contract);
                //блокируем на изменение контрагента и проект
                initEditWindow();
                //проверка на заполнение поле договор
                if (isContract) {
                    //изменеие договора у связанных сущностей
                    updateContractInvDoc();
                    updateContractActDoc();
                }
            }
        }
    }

    //создать договор второй вариант.
    protected Contract createContract(){
        UUID templateId = UUID.fromString("0313a6d3-251c-e5c9-fb57-d9b08c9d9588");

        LoadContext loadContext = new LoadContext(Doc.class).setId(templateId).setView("templateBrowse");

        Doc template = dataService.load(loadContext);
        if (template == null) {
            throw new AccessDeniedException(PermissionType.ENTITY_OP, metadata.getClassNN(Contract.class).getName() + ":" + EntityOp.READ.getId());
        } else{
            template = documentCopySupport.reloadSrcDoc(template);
            return copyFrom(template, getItem());
        }
    }

    @Nonnull
    protected Contract copyFrom(@Nonnull Doc sourceDoc,@Nonnull OrdDoc item) {
        Preconditions.checkNotNullArgument(sourceDoc, "Template document is not specified");
        Preconditions.checkNotNullArgument(item, "Order is not specified");
        String nameCompany = item.getCompany().getFullName() != null ? item.getCompany().getFullName() : item.getCompany().getName() ;
        String nameProject = item.getProject().getName();
        Contract contract = metadata.create(Contract.class);
        contract.copyFrom(sourceDoc, toCommit);
        contract.setCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        contract.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        contract.setTemplate(false);
        contract.setNumber(item.getNumber());
        contract.setContractor(item.getCompany());
        contract.setDocCategory(crmConfig.getDocCategotyExhibitionProject());
        contract.setProject(item.getProject());
        contract.setOwner(item.getOwner());
        contract.setDepartment(item.getOwner().getDepartment());
        String comment = String.format("Договор c %s по проекту %s", nameCompany, nameProject);
        contract.setComment(comment);
        contract.setDescription(comment);
        contract.setCurrency(getCurrency(item.getCurrency().getCode()));
        //contract = fixContractCardRole(contract);
        return contract;
    }

    private com.haulmont.thesis.core.entity.Currency getCurrency(String code) {
        LoadContext loadContext = new LoadContext(com.haulmont.thesis.core.entity.Currency.class).setView("_local");
        loadContext.setQueryString("select e from df$Currency e where e.digitalCode = :code")
                .setParameter("code", code).setMaxResults(1);

        return dataManager.load(loadContext);
    }

    private boolean hasDependentDocs()
    {
        LoadContext loadContext = new LoadContext(Doc.class).setView("_local");
        loadContext.setQueryString("select e from df$Doc e where e.parentCard.id = :docId")
                .setParameter("docId", getItem().getId());

        if (dataManager.loadList(loadContext).size()>0)
        {
            return true;
        }
        return false;
    }

    private boolean hasExternals()
    {
        LoadContext loadContext = new LoadContext(IntegrationResolver.class).setView("_local");
        loadContext.setQueryString("select e from crm$IntegrationResolver e where e.entityId = :entId and e.extId is not null")
                .setParameter("entId", getItem().getId());

        if (dataManager.loadList(loadContext).size()>0)
        {
            return true;
        }
        return false;
    }

    //пометить к обмену
    public void exportButton() {
        if (getItem() != null) {

            if (!super.validateDoc(getItem())) {
                return;
            }

            if (!super.validateDetails(detailsDs.getItems())) {
                return;
            }

            if (!super.validateContract(getItem().getContract())) {
                return;
            }

            if (!super.validateProject(getItem().getProject())) {
                return;
            }

            if (!super.validateCompany(getItem().getCompany())) {
                return;
            }

            export();
        }
    }

    protected void export() {
        Map<String, Object> params = new HashMap<>();
        exportParentCompany();
        params.put(getItem().getProject().getMetaClass().getName(), getItem().getProject().getId());
        params.put(getItem().getCompany().getMetaClass().getName(), getItem().getCompany().getId());
        params.put(getItem().getMetaClass().getName(), getItem().getId());

        Boolean result = work1C.regLog(params, priority);
        if (result) {
            super.integrationResolverUpdateStatus(getItem().getId(),StatusDocSales.MARKER1C);
            String txtMesage = "Заказ отправлен в 1С";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        }
    }

    public void onAddProductBtnClick() {

        if (!super.validateDoc(getItem())) {
            return;
        }

        if (!super.validateProject(getItem().getProject())) {
            return;
        }

        if(getItem().getDocCategory() == null){
            String txtMesage = "Не выбрана категория!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }

        docCategory.setEditable(false);//блокируем на изменения
        project.setEditable(false);//блокируем на изменения
        Map<String, Object> params = new HashMap<>();
        final OrdDoc ordDoc = getItem();
        params.put("ordDoc", ordDoc);
        params.put("ordDocDetails", detailsDs.getItems());
        final Window window = openWindow("crm$Cost.select", WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                CollectionDatasource<OrderDetail, UUID> selectDs = window.getDsContext().get("selectDs");
                if (selectDs.size() > 0) {
                    Collection<OrderDetail> selectOrderDetailCollection = selectDs.getItems();
                    newDetailDs(selectOrderDetailCollection);
                    SaveOrdDocAndCalcFullSumTaxSum();
                }
            }
        });
    }

    private void newDetailDs(Collection<OrderDetail> selectOrderDetailCollection) {
        //удаляем из основного detailsDs, строки которых нет в selectDs
        ////есть уже в заказе. Нужно обновить данные        ;
        if (removeItemDetailsDs(removeItemsDetails(selectOrderDetailCollection))) {
            //новый. Добавить в ds
            for (OrderDetail selectOrderDetail : selectOrderDetailCollection) {
                //новый. Добавить в ds
                if (selectOrderDetail.getTempId() == null) {
                    detailsDs.addItem(selectOrderDetail);
                }
            }
        }
    }

    private void sinchronizeOrdDetail(OrderDetail ordDetail, OrderDetail selectOrdDetail) {
        ordDetail.setProduct(selectOrdDetail.getProduct());
        ordDetail.setAmount(selectOrdDetail.getAmount());
        ordDetail.setCost(selectOrdDetail.getCost());
        ordDetail.setTotalSum(selectOrdDetail.getTotalSum());
        ordDetail.setTaxSum(selectOrdDetail.getTaxSum());
        ordDetail.setOrdDoc(selectOrdDetail.getOrdDoc());
        ordDetail.setSumWithoutNds(selectOrdDetail.getSumWithoutNds());
        ordDetail.setAlternativeName_ru(selectOrdDetail.getAlternativeName_ru());
        ordDetail.setAlternativeName_en(selectOrdDetail.getAlternativeName_en());
        ordDetail.setDiscountSum(selectOrdDetail.getDiscountSum());
        ordDetail.setMarginSum(selectOrdDetail.getMarginSum());
        ordDetail.setDiscountType(selectOrdDetail.getDiscountType());
        ordDetail.setCostPerPiece(selectOrdDetail.getCostPerPiece());
        ordDetail.setSecondaryAmount(selectOrdDetail.getSecondaryAmount());
        ordDetail.setOrderDetailGroup(selectOrdDetail.getOrderDetailGroup());
    }

    private Set<OrderDetail> removeItemsDetails(Collection<OrderDetail> selectOrderDetailCollection) {
        Set<OrderDetail> setRemove = new HashSet<>();
        for (OrderDetail ordDetail : detailsDs.getItems()) {
            boolean isRemove = true;
            for (OrderDetail selectOrdDetail : selectOrderDetailCollection) {
                if (selectOrdDetail.getTempId() != null) {
                    if (ordDetail.getId().equals(selectOrdDetail.getTempId())) {
                        sinchronizeOrdDetail(ordDetail, selectOrdDetail); //есть уже в заказе. Нужно обновить данные
                        isRemove = false;
                    }
                }
            }
            if(isRemove) {
                setRemove.add(ordDetail);
            }
        }
        return setRemove;
    }

    private boolean removeItemDetailsDs(Set<OrderDetail> setRemove) {
        if(setRemove.size() > 0) {
            for (OrderDetail orderDetailRemove:setRemove) {
                detailsDs.removeItem(orderDetailRemove);
            }
        }
        return true;
    }

    public void replaceCurrancyButton() {

        if(work1C.validate(getItem().getIntegrationResolver())) {
            if (!validateReplaceCurrancy()) {
                String txtMesage = "Смена валюты возможн при условии, что Заказ имеет статус Новый, а также по данному заказу нет Договора, Счета и Акта!";
                showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                return;
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("currency", getItem().getCurrency().getId());
        final Window window = openWindow("crm$Currency.select", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                CollectionDatasource<Currency, UUID> selectDs = window.getDsContext().get("mainDs");
                if (selectDs.getItems().size() > 0) {
                    for (Currency currency : selectDs.getItems()) {
                        replaceCurrancy(currency);
                    }
                }
            }
        });
    }

    private Boolean validateReplaceCurrancy()
    {
        Boolean result = true;
        OrdDoc item = getItem();
        if(item.getContract()!=null) {
            result = false;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", item.getIntegrationResolver().getId());
        IntegrationResolver integrationResolver = work1C.buildQuery(IntegrationResolver.class, params);
        if (!StatusDocSales.NEW.equals(integrationResolver.getStateDocSales())) {
            result = false;
        }
        params.clear();
        params.put("view", "_minimal");
        params.put("parentCard.id", item.getId());
        Card card = work1C.buildQuery(Card.class, params);
        if(card != null) {
            result = false;
        }
        return result;
    }

    private void replaceCurrancy(Currency currency)
    {
        OrdDoc ordDocItem = getItem();
        ordDocItem.setCurrency(currency);
        BigDecimal newFullSum = BigDecimal.valueOf(0);
        BigDecimal newTaxSum = BigDecimal.valueOf(0);
        if (detailsDs.size() > 0) {
            for (OrderDetail od : detailsDs.getItems()) {
                Cost cost = projectCost(od.getProduct().getId(), ordDocItem.getProject().getId());
                if (cost == null) {
                    cost = costBase(od.getProduct().getId());
                }
                if (cost != null) {
                    BigDecimal newCost = cost.getPrimaryCost(); //руб
                    if ("978".equals(currency.getCode())) {
                        newCost = cost.getSecondaryCost(); //евро
                    }
                    if ("840".equals(currency.getCode())) {
                        newCost = cost.getSecondaryCost(); //евро
                    }
                    if (newCost != null) {
                        od.setDiscountType(DiscountTypeEnum.percent);
                        od.setMarginSum(BigDecimal.valueOf(0));
                        od.setDiscountSum(BigDecimal.valueOf(0));
                        od.setCostPerPiece(newCost);
                        ProductCalculate productCalculate = new ProductCalculate(od);
                        Map<String, Object> validate = productCalculate.validateCalculate();
                        if (validate.size() > 0) {
                            showNotification(getMessage(validate.get("message").toString()), NotificationType.HUMANIZED);
                            break;
                        } else {
                            od = productCalculate.costCalculation();
                            newFullSum = newFullSum.add(od.getTotalSum());
                            newTaxSum = newTaxSum.add(od.getTaxSum());
                        }

                    } else {
                        detailsDs.removeItem(od); //если нет цены в выбраной валюте, удаляем услугу из заказа
                    }
                } else {
                    detailsDs.removeItem(od); //если нет цены, удаляем услугу из заказа
                }
            }
        }
        ordDocItem.setFullSum(newFullSum);
        ordDocItem.setTaxSum(newTaxSum);
        cur1.setDatasource(cardDs, "currency.name_ru");
        cur2.setDatasource(cardDs, "currency.name_ru");
        cur3.setDatasource(cardDs, "currency.name_ru");
    }

    //базовая цена
    private Cost costBase(UUID productId) {
        LoadContext loadContext = new LoadContext(Cost.class)
                .setView("edit");
        loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.startDate <= :date) and e.product.id = :product")
                .setParameter("product", productId)
                .setParameter("date", java.util.Calendar.getInstance().getTime());
        return dataManager.load(loadContext);
    }

    //цена для проекта
    private Cost projectCost(UUID productId, UUID projectId) {
        LoadContext loadContext = new LoadContext(Cost.class)
                .setView("edit");
        loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.project.id = :project and c.startDate <= :date) and e.product.id = :product and e.project.id = :project")
                .setParameter("product", productId)
                .setParameter("date", java.util.Calendar.getInstance().getTime())
                .setParameter("project", projectId);
        return dataManager.load(loadContext);
    }

    private void initEditWindow() {
        if (getItem().getIntegrationResolver() != null && !isTemplate) {
            if (getItem().getIntegrationResolver().getDel()) {
                orderDetailTable.removeAllActions();
                detailBtnPnl.setVisible(false);
                docInfo.setEnabled(false);
                actionsFrameScroll.setVisible(false);
            }
        }
        boolean isDetailsDs = !(detailsDs.size() > 0);
        company.setEditable(getItem().getCompany() != null && getItem().getContract() != null ? false : true);
        project.setEditable(isDetailsDs);
        contract.setEditable(getItem().getContract() != null ? false : true);
        docCategory.setEditable(isDetailsDs);
        currency.setEditable(isDetailsDs);
    }

    public void searchProduct() {
        searchProductColor();
    }

    protected void searchProductColor() {
        orderDetailTable.addStyleProvider(new Table.StyleProvider<OrderDetail>() {
            @Nullable
            @Override
            public String getStyleName(OrderDetail entity, @Nullable String property) {
                String searchText = searchSimpleText.getValue() != null ? searchSimpleText.getValue().toString().toLowerCase() : null;
                if (entity != null && searchText != null) {
                    String alternativeName = entity.getAlternativeName_ru().toLowerCase();
                    if(alternativeName.contains(searchText)) {
                        return "orange-grade";
                    }
                }
                return null;
            }
        });
    }

    public void createProduct() {
        openEditOrderDetail("create");
    }

    public void editProduct() {
        openEditOrderDetail("edit");
    }

    private void openEditOrderDetail(String mode) {
        if(!work1C.validate(getItem(), getItem().getProject(), getItem().getDocCategory())){
            showNotification(getMessage("Необходимо выбрать проект и категорию!"), NotificationType.HUMANIZED);
            return;
        }
        else {
            docCategory.setEditable(false);//блокируем на изменения
            project.setEditable(false);//блокируем на изменения
        }
        OrderDetail newEntity = null;
        if("create".equals(mode)) {
            if(PersistenceHelper.isNew(getItem())) {
                showNotification(getMessage("Сохраните Заказ!"), NotificationType.HUMANIZED);
                return;
            }
            newEntity = metadata.create(OrderDetail.class);
            newEntity.setOrdDoc(getItem());
        }
        if("edit".equals(mode)) {
            if (orderDetailTable.getSelected().size() < 1) {
                String txtMesage = "Не выбрана услуга!";
                showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                return;
            }
            newEntity = orderDetailTable.getSingleSelected();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("mode", mode);
        Window window = openEditor("crm$OrderDetail.edit", newEntity, WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                detailsDs.refresh();
                SaveOrdDocAndCalcFullSumTaxSum();
            }
        });
    }

    //edit product
    protected Action EditAction()  {
        return new EditAction(orderDetailTable) {
            @Override
            public void actionPerform(Component component) {
                openEditOrderDetail("edit");
            }
        };
    }

    //delete product
    protected Action RemoveAction()  {
        return new RemoveAction(orderDetailTable) {
            @Override
            protected void afterRemove(Set selected) {
                SaveOrdDocAndCalcFullSumTaxSum();
            }
        };
    }

    private void SaveOrdDocAndCalcFullSumTaxSum() {
        if(detailsDs.size() < 1){
            getItem().setFullSum(BigDecimal.valueOf(0));
            getItem().setTaxSum(BigDecimal.valueOf(0));
            return;
        }
        BigDecimal sum = BigDecimal.valueOf(0);
        BigDecimal taxSum = BigDecimal.valueOf(0);
        for (Object i : detailsDs.getItems()) {
            OrderDetail item = (OrderDetail) i;
            if(item.getTotalSum()!=null){
                sum = sum.add(item.getTotalSum());
            }
            if(item.getTaxSum()!=null){
                taxSum = taxSum.add(item.getTaxSum());
            }
        }
        getItem().setFullSum(sum);
        getItem().setTaxSum(taxSum);
        Label fullSumLb = getComponentNN("fullSum");
        Label taxSumLb = getComponentNN("taxSum");
        cardDs.getItem().setFullSum(getItem().getFullSum());
        cardDs.getItem().setTaxSum(getItem().getTaxSum());
        fullSumLb.setDatasource(cardDs, "fullSum");
        taxSumLb.setDatasource(cardDs, "taxSum");
        super.commit();
    }

    public void groupBtnClick() {
        boolean isValidate = orderDetailTable.getSelected().size() > 0 ? true : false;
        if (isValidate) {
            openWindowSelect("crm$OrderDetailGroup.edit");
        } else {
            String txtMesage = "Не выбрана(ы) услуга(и) для группировки!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
    }

    protected void openWindowSelect(final String windowAlias) {
        Map<String, Object> params = new HashMap<>();
        params.put("ordDocId", getItem().getId());
        params.put("selectOrderDetail", orderDetailTable.getSelected());
        final Window window = openWindow(windowAlias, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                detailsDs.refresh();
                if ("crm$SelectItemPercent.edit".equals(windowAlias)) SaveOrdDocAndCalcFullSumTaxSum();
            }
        });
    }

    public void selectItemPrecentBtnClick() {
        boolean isValidate = orderDetailTable.getSelected().size() > 0 ? true : false;
        if (isValidate) {
            openWindowSelect("crm$SelectItemPercent.edit");
        } else {
            String txtMesage = "Не выбрана(ы) услуга(и) для установки скидки/наценки!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
    }

    public void offerContractButton() {
        if (validCreateContract("offerContractButton")) {
            OrdDoc item = getItem();
            Organization organizationDefault = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();
            if (item.getOrganization().equals(organizationDefault)) {
                item.setContract(crmConfig.getContractOfferEfi());
            } else {
                item.setContract(crmConfig.getContractOfferNeva());
            }
            super.commit();
        }
    }

    protected boolean validCreateContract(String nameMethod) {
        OrdDoc item = getItem();
        if(PersistenceHelper.isNew(item)) {
            String txtMesage = "Заказ необходимо сохранить перед созданием договора";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return false;
        }

        if (!work1C.validate(item.getCompany())) {
            String txtMesage = "Поле \"Заказчик\" должно быть заполнено!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return false;
        }

        if (!work1C.validate(item.getProject())) {
            String txtMesage = "Поле \"Проект\" должно быть заполнено!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return false;
        }

        if (!work1C.validate(item.getOwner())) {
            String txtMesage = "Поле \"Ответственный\" должно быть заполнено!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return false;
        }

        if (work1C.validate(item.getContract())) {
            if ("offerContractButton".equals(nameMethod)) {
                String txtMesage = String.format("Поле \"Договор\" уже содержит договор: \n %s!", item.getContract().getDescription());
                showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                return false;
            } else {
                if (!"offer".equals(item.getContract().getDocCategory().getCode())) {
                    String txtMesage = String.format("Поле \"Договор\" уже содержит договор: \n %s!", item.getContract().getDescription());
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    return false;
                }
            }
        }
        return true;
    }

    private void updateContractInvDoc() {
        OrdDoc item = getItem();
        Map<String, Object> params = new HashMap<>();
        params.put("view", "edit");
        params.put("parentCard.id", item.getId());
        List<InvDoc> list = work1C.buildQueryList(InvDoc.class, params);
        if (list.size() > 0) {
            for (InvDoc doc : list) {
                doc.setContract(item.getContract());
                toCommit.add(doc);
            }
        }
    }

    private void updateContractActDoc() {
        OrdDoc item = getItem();
        Map<String, Object> params = new HashMap<>();
        params.put("view", "edit");
        params.put("parentCard.id", item.getId());
        List<AcDoc> list = work1C.buildQueryList(AcDoc.class, params);
        if (list.size() > 0) {
            for (AcDoc doc : list) {
                if (doc.getIntegrationResolver().getPosted() != null && !doc.getIntegrationResolver().getPosted()) {
                    doc.setContract(item.getContract());
                    toCommit.add(doc);
                }
            }
        }
    }

    private void exportParentCompany() {
        if (getItem().getCompany().getCompanyType().equals(CompanyTypeEnum.branch)) {
            Map<String, Object> params = new HashMap<>();
            params.put(getItem().getCompany().getMetaClass().getName(), getItem().getCompany().getParentCompany().getId());
            work1C.regLog(params, priority);
        }
    }

    protected Action SurchargeAction()  {
        return new AbstractAction("surcharge") {
            @Override
            public void actionPerform(Component component) {
                final Window window = openWindow("crm$InvSurcharge", WindowManager.OpenType.DIALOG,  Collections.<String, Object>singletonMap("ordDoc", getItem()));
                window.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                            BigDecimal sum = window.getContext().getParamValue("sum");
                            Map<String, Object> params = new HashMap<>();
                            params.put("ordDoc", getItem());
                            params.put("sum", sum);
                            SurchargeInvoice surchargeInvoice = new SurchargeInvoice(params);
                            InvDoc invoice = surchargeInvoice.getInvSurcharge();
                            Window window = openEditor("crm$InvDoc.edit", invoice, WindowManager.OpenType.THIS_TAB);
                        }
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("invSurcharge");
            }



        };
    }


}