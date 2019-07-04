package com.haulmont.thesis.crm.web.ui.activitykind;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.entity.ActivityKind;
import com.haulmont.thesis.crm.entity.BaseActivity;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ActivityKindBrowser extends AbstractLookup {

    @Named("activityKindsDs")
    protected CollectionDatasource<ActivityKind, UUID> mainDs;
    @Named("activityKindsTable")
    protected Table mainTable;

    @Inject
    protected ActivityService activityService;

    @Inject
    protected Messages messages;

    @Inject
    protected Security security;

    protected Map<String, Object> paramsMap = new HashMap<>();
    protected Log log = LogFactory.getLog(this.getClass());

    protected static final String CHANGE_ACTION_ID = "change";
    protected static final String CAPTION_WARNING = "change.warning";
    protected static final String FIND_WARNING_MSG = "change.find";
    protected static final String BODY_WARNING_MSG = "change.body";
    protected static final String BODY_NOT_FOUND_MSG = "change.bodyNotFound";
    protected static final String BODY_FOUND_MSG = "find.body";

    @Override
    public void init(Map<String, Object> params) {
        createRemoveAction();
        changeKind();

        if (mainDs != null) {
            mainDs.addListener(new CollectionDsListenerAdapter<ActivityKind>() {
                @SuppressWarnings("unchecked")
                @Override
                public void itemChanged(Datasource ds, ActivityKind prevItem, ActivityKind item) {
                    if (item != null) {
                        paramsMap.put("akClass", "%" + item.getEntityType() + "%");
                    }
                    super.itemChanged(ds, prevItem, item);
                }

            });
        }

        Boolean isLookup = (Boolean) params.get("isLookup");
        if (BooleanUtils.isTrue(isLookup)) {
            setCaption(getMessage("lookup.caption"));
        }
    }

    public void OpenProcsEditor() {
        openDialogEditorWithAlias("activityKindProcs");
    }

    public void OpenReportsEditor() {
        openDialogEditorWithAlias("activityKindReports");
    }

    protected void openDialogEditorWithAlias(String windowAlias) {
        ActivityKind kind = mainTable.getSingleSelected();
        if (kind != null) {
            App.getInstance().getWindowManager().getDialogParams().setWidth(400);
            final Window window = openEditor(windowAlias, kind, WindowManager.OpenType.DIALOG, paramsMap);
            window.addListener(new Window.CloseListener() {
                @Override
                public void windowClosed(String actionId) {
                    if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                        ActivityKind item = (ActivityKind) ((Editor) window).getItem();
                        if (mainDs != null && item != null)
                            mainDs.updateItem(item);
                    }
                }
            });
        } else {
            showNotification(getMessage("notSelectedActivityKind"), NotificationType.HUMANIZED);
        }
    }

    protected void createRemoveAction() {
        mainTable.addAction(new RemoveAction(mainTable, true) {
            @Override
            public void actionPerform(Component component) {
                Long countEntities = getCountEntities();
                if (countEntities > 0) {
                    String bodyMsg = formatMessage(BODY_FOUND_MSG, countEntities);
                    showMsgHumanized(bodyMsg, "");
                } else {
                    super.actionPerform(component);
                }
            }
        });
    }

    protected void changeKind() {
        mainTable.addAction(new AbstractAction(CHANGE_ACTION_ID) {
            @Override
            public void actionPerform(Component component) {
                ActivityKind selectedKind = mainTable.getSingleSelected();
                Long countEntities = getCountEntities();
                if (countEntities > 0) {
                    String captionMsg = getMessageInClass(CAPTION_WARNING);
                    String findMsg = formatMessage(FIND_WARNING_MSG, countEntities);
                    String bodyMsg = getMessageInClass(BODY_WARNING_MSG);
                    getFrame().showOptionDialog(captionMsg, findMsg + bodyMsg, MessageType.WARNING_HTML,
                            new Action[]{new ChangeKindAction(selectedKind), new CancelChangeKindAction()});
                } else {
                    showMsgHumanized(CAPTION_WARNING, BODY_NOT_FOUND_MSG);
                }
            }
        });
    }

    protected Long getCountEntities() {
        return mainTable.getSingleSelected() != null
                ? activityService.getCountWithSpecifiedKind((UUID) mainTable.getSingleSelected().getId())
                : 0L;
    }

    protected void showMsgHumanized(String caption, String body) {
        String captionMsg = getMessageInClass(caption);
        String bodyMsg = getMessageInClass(body);
        String msg = captionMsg + " " + bodyMsg;
        getFrame().showNotification(msg, "", NotificationType.HUMANIZED);
    }

    protected String getMessageInClass(String key) {
        return messages.getMessage(ActivityKindBrowser.class, key);
    }

    protected String formatMessage(String key, Object... params) {
        return messages.formatMessage(ActivityKindBrowser.class, key, params);
    }

    protected class ChangeKindAction extends AbstractAction {

        private static final String KIND_LOOKUP = "crm$ActivityKind.browse";
        private static final String ACTION_CHANGE_ID = "action.Change";
        private static final String ICONS_OK = "icons/ok.png";
        private static final String TYPE_PARAM = "type";
        private static final String KIND_ID_PARAM = "kindId";

        private static final String CAPTION_SUCCESS = "change.success";
        private static final String BODY_SUCCESS_MSG = "change.bodySuccess";

        private static final String CAPTION_FALL = "change.fall";
        private static final String BODY_FALL_MSG = "change.bodyFall";

        private ActivityKind selectedKind;

        protected ChangeKindAction(ActivityKind selectedKind) {
            super(ACTION_CHANGE_ID);
            this.selectedKind = selectedKind;
        }

        @Override
        public void actionPerform(Component component) {
            Map<String, Object> params = new HashMap<>();
            params.put(TYPE_PARAM, selectedKind.getEntityType());
            params.put(KIND_ID_PARAM, selectedKind.getId());
            params.put("isLookup", Boolean.TRUE);
            getFrame().openLookup(KIND_LOOKUP, new Window.Lookup.Handler() {
                public void handleLookup(Collection items) {
                    if (items != null && items.size() > 0) {
                        ActivityKind kind = (ActivityKind) items.iterator().next();
                        changeKind(kind);
                    }
                }
            }, WindowManager.OpenType.THIS_TAB, params);
        }

        private void changeKind(ActivityKind kind) {
            List<BaseActivity> entities = activityService.getWithKind(selectedKind.getId());
            entityIterator(kind, entities);
            showMsgAfterChange(kind);
        }

        private void entityIterator(ActivityKind kind, List<BaseActivity> entities) {
            int i = 0;
            int j = 0;
            List<BaseActivity> entitiesToCommit = new ArrayList<>();
            for (BaseActivity entity : entities) {
                entity.setKind(kind);
                entitiesToCommit.add(entity);
                i++;
                j++;
                if (i >= 100) {
                    commitKind(i, j, entitiesToCommit);
                    entitiesToCommit.clear();
                    i = 0;
                }
            }
            if (i > 0) {
                commitKind(i, j, entitiesToCommit);
            }
            entitiesToCommit.clear();
        }

        private void commitKind(int i, int j, List<BaseActivity> entitiesToCommit) {
            CommitContext commitContext = new CommitContext();
            commitContext.getCommitInstances().addAll(entitiesToCommit);
            getDsContext().getDataSupplier().commit(commitContext);
            getDsContext().commit();
            log.info("Commit " + i + " of " + j + " cards");
        }

        private void showMsgAfterChange(ActivityKind kind) {
            if (getCountEntities() == 0) {
                String captionMsg = getMessageInClass(CAPTION_SUCCESS);
                String bodyMsg = formatMessage(BODY_SUCCESS_MSG, selectedKind.getName(), kind.getName());
                String msg = captionMsg + " " + bodyMsg;
                getFrame().showNotification(msg, "", NotificationType.HUMANIZED);
            } else {
                showMsgHumanized(CAPTION_FALL, BODY_FALL_MSG);
            }
        }

        @Override
        public String getCaption() {
            return  getMessageInClass(ACTION_CHANGE_ID);
        }

        @Override
        public String getIcon() {
            return ICONS_OK;
        }
    }

    protected class CancelChangeKindAction extends AbstractAction {

        private static final String ACTION_CANCEL_ID = "actions.Cancel";
        private static final String ICONS_CANCEL = "icons/cancel.png";

        protected CancelChangeKindAction() {
            super(ACTION_CANCEL_ID);
        }

        @Override
        public void actionPerform(Component component) {
        }

        @Override
        public String getCaption() {
            return getMessageInClass(ACTION_CANCEL_ID);
        }

        @Override
        public String getIcon() {
            return ICONS_CANCEL;
        }
    }
}