/**
 * Persistenz mit Room-API.
 * <br><br>
 *
 * Einstiegs-Doku:
 * <ul>
 *   <li><a href="https://developer.android.com/training/data-storage/room">Einstiegs-Doku</a></li>
 *   <li><a href="https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9">Tutorial auf medium.com</a></li>
 * </ul>
 * <br><br>
 *
 * Room-Libraries in Datei {@code app/build.gradle} eintragen:
 * <pre>
 * implementation 'android.arch.persistence.room:runtime:2.2.5'
 * annotationProcessor 'android.arch.persistence.room:compiler:2.2.5'
 * </pre>
 * <br><br>
 *
 * <b>Verwendung:</b>
 * <ul>
 *     <li>Singleton-Instanz von {@code MeineDatenbank} holen.</li>
 *     <li>Relevantes DAO mit entsprechender Getter-Methode von {@code MeineDatenbank}-Instanz.</li>
 *     <li>Methode für gewünschte DB-Operation an DAO-Objekt aufrufen.</li>
 * </ul>
 */
package de.mide.ssidcollector.db;