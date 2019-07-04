/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.entity.Log1C;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

@ManagedBean(ProcessingServices1cMBean.NAME)
public class ProcessingServices1c implements ProcessingServices1cMBean {

    @Inject
    protected ProjectService projectService;
    @Inject
    protected AccountService accountService;
    @Inject
    protected NomenclatureService nomenclatureService;
    @Inject
    protected OrdService ordService;
    @Inject
    protected InvService invService;
    @Inject
    protected AcService acService;
    @Inject
    protected VatService vatService;
    @Inject
    protected PaymentService paymentService;
    @Inject
    protected StatusService statusService;
    @Inject
    protected CashService cashService;
    @Inject
    protected DataDisableService dataDisableService;
    @Inject
    protected PayOrdService payOrdService;
    @Inject
    protected PayOrdStatusService payOrdStatusService;

    protected Log logFactory = LogFactory.getLog(this.getClass());




    @Authenticated
    public String CatalogAndDocProcessing()
    {
        String info = "Start class ProcessingServices1c:CatalogAndDocProcessing";
        logFactory.info(info);
        try {
            Work1C work1C = AppBeans.get(Work1C.class);
            boolean result;
            Log1C log = work1C.getMyLog1C();
            if(log == null)
            {
                info = "class ProcessingServices1c:CatalogAndDocProcessing. To send 1C no record.";
                logFactory.info(info);
                return info;
            }
            info = String.format("class ProcessingServices1c:CatalogAndDocProcessing. Object to the exchange %s", log.getEntityName());
            logFactory.info(info);

            switch (log.getEntityName())
            {
                case "crm$Nomenclature": result = nomenclatureService.setNomenclatureOne(log);
                    break;
                case "crm$Company": result = accountService.setAccountOne(log);
                    break;
                case "crm$Project": result = projectService.setProjectOne(log);
                    break;
                //(не используется в новой верии)
                //case "df$Contract": result = contractService.setContractOne(log);
                //    break;
                case "crm$OrdDoc": result = ordService.setOrdOne(log);
                    break;
                case "crm$InvDoc": result = invService.setInvOne(log);
                    break;
                case "crm$AcDoc": result = acService.setAcOne(log);
                    break;
                case "crm$VatDoc": result = vatService.setVatOne(log);
                    break;
                case "crm$PayOrd": result = payOrdService.setPayOrdOne(log);
                    break;

                default: result = true;
                    break;
            }

            info = String.format("End class ProcessingServices1c:CatalogAndDocProcessing result=%s", result);
            logFactory.info(info);
            return info;

        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:CatalogAndDocProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String PaymentProcessing()
    {
        String info = "Start class ProcessingServices1c:PaymentProcessing";
        logFactory.info(info);
        boolean result;
        try {
            result = paymentService.getPayment();
            info = String.format("End class ProcessingServices1c:PaymentProcessing result=%s",result);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:PaymentProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String StatusProcessing()
    {
        String info = "Start class ProcessingServices1c:StatusProcessing";
        logFactory.info(info);
        boolean result;
        try {
            result = statusService.getStatusAll();
            info = String.format("End class ProcessingServices1c:StatusProcessing result=%s",result);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:StatusProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String CalculateSumPaymentProcessing()
    {
        String info = "Start class ProcessingServices1c:CalculateSumPaymentProcessing";
        logFactory.info(info);
        try {
            SqlSumPayment sqlSumPayment = new SqlSumPayment();
            info = String.format("End class ProcessingServices1c:CalculateSumPaymentProcessing result=%s",sqlSumPayment.result);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:CalculateSumPaymentProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String PKOProcessing()
    {
        String info = "Start class ProcessingServices1c:PKOProcessing";
        logFactory.info(info);
        try {
            boolean isResult = false;
            SqlSumPKO sqlSumPKO = new SqlSumPKO();
            if(sqlSumPKO.result.size() > 0) {
                isResult = cashService.setCashService(sqlSumPKO.result);
            }
            info = String.format("End class ProcessingServices1c:PKOProcessing result=%s",isResult);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:PKOProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String DataDisable()
    {
        String info = "Start class ProcessingServices1c:DataDisable";
        logFactory.info(info);
        try {
            boolean isResult = dataDisableService.isUpdateDataDisable();
            info = String.format("End class ProcessingServices1c:DataDisable result=%s",isResult);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:DataDisable. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public String PayOrdStatusProcessing()
    {
        String info = "Start class ProcessingServices1c:PayOrdStatusProcessing";
        logFactory.info(info);
        try {
            boolean isResult = payOrdStatusService.updatePayOrdStatus();
            info = String.format("End class ProcessingServices1c:PayOrdStatusProcessing result=%s",isResult);
            logFactory.info(info);
            return info;
        } catch (Exception e) {
            info = String.format("Error class ProcessingServices1c:PayOrdStatusProcessing. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

}
