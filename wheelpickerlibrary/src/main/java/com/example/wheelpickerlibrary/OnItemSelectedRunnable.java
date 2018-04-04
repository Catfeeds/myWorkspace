// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.wheelpickerlibrary;

final class OnItemSelectedRunnable implements Runnable {
    final WheelView wheelView;

    OnItemSelectedRunnable(WheelView wv ) {
        this.wheelView = wv;
    }

    @Override
    public final void run() {
        wheelView.onItemSelectedListener.onItemSelected(wheelView.getSelectedItem());
    }
}
