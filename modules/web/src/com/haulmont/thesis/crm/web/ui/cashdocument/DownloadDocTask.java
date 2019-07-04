package com.haulmont.thesis.crm.web.ui.cashdocument;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.core.global.ProgressInfo;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.EvotorService;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashDocument;
import com.haulmont.thesis.crm.entity.CashDocumentPosition;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by k.khoroshilov on 27.05.2017.
 */
public class DownloadDocTask extends BackgroundTask<Integer, List<Entity>> {

    private Log log = LogFactory.getLog(DownloadDocTask.class);

    protected ProgressInfo progressInfo;

    protected UUID storeId;
    protected Date beginDate, endDate;
    protected DataManager dataManager;

    protected DownloadDocTask(UUID storeId, Date beginDate, Date endDate, long timeout, TimeUnit timeUnit, Window ownerWindow) {
        super(timeout, timeUnit, ownerWindow);
        this.storeId = storeId;
        this.beginDate = beginDate;
        this.endDate = endDate;

        this.progressInfo = new ProgressInfo();

        dataManager = AppBeans.get(DataManager.NAME);
    }

    @Override
    public List<Entity> run(TaskLifeCycle<Integer> taskLifeCycle) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);

        List<Entity> result = new ArrayList<>();
        int i = 0;
        try {
            List<CashDocument> docs;
            EvotorService evotorService = AppBeans.get(EvotorService.NAME);

            if (storeId != null){
                docs = evotorService.getSellDocs(storeId, beginDate, endDate);
            }else {
                docs = evotorService.getSellDocs(beginDate, endDate);
            }

            Set<UUID> cacheDoc = getDocs(beginDate, endDate);

            for(CashDocument doc : docs){
                progressInfo.setLinePosition(i + 1);

                if (!cacheDoc.contains(doc.getId())){
                    result.add(doc);
                    result.addAll(doc.getPositions());
                    cacheDoc.add(doc.getId());
                }else{
                    Set<String> positions = getDocPosition(doc);
                    for(CashDocumentPosition pos : doc.getPositions()){
                        if (!positions.contains(pos.getPosId())){
                            result.add(pos);
                            positions.add(pos.getPosId());
                        }
                    }
                }
            }
        }catch (ServiceNotAvailableException e){
            log.error(e.getMessage());
            progressInfo.setError(e.getMessage());
            result = null;
        }catch (Exception e){
           e.printStackTrace();
            progressInfo.setError(e.getMessage());
           result = null;
        }
        return taskLifeCycle.isInterrupted() ? null : result;
    }

    @Override
    public void done(List<Entity> result) {
        super.done(result);
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        WindowManager manager = App.getInstance().getWindowManager();

        if (StringUtils.isNotBlank(progressInfo.getError())) {
            WindowInfo windowInfo = windowConfig.getWindowInfo("errorDialog");
            manager.getDialogParams().setWidth(420);
            manager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, Collections.<String, Object>singletonMap("progressInfo", progressInfo));
        }else if (result.isEmpty()) {
            getOwnerFrame().showNotification(getMessage("importNothing"), IFrame.NotificationType.HUMANIZED);
        } else {
            CommitContext context = new CommitContext(result);
            dataManager.commit(context);
            getOwnerFrame().showNotification(getMessage("importComplete"), IFrame.NotificationType.HUMANIZED);
            for (Window w : manager.getOpenWindows()){
                if (w.getId().equals("crm$CashDocument.browse")) w.getDsContext().refresh();
            }
        }
    }

    protected Set<UUID> getDocs(Date sDate, Date eDate){
        Set<UUID> result = new HashSet<>();

        LoadContext loadContext = new LoadContext(CashDocument.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$CashDocument e where e.receiptCreationDateTime >= :sDate and e.receiptCreationDateTime <= :eDate")
                .setParameter("sDate", sDate)
                .setParameter("eDate", eDate);

        List<CashDocument> list = dataManager.loadList(loadContext);
        for (CashDocument doc : list){
            result.add(doc.getId());
        }
        return result;
    }

    protected Set<String> getDocPosition(CashDocument doc){
        Set<String> result = new HashSet<>();

        LoadContext loadContext = new LoadContext(CashDocumentPosition.class).setView("_local");
        loadContext.setQueryString("select e from crm$CashDocumentPosition e where e.cashDocument.id = :doc")
                .setParameter("doc", doc);
        List<CashDocumentPosition> list = dataManager.loadList(loadContext);
        for (CashDocumentPosition position : list){
            result.add(position.getPosId());
        }
        return result;
    }

    @Override
    public void progress(List<Integer> changes) {
        super.progress(changes);
    }

    @Override
    public boolean handleException(final Exception ex) {
        if (ex !=  null) {
            showExecutionError(ex);
        }
        return super.handleException(ex);
    }

    protected void showExecutionError(Exception ex) {
        final IFrame ownerFrame = getOwnerFrame();
        if (ownerFrame != null) {
            String localizedMessage = ex.getLocalizedMessage();
            if (StringUtils.isNotBlank(localizedMessage)) {
                ownerFrame.showNotification(getMessage( "backgroundWorkProgress.executionError"),
                        localizedMessage, IFrame.NotificationType.WARNING);
            } else {
                ownerFrame.showNotification(getMessage( "backgroundWorkProgress.executionError"),
                        IFrame.NotificationType.WARNING);
            }
        }
    }

    @Override
    public boolean handleTimeoutException() {
        final IFrame ownerFrame = getOwnerFrame();
            if (ownerFrame != null) {
                ownerFrame.showNotification(
                        getMessage("backgroundWorkProgress.timeout"),
                        getMessage( "backgroundWorkProgress.timeoutMessage"),
                        IFrame.NotificationType.WARNING);
            }
        return super.handleTimeoutException();
    }

    protected String getMessage(String key) {
        return AppBeans.get(Messages.class).getMessage(getClass(), key);
    }
}
