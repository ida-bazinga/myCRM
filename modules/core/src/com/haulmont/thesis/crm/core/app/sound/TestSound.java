/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.sound;

import org.springframework.stereotype.Component;

@Component(TestSoundMBean.NAME)
public class TestSound implements TestSoundMBean {

    public void play(String puth) {
        Sound.playSound(puth).join();
        //Sound.playSound("http://s1download-universal-soundbank.com/wav/83.wav").join();
    }
}
