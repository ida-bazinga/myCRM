package com.haulmont.thesis.crm.core.app;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.ServerConfig;
import com.haulmont.cuba.core.entity.Config;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.cuba.security.app.EntityLogAPI;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportScreen;
import com.haulmont.thesis.core.app.AbstractDeployer;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ManagedBean("tExpoCRM_ExtDeployer")
public class ExtDeployer extends AbstractDeployer implements ExtDeployerMBean {

    protected Log log = LogFactory.getLog(ExtDeployer.class);
    @Inject
    protected EntityLogAPI entityLogAPI;

    public ExtDeployer() {
        createAppContextListener();
    }

    protected void createAppContextListener() {
        AppContext.addListener(new AppContext.Listener() {
            public void applicationStarted() {
                checkForFirstInit();
            }

            public void applicationStopped() {
            }
        });
    }

    protected void checkForFirstInit() {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            List<Config> configs = em.createQuery("select c from sys$Config c where c.name='tExpoCRM.initDefaultInFirstStart'",
                    Config.class).getResultList();
            String initDefaultInFirstStart = null;
            if (!configs.isEmpty()) {
                initDefaultInFirstStart = configs.get(0).getValue();
            }

            if (Boolean.TRUE.toString().equals(initDefaultInFirstStart) || initDefaultInFirstStart == null) {
                String edition = AppContext.getProperty("thesis.initDefaultEdition");
                if (StringUtils.isNotBlank(edition) && Integer.parseInt(edition) > 1) {
                    initDefault("init");
                    if (initDefaultInFirstStart == null) {
                        try {
                            login();
                            Config config = metadata.create(Config.class); //new Config();
                            config.setName("tExpoCRM.initDefaultInFirstStart");
                            config.setValue("false");
                            em.persist(config);
                            tx.commit();
                        } catch (LoginException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } finally {
                            logout();
                        }
                    } else {
                        try {
                            login();
                            em.createQuery("update sys$Config c set c.value='false' where c.name='tExpoCRM.initDefaultInFirstStart'",
                                    Config.class).executeUpdate();
                            tx.commit();
                        } catch (LoginException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } finally {
                            logout();
                        }
                    }
                }
            }
        } finally {
            tx.end();
        }
    }

    @Authenticated
    public String initDefault(String password) {
        if (password != null && PASSWORD.equals(DigestUtils.md5Hex(password))) {
            try {
                executeInitScripts();
                
                initExtensionDocumentsFunctionality();
                initStandardFilters();
                entityLogAPI.invalidateCache();
                return INIT_SUCESS;
            } catch (Exception e) {
                return ExceptionUtils.getStackTrace(e);
            }
        }
        return "Error password";
    }

    protected void executeInitScripts() {
        String dbDirPath = AppBeans.get(Configuration.class).getConfig(ServerConfig.class).getDbDir();  //ConfigProvider.getConfig(ServerConfig.class).getDbDir();
        String dbmsType = AppContext.getProperty("cuba.dbmsType");
        File folderDB = new File(dbDirPath + "/50-tExpoCRM/init/" + dbmsType);
        File[] scripts = folderDB.listFiles();
        for (File script : scripts) {
            if (script.getName().contains("init")) {
                executeSqlScript(script);
            }
        }
    }
    

    public String initExtensionDocumentsFunctionality() {
        ArrayList<String> procCodes = Lists.newArrayList("Endorsement", "Resolution", "Acquaintance", "Registration");
        ArrayList<String> reportCodes = Lists.newArrayList("EndorsementList");

        String extensionDocuments = AppContext.getProperty("ext.extensionDocuments");
        if(extensionDocuments == null || extensionDocuments.isEmpty())
            return "No extension documents found";

        String[] extDocMetaClasses = extensionDocuments.split(",");
        Transaction tx = persistence.getTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<Proc> query = em.createQuery("select p from wf$Proc p where p.code in :procCodes", Proc.class);
            query.setParameter("procCodes", procCodes);
            List<Proc> procs = query.getResultList();

            for (Proc proc : procs) {
                String cardTypes = proc.getCardTypes();
                for (String metaClass : extDocMetaClasses)
                    if(!cardTypes.contains(metaClass))
                        cardTypes += metaClass + ",";

                proc.setCardTypes(cardTypes);
            }

            TypedQuery<Report> reportQuery = em.createQuery("select r from report$Report r where r.code in :reportCodes", Report.class);
            reportQuery.setParameter("reportCodes", reportCodes);
            List<Report> reports = reportQuery.getResultList();

            for (Report report : reports) {
                Report reportFromXml = reportingApi.convertToReport(report.getXml());
                List<ReportScreen> reportScreens = reportFromXml.getReportScreens();

                for (String metaClass : extDocMetaClasses) {
                    final String screenId = metaClass + ".edit";
                    Optional<ReportScreen> oReportScreen = Iterables.tryFind(reportScreens, new Predicate<ReportScreen>() {
                        @Override
                        public boolean apply(ReportScreen input) {
                            return screenId.equals(input.getScreenId());
                        }
                    });

                    if(!oReportScreen.isPresent()) {
                        ReportScreen reportScreen = new ReportScreen();
                        reportScreen.setReport(reportFromXml);
                        reportScreen.setScreenId(screenId);
                        reportScreens.add(reportScreen);
                    }
                }

                reportFromXml.setReportScreens(reportScreens);
                report.setXml(reportingApi.convertToXml(reportFromXml));
            }
            tx.commit();
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
            return stackTrace;
        } finally {
            tx.end();
        }

        return "Standard functionality added to extension documents";
    }

    public String initStandardFilters() {
        StringBuilder resultMessage = new StringBuilder();
        String filtersDirPath = AppContext.getProperty("ext.filtersDir");
        File filtersDir = new File(filtersDirPath);
        if (filtersDir.exists()) {
            File[] filters = filtersDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });
            Arrays.sort(filters);
            createFilterEntities(filters, resultMessage);
        } else {
            resultMessage.append("Folders dir ").append(filtersDirPath).append(" not found");
        }
        return resultMessage.toString();
    }

    protected void createFilterEntities(File[] filters, StringBuilder resultMessage) {
        Transaction tx = persistence.createTransaction();
        try {
            login();
            for (File filterXml : filters) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(filterXml);
                    String xmlContent = IOUtils.toString(fis);
                    Document document = Dom4j.readDocument(xmlContent);
                    Element filterEl = document.getRootElement();
                    createOrUpdateFilter(filterEl.attributeValue("code"), filterEl.attributeValue("name"),
                            filterEl.attributeValue("filterComponent"), Dom4j.writeDocument(document, true));
                    resultMessage.append("File ")
                            .append(filterXml.getName())
                            .append(" processed\n");
                } catch (FileNotFoundException e) {
                    resultMessage.append(ExceptionUtils.getStackTrace(e));
                } finally {
                    IOUtils.closeQuietly(fis);
                }
            }
            tx.commit();
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            tx.end();
            logout();
        }
    }
    
    
}