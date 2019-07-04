package com.haulmont.thesis.crm.web.invDocBugetDetail;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.InvDocBugetDetail;

import java.util.Map;

@Deprecated
public class InvDocBugetDetailEditor<T extends InvDocBugetDetail> extends AbstractEditor<T> {


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        if (getItem() != null) {
            if (getItem().getInvDoc() != null) {    //Calculating the remaining Budget funds
                /*
                if (getItem().getInvDoc().getFullSum() != null) {
                    if (getItem().getInvDoc().getInvDocBugetDetail() != null && getItem().getInvDoc().getInvDocBugetDetail().size() > 0) {
                        ArrayList<InvDocBugetDetail> inv = new ArrayList<>();
                        BigDecimal totalDetailsSum = BigDecimal.valueOf(0);
                        for (InvDocBugetDetail item : getItem().getInvDoc().getInvDocBugetDetail()) {
                            if (item.getFullSum()!= null) {
                                totalDetailsSum = totalDetailsSum.add(item.getFullSum());
                            }
                        }
                        if (!totalDetailsSum.stripTrailingZeros().equals(getItem().getInvDoc().getFullSum().stripTrailingZeros()) && getItem().getInvDoc().getFullSum().subtract(totalDetailsSum).compareTo(BigDecimal.ZERO) >= 0) {
                            getItem().setFullSum(getItem().getInvDoc().getFullSum().subtract(totalDetailsSum));
                        }
                    } else {getItem().setFullSum(getItem().getInvDoc().getFullSum());}
                }
                */
                if (getItem().getInvDoc().getCurrency() != null) {
                    getItem().setCurrency(getItem().getInvDoc().getCurrency());
                }
            }
        }
    }

}