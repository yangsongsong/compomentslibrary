package com.example.momo.myapplication.cenment.eventhook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.momo.myapplication.cenment.CementAdapter;
import com.example.momo.myapplication.cenment.CementViewHolder;


import java.util.ArrayList;
import java.util.List;

public class EventHookHelper<VH extends CementViewHolder> {

    private boolean isAfterBind = false;
    @NonNull
    private final CementAdapter cementAdapter;
    private final List<EventHook<VH>> eventHooks = new ArrayList<>();

    public EventHookHelper(@NonNull CementAdapter cementAdapter) {
        this.cementAdapter = cementAdapter;
    }

    public void add(@NonNull EventHook<VH> eventHook) {
        if (isAfterBind) {
            throw new IllegalStateException("can not add event hook after bind");
        }
        eventHooks.add(eventHook);
    }

    public void bind(@NonNull CementViewHolder viewHolder) {
        for (final EventHook<VH> eventHook : eventHooks) {
            if (!eventHook.mClazz.isInstance(viewHolder)) continue;
            final VH vh = eventHook.mClazz.cast(viewHolder);
            View view = eventHook.onBind(vh);
            if (view != null) {
                attachToView(eventHook, vh, view);
            }
            List<? extends View> views = eventHook.onBindMany(vh);
            if (views != null) {
                for (View v : views) {
                    attachToView(eventHook, vh, v);
                }
            }
        }
    }

    /**
     * bind {@param eventHook} to {@param view}
     */
    private void attachToView(@NonNull EventHook<VH> eventHook, @NonNull VH viewHolder, @Nullable View view) {
        if (view == null) return;
        eventHook.onEvent(view, viewHolder, cementAdapter);
        //set true once having one success bind
        isAfterBind = true;
    }
}
