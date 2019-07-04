package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.thesis.crm.core.app.contactcenter.CallerIdService;
import com.haulmont.thesis.crm.entity.ExtCompany;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by k.khoroshilov on 11.02.2017.
 */
@Service(CallerIdService.NAME)
public class CallerIdServiceBean implements CallerIdService {

    @Inject
    protected Persistence persistence;


    @Override
    public List<ExtCompany> getCompaniesByPhoneNumber(String phoneNumber) {
        return StringUtils.isNotBlank(phoneNumber) ? getCompanies(phoneNumber) : Collections.<ExtCompany>emptyList();
    }

    @SuppressWarnings("unchecked")
    protected List<ExtCompany> getCompanies(@Nonnull String phoneNumber) {
        Preconditions.checkNotNullArgument(phoneNumber);
        List<ExtCompany> result = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery(
                    "select distinct cm from crm$Company cm" +
                            " join cm.contactPersons cp join cp.communications co" +
                            " where co.mainPart = :num and co.commKind.communicationType = @enum(com.haulmont.thesis.crm.entity.CommunicationTypeEnum.phone)",
                    ExtCompany.class);
            query.setParameter("num", phoneNumber);

            List<ExtCompany> companies = query.getResultList();
            if (!companies.isEmpty()) result = companies;
        } finally {
            tx.end();
        }
        return result;
    }
}
