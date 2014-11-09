package com.serge45.app.seats;

import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LayoutGenerationUtils {
    public static void generateTableLayout(TableLayout layout, Context context, int rowCount, int colCount, String text) {
        layout.removeAllViews();
        
        for (int j = 0; j < rowCount; ++j) {
            TableRow row = new TableRow(context);
            row.setGravity(Gravity.CENTER);

            for (int i = 0; i < colCount; ++i) {
                Button btn = new Button(context);
                btn.setText(text);
                btn.setGravity(Gravity.CENTER);
                row.addView(btn, 
                            new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 
                                                      TableRow.LayoutParams.MATCH_PARENT,
                                                      0.5f)
                            );
            }

            layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
                                                             TableLayout.LayoutParams.MATCH_PARENT,
                                                             1.f)
                           );
        }
        
    }
}
