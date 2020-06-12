/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.mvp.client.AnimationMapper;
import com.googlecode.mgwt.ui.client.widget.animation.Animation;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.ted.commander.client.places.*;


/**
 * Created by pete on 10/17/2014.
 */
public class CommanderAnimationMapper implements AnimationMapper {

    @Override
    public Animation getAnimation(Place oldPlace, Place newPlace) {


        if (newPlace instanceof LoginPlace) return Animations.POP_REVERSE;

        if (newPlace instanceof DailyDetailPlace) return Animations.POP;
        if (oldPlace instanceof DailyDetailPlace && newPlace instanceof DashboardPlace) return Animations.POP_REVERSE;

        if (newPlace instanceof ActivationKeysPlace) return Animations.POP;
        if (oldPlace instanceof ActivationKeysPlace && newPlace instanceof DashboardPlace)
            return Animations.POP_REVERSE;

        if (newPlace instanceof AccountPlace) return Animations.POP;
        if (oldPlace instanceof AccountPlace && newPlace instanceof DashboardPlace) return Animations.POP_REVERSE;

        if (newPlace instanceof LocationListPlace) return Animations.POP;
        if (oldPlace instanceof LocationListPlace && newPlace instanceof DashboardPlace) return Animations.POP_REVERSE;


        if (oldPlace instanceof LocationEditPlace) return Animations.SLIDE_REVERSE;
        if (oldPlace instanceof AccountMembershipPlace) return Animations.SLIDE_REVERSE;
        if (oldPlace instanceof AccountMTUPlace) return Animations.SLIDE_REVERSE;
        if (oldPlace instanceof LocationMTUPlace) return Animations.SLIDE_REVERSE;
        if (oldPlace instanceof ComparisonGraphPlace) return Animations.SLIDE_REVERSE;


        return Animations.SLIDE;

    }
}
