package com.ted.commander.client.view.adviceEdit.triggerTiles;

import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperInputElement;

/**
 * Created by pete on 6/28/2016.
 */
public class RangeEventListener implements EventListener {
    final int min;
    final int max;
    final PaperInputElement paperInputElement;

    public RangeEventListener(int min, int max, PaperInputElement paperInputElement) {
        this.min = min;
        this.max = max;
        this.paperInputElement = paperInputElement;
    }

    @Override
    public void handleEvent(Event event) {
        if (paperInputElement.getValue().trim().length() > 0){
            Integer v = Integer.parseInt(paperInputElement.getValue());
            if (v < min) paperInputElement.setValue(Integer.toString(min));
            if (v > max) paperInputElement.setValue(Integer.toString(max));
        }
    }
}
