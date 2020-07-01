package de.mide.ssidcollector.auxi;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Klasse enthält Hilfsmethoden für Erzeugen von Dialogen.
 */
public class DialogHelper {

    /**
     * Hilfsmethode zur Anzeige eines Dialogs für Fehlermeldungen u.ä.
     *
     * @param context  Context (Selbstreferenz auf aufrufende Activity).
     *
     * @param title  Dialog-Titel
     *
     * @param nachricht  Eigentlicher Text des Dialogs.
     */
    public static void zeigeDialog(Context context, String title, String nachricht) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(nachricht);

        dialogBuilder.setPositiveButton( "Weiter", null );
        dialogBuilder.setCancelable(false);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
