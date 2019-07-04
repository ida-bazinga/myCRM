/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.thesis.core.app.NumerationWorker;
import com.haulmont.thesis.core.app.NumerationWorkerBean;
import com.haulmont.thesis.core.entity.Numerator;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ExtNumerationWorkerBean extends NumerationWorkerBean implements NumerationWorker {

    @Override
    public String getNextNumber(@Nonnull final String numeratorName) {
        Preconditions.checkNotNullArgument(numeratorName);
        return persistence.createTransaction().execute(new Transaction.Callable<String>() {
            @Override
            public String call(EntityManager em) {
                Numerator numerator = (Numerator) em.createQuery("select e from df$Numerator e where e.code = ?1")
                        .setParameter(1, numeratorName)
                        .getFirstResult();

                if (numerator == null)
                    throw new IllegalArgumentException(String.format("Numerator with code %s not found", numeratorName));

                return getNextNumberValue(numerator, new HashMap<String, Object>());
            }
        });
    }
}
