package de.mide.ssidcollector;

import static de.mide.ssidcollector.auxi.DialogHelper.zeigeDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;

import de.mide.ssidcollector.fragmente.DatenbankFragment;
import de.mide.ssidcollector.fragmente.SsidSammelFragment;


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

    /** Tag for Logging to be used by all classes of the app. */
    public static final String TAG4LOGGING = "SsidCollector";

    /** Request Code für Erkennung Callback-Aufruf für Runtime Permissions. */
    private static int REQUEST_CODE = 123;

    /** Toolbar zur Anzeige des Hamburger-Menüs. */
    private Toolbar _toolbar = null;

    /** Die "Schublade" (Drawer) mit den Menü-Einträgen für die Navigation. */
    private DrawerLayout _navigationSchublade = null;

    /** UI-Element mit Menü-Einträgen. */
    private NavigationView _hauptmenueNavigationView = null;

    /** ActionBar-Schalter. */
    private ActionBarDrawerToggle _actionBarSchalter = null;

    /**
     * Manager-Objekt um zur Laufzeit das zum ausgewählten Menü-Eintrag gewählte Fragment in
     * das Platzhalter-Element (FrameLayout).
     */
    private FragmentManager _fragmentManager = null;


    /**
     * Lifecycle-Methode: Layout-Datei laden, UI-Elemente holen, Überprüfung
     * ob Gerät WLAN kann; wenn ja, dann WiFiManager holen und BroadcastReceiver
     * registrieren.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRuntimePermissions();


        _toolbar = findViewById(R.id.toolbar);

        _navigationSchublade = findViewById(R.id.schubladen_layout);

        _hauptmenueNavigationView = findViewById(R.id.navigationView);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        _hauptmenueNavigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        fragmentAnzeigen(menuItem);
                        return true;
                    }
                }
        );


        _fragmentManager   = getSupportFragmentManager();
        _actionBarSchalter = erzeugeActionBarDrawerToggle();

        _actionBarSchalter.setDrawerIndicatorEnabled(true);
        _actionBarSchalter.syncState();

        _navigationSchublade.addDrawerListener(_actionBarSchalter);

        MenuItem defaultMenuItem = _hauptmenueNavigationView.getMenu().findItem(R.id.nav_sammeln);
        fragmentAnzeigen(defaultMenuItem);
    }


    /**
     * Methode erzeugt und liefert ActionBar-Schalter zurück.
     *
     * @return  ActionBar-Schalter
     */
    private ActionBarDrawerToggle erzeugeActionBarDrawerToggle() {

        return new ActionBarDrawerToggle( this,
                                          _navigationSchublade,
                                          _toolbar,
                                          R.string.schublade_auf,
                                          R.string.schublade_zu
                                         );
    }


    /**
     * Fragment passend zum gerade ausgewählten Menu-Item auswählen.
     *
     * @param menuItem  Menu-Item, das ausgewählt wurde.
     */
    public void fragmentAnzeigen(MenuItem menuItem) {

        Fragment fragment      = null;
        Class    fragmentClass = null;

        int menuItemId = menuItem.getItemId();
        switch( menuItemId ) {

            case R.id.nav_sammeln:
                    fragmentClass = SsidSammelFragment.class;
                break;

            case R.id.nav_datenbank:
                    fragmentClass = DatenbankFragment.class;
                break;

            default:
                Log.e(TAG4LOGGING, "Unbehandeltes Menu-Item " + menuItemId + " aufgerufen.");
                zeigeDialog(this, "Interner Fehler", "Unbehandeltes Menu-Item ausgewählt.");
        }

        try {

            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception ex) {

            Log.e(TAG4LOGGING, "Konnte fragmentClass=\"" + fragment + "\" nicht laden.", ex);
            zeigeDialog(this, "Interner Fehler", "Fragment-Klasse konnte nicht instanziiert werden.");
            return;
        }

        _fragmentManager.beginTransaction().replace(R.id.platzhalterFrame, fragment).commit();

        menuItem.setChecked(true);

        setTitle( menuItem.getTitle() );

        _navigationSchublade.closeDrawers();
    }


    /**
     * Event-Handler-Methode um Navigations-Schublade auszuziehen.
     *
     * @param menuItem  Menü-Eintrag, der Event ausgelöst hat.
     *
     * @return {@code true} wenn Event in dieser Methode behandelt werden konnte.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if ( _actionBarSchalter.onOptionsItemSelected(menuItem) ) {

            return true;
        }

        return super.onOptionsItemSelected(menuItem);
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

        if (permissions.length == 0) {

            Log.w(TAG4LOGGING, "Callback-Methode für RuntimePermissions hat Antwort für 0 Permissions enthalten!");
            return;
        }

        if (requestCode == REQUEST_CODE) {

            if (grantResults[0] != PERMISSION_GRANTED) {

                zeigeDialog(this,
                        "Berechtigung verweigert",
                        "Sie haben der App die benötigten Berechtigungen verweigert, sie kann deshalb nicht genutzt werden.");
            }
        }
    }


    /**
     * Synchronisierung von ActionBar-Schalter nach Wiederherstellung oder Drehen des Geräts.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        _actionBarSchalter.syncState();
    }


    /**
     * Siehe {@link #onPostCreate(Bundle)}.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        _actionBarSchalter.onConfigurationChanged(newConfig);
    }

}
