package com.haulmont.thesis.crm.web.companyVerifiedState;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.entity.Correspondent;
import com.haulmont.thesis.crm.entity.CompanyVerifiedState;
import com.haulmont.thesis.crm.entity.VerifiedStateEnum;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompanyVerifiedStateMessage extends AbstractWindow {

    @Inject
    protected TextArea comment;
    @Inject
    private DataManager dataManager;
    @Inject
    protected UserSession userSession;

    protected Set<Entity> toCommit = new HashSet<>();
    protected Set<Entity> toRemove = new HashSet<>();

    protected CompanyVerifiedState companyVerifiedState;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(190).setWidth(300).setResizable(false);
        this.companyVerifiedState = (CompanyVerifiedState) params.get("companyVerifiedState");
        this.companyVerifiedState.setByUser(userSession.getCurrentOrSubstitutedUser().getName());
    }

    public void approved(Component source) {
        this.companyVerifiedState.setState(VerifiedStateEnum.VERIFIED);
        toCommit.add(this.companyVerifiedState);
        dcCommit();
    }

    public void notApproved(Component source) {
        if (comment.getValue()!= null) {
            String txt = comment.getValue().toString();
            if (!txt.isEmpty()) {
                this.companyVerifiedState.setState(VerifiedStateEnum.CANNOTVERIFIED);
                this.companyVerifiedState.setCommentRu(txt);
                toCommit.add(this.companyVerifiedState);
                Correspondent correspondent = this.companyVerifiedState.getCorrespondent();
                toRemove.add(correspondent);
                dcCommit();
            }
        }
    }

    private void dcCommit() {
        dataManager.commit(new CommitContext(toCommit, toRemove));
        close("close");
    }

    @Override
    public boolean close(String actionId) {
        return ((Window) frame).close(actionId);
    }
}