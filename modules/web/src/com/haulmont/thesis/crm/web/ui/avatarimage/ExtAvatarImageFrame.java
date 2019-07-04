package com.haulmont.thesis.crm.web.ui.avatarimage;

import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.thesis.web.ui.avatarimage.AvatarImageFrame;

public class ExtAvatarImageFrame extends AvatarImageFrame {

    //// TODO: 01.08.2016  в родителе есть инжект на Employee!!!

    @Override
    public void init() {
        if (!isInitialized) {
            isInitialized = true;
            initRemoveAction();
            initUploadField();
            embedded.setType(Embedded.Type.IMAGE);
            embedded.setStyleName("thesis-sizable-image");
            embedded.setSource(getDefaultEmbeddedSource());
            upload.setCaption("");
            upload.setDescription(messages.getMessage(getClass(), "uploadButton.caption"));
        }
    }
    @Override
    protected void initRemoveAction() {
        //remove.setStyleName(BaseTheme.BUTTON_LINK);
        remove.setDescription(messages.getMessage(getClass(), "removeDescr"));
        remove.setAction(new AbstractAction("remove") {
            @Override
            public void actionPerform(Component component) {
                if (fileDescriptor != null && !fileDescriptor.equals(item.getValueEx(imageProperty))) {
                    fileStorageTools.removeFile(fileDescriptor);
                    removeFile(fileDescriptor);
                }
                fileDescriptor = null;
                refreshEmbedded(null);
            }

            @Override
            public String getCaption() {
                return "";
            }
        });
    }

}