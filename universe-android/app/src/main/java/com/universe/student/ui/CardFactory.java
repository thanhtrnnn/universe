package com.universe.student.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.universe.student.R;

public final class CardFactory {

    private CardFactory() {
    }

    public static View dataCard(Context context, String title, String subtitle, String meta) {
        View card = LayoutInflater.from(context).inflate(R.layout.view_data_card, null, false);
        setText(card, R.id.cardTitle, title);
        setText(card, R.id.cardSubtitle, subtitle);
        setText(card, R.id.cardMeta, meta);
        return card;
    }

    public static void setAction(View card, String label, View.OnClickListener listener,
                                 boolean enabled) {
        Button action = card.findViewById(R.id.cardAction);
        action.setText(label);
        action.setEnabled(enabled);
        action.setVisibility(View.VISIBLE);
        action.setOnClickListener(listener);
    }

    public static View statCard(Context context, String value, String label) {
        View card = LayoutInflater.from(context).inflate(R.layout.view_stat_card, null, false);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        setText(card, R.id.statValue, value);
        setText(card, R.id.statLabel, label);
        return card;
    }

    private static void setText(View parent, int id, String value) {
        TextView textView = parent.findViewById(id);
        textView.setText(value == null || value.isEmpty() ? "-" : value);
    }
}
