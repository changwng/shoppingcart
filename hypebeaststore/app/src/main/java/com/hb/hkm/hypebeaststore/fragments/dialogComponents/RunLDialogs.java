package com.hb.hkm.hypebeaststore.fragments.dialogComponents;

import android.content.Context;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * Created by hesk on 2/3/15.
 */
public class RunLDialogs {
    public static void strDemo2(Context ctx, final String context) {
        final MaterialDialog dalm = new MaterialDialog(ctx)
                .setTitle("MaterialDialog")
                .setMessage(context)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  dalm.dismiss();

                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  dalm.dismiss();

                    }
                });

        dalm.show();


    }

    public static void strDemo(Context ctx
            , final String title, final String context_str) {
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(ctx, title, "ok");

        // Now we can any of the following methods.
        builder.content(context_str);
        builder.negativeText(android.R.string.cancel);
        builder.darkTheme(true)
        ; // Enables right to left positioning for languages that may require so.

        // Now we can build the dialog.
        CustomDialog customDialog = builder.build();

        // Show the dialog.
        customDialog.show();

    }
}
