package de.mide.ssidcollector;

import static de.mide.ssidcollector.auxi.DialogHelper.zeigeDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


/**
 * App zum Sammeln von Namen von WiFi-Netzen (SSIDs).
 * <br><br>
 *
 * Code-Beispiele für Scannen nach WLAN-Netzen:
 * <ul>
 *   <li><a href="https://developer.android.com/guide/topics/connectivity/wifi-scan">Beispiel in offizieller Doku</a></li>
 *   <li><a href="https://stackoverflow.com/a/17167318/1364368">Antwort auf SO</a></li>
 * </ul>
 * <br><br>
 *
 * Siehe
 * <a href="https://guides.codepath.com/android/fragment-navigation-drawer">hier</a>
 * für ein Tutorial zur Verwendung von NavigationDrawer.
 * <br><br>
 *
 * Sammeln von WiFi-Namen wenn App im Hintergrund ist (mit Service-Klasse) nicht sinnvoll:
 * <i>"The startScan() method performs a full scan for background apps only a few times each hour. 
 * If a background app calls the method again soon afterward, the WifiManager class provides cached 
 * results from the previous scan."</i>
 * <a href="https://developer.android.com/about/versions/oreo/background-location-limits">(Quelle)</a>
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG4LOGGING = "SsidCollector";

    /** Request Code für Erkennung Callback-Aufruf für Runtime Permissions. */
    private static int REQUEST_CODE = 123;

    /**
     * Lifecycle-Methode: Layout-Datei laden, UI-Elemente holen, Überprüfung
     * ob Gerät WLAN kann; wenn ja, dann WiFiManager holen und BroadcastReceiver
     * registrieren.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    /**
     * Überprüft, ob App aktuell über die für den WLAN-Scan benötigten Permissions verfügt.
     * Wenn dies nicht der Fall ist, dann werden die Permissions auch gleich vom Nutzer
     * angefordert.
     *
     * @return  {@code true} wenn die App schon die benötigten Runtime Permissions hat;
     *          wenn {@code false}, dann werden die Berechtigungen beim Nutzer angefragt
     *          und nach Antwort durch den Nutzer die Callback-Methode
     *          {@code onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean checkRuntimePermissions() {

        int apiLevel = android.os.Build.VERSION.SDK_INT;
        if (apiLevel >= 23) {

            Log.i(TAG4LOGGING, "API-Level von Gerät >= 23, also müssen Runtime-Permissions überprüft werden.");

            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {

                String[] permissionArray = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissionArray, REQUEST_CODE);

                return false;

            } else {

                return true;
            }

        } else {

            // apiLevel < 23, also gibt es noch keine Runtime Permissions
            return true;
        }
    }


    /**
     * Callback-Methode für Anfrage Runtime Permissions beim Nutzer.
     *
     * @param requestCode  Erkennungs-Code
     *
     * @param permissions  Array der angefragten Berechtigungen.
     *
     * @param grantResults  Ergebnisse (genehmigt oder abgelehnt) für einzelne Berechtigungen.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults[0] != PERMISSION_GRANTED) {

                zeigeDialog(this, "Berechtigung verweigert",
                        "Sie haben der App die benötigten Berechtigungen verweigert, sie kann deshalb nicht genutzt werden.");
            }
        }
    }

}
