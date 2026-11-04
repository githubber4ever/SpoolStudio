# Spool Studio – ausführliche Anleitung

Stand: Version **3.1.3 (27)**. Die Bedienelemente der App sind englisch; diese Anleitung verwendet ihre tatsächlichen Namen.

## 1. Voraussetzungen und Installation

- Android **10 / API 29 oder neuer**, für das Lesen und Schreiben ein NFC-fähiges Gerät mit aktiviertem NFC.
- Ein erreichbarer Spoolman-Server für Spulenauswahl, Speichern und Tag-Verknüpfungen.
- Für native NFC/RFID-Tags: Spoolman **0.27.0 oder neuer**.
- Optional für den Snapmaker U1: kompatible Paxx12 Extended Firmware mit aktivierter Spoolman-/SpoolLink-Integration und erreichbarem Moonraker.
- Bambu-Import benötigt einen selbst bereitgestellten Schlüssel und ein unterstütztes, lesbares Tag. Der Schlüssel ist nicht enthalten.

Das APK von der [Release-Seite](https://github.com/GeorgHo/SpoolStudio/releases/latest) herunterladen und installieren. Falls Android fragt, der verwendeten Browser- oder Dateimanager-App die Installation erlauben. Ein Update über die bestehende Installation erhält normalerweise die Einstellungen; die App nicht vorher deinstallieren.

Für die geprüften Abläufe wurde Spoolman 0.27.0 verwendet. Die ersten Legacy-Tests liefen mit **2.0.0-paxx12-24**, native Speicherung und Hin-/Rückmigration mit **2.1.0-paxx12-pr867**. Die neuere Firmware ist für die getestete native Speicherumschaltung erforderlich, nicht für alle Grundfunktionen der App. Maßgeblich für diese Umschaltung ist eine Firmware mit [PR #867](https://github.com/paxx12-snapmaker-u1/SnapmakerU1-Extended-Firmware/pull/867).

## 2. Verbindungen einrichten

1. Über das Zahnrad **Settings** öffnen.
2. Die Spoolman-Adresse einschließlich Port eintragen, beispielsweise `http://spoolman.local:8000`.
3. **Test Spoolman Connection** ausführen. Serverinformationen und angebotene Feldprüfungen beachten.
4. Für die Druckeranbindung die Moonraker-Adresse eintragen, beispielsweise `http://printer.local:7125`, und **Test Moonraker Connection** ausführen.
5. Auf dem Drucker die Spoolman-/SpoolLink-Integration aktivieren und dort denselben Spoolman-Server einstellen.
6. Einstellungen speichern und zur Hauptansicht zurückkehren.

Das Telefon muss die eingetragenen Adressen erreichen können. `localhost` bezeichnet auf dem Telefon das Telefon selbst. Bei mehreren Verbindungsprofilen vor Änderungen prüfen, welches Profil und welcher Server aktiv sind.

## 3. Den richtigen Tag-Speicher wählen

Ein physisches NFC-Tag besitzt eine Hardware-UID. Spoolman kann diese mit einer konkreten Spule verknüpfen. Diese Verknüpfung ist von den auf dem Tag gespeicherten Materialdaten unabhängig.

Unter **Settings → Spoolman → NFC / RFID tag storage** stehen folgende Modi zur Verfügung:

| Modus | Lesen | Verknüpfen und Entfernen | Verwendung |
|---|---|---|---|
| **Both (compatibility)** | Native Zuordnung zuerst, dann `card_uids` | Beide Speicher, sofern native Tags unterstützt werden; auf älteren Servern Legacy | Standard für die Übergangszeit |
| **Only card_uids** | Nur das Legacy-Feld | Nur `card_uids` | Drucker verwendet Legacy-Speicherung |
| **Only native tags** | Nur native Tags | Nur native Tags | Drucker verwendet native Speicherung; Spoolman ≥ 0.27.0 |

Die Auswahl wird pro Server gespeichert. In einem Einzelmodus bleibt der andere Speicher unberührt. In **Both** gewinnt beim Lesen die native Zuordnung, wenn derselbe UID in beiden Speichern unterschiedliche Spulen zugewiesen sind.

**App und Drucker müssen auf denselben aktiven Speicher zugreifen.** Firmware 2.0.0-paxx12-24 benötigt `card_uids`; dafür **Only card_uids** oder **Both** verwenden. Die Firmware mit PR #867 verwendet genau einen Speicher; sie schreibt nicht beide gleichzeitig.

Eine Modusänderung in der App verschiebt keine Daten. Die aktuelle Formularauswahl und die zuletzt gescannte UID werden dabei zurückgesetzt, damit keine alte Zuordnung versehentlich weiterverwendet wird.

### Speicher umstellen und migrieren

Vor einer Migration eine Spoolman-Sicherung erstellen und die Umstellung mit einer Spule prüfen.

- Die Speicherumschaltung in **firmware-config** einer geeigneten Firmware verschiebt die Verknüpfungen in den gewählten Speicher und leert den bisherigen. Die Rückrichtung wird ebenfalls unterstützt. Die Einstellung war im getesteten Build noch verborgen.
- **Review legacy tag migration** in der App ist eine separate Funktion zum Prüfen/Kopieren bestehender Legacy-Verknüpfungen in native Tags. Sie ist in Both/native-only verfügbar und berücksichtigt auch archivierte Spulen.
- Das zusätzliche Leeren erfolgreich migrierter Legacy-Einträge ist eine ausdrückliche Option. Konflikte, ungültige oder doppelte Einträge müssen geprüft werden.
- Eine App-Migration schaltet den Drucker nicht automatisch um. Für dessen Umschaltung und die Rückmigration firmware-config verwenden.

Nicht nach einer vollständigen Firmware-Migration weiter ausschließlich den geleerten alten Speicher verwenden. App-Modus anpassen, Tag erneut scannen und die Zuordnung kontrollieren.

## 4. Eine Spule auswählen oder anlegen

**Select from Spoolman** öffnet die suchbare Spulenliste. Die gewünschte Spule auswählen und vor dem Speichern ihre ID prüfen.

- **New spool** beginnt mit einer neuen, leeren Spule.
- **New from selected** erstellt eine neue Spule anhand der aktuellen Auswahl. Die neue physische Spule benötigt ihre eigene Tag-Zuordnung.
- **Create in Spoolman** legt den neuen Datensatz an.
- **Update Spoolman** speichert Änderungen an einer vorhandenen Spule.

Material, **Material modifier** und **Variant** sind getrennte Angaben. Beispielsweise kann PETG mit Modifier `2.0` geführt werden; der Modifier soll beim Anlegen aus einer vorhandenen Spule übernommen werden. Optionale Angaben wie Leergewicht, Charge und Kommentar hängen von den Einstellungen ab.

### Farbe und Filamentname

HEX und RGB beschreiben die Farbe. **Filament Name** ist der Produktname und kann darüber hinausgehende Angaben enthalten. Eine Farbänderung soll einen vorhandenen Produktnamen nicht automatisch ersetzen.

**Use color name: …** ist eine freiwillige Aktion zum Übernehmen des vorgeschlagenen Farbnamens als Filamentname. Bei bereits passendem Namen wird sie ausgeblendet; Groß-/Kleinschreibung und zusätzliche Leerzeichen werden dabei berücksichtigt. Beispiel: `Green` und `47D501` / RGB `71, 213, 1` benötigen diese Aktion nicht.

## 5. Ein Tag lesen

1. **Read RFID** wählen und das Tag an die NFC-Antenne des Telefons halten.
2. Das Tag ruhig halten, bis der Lesevorgang abgeschlossen ist.
3. Die angezeigte UID, Spoolman-ID, Material und Farbe prüfen.

Bei erfolgreicher Serverabfrage hat die gespeicherte UID-Zuordnung Vorrang vor Materialdaten auf dem Tag. So kann ein Tag auf eine andere Spule verweisen, ohne dass sein alter physischer Inhalt neu geschrieben wurde.

Ein natives **Filament-Tag** verweist auf ein Produkt und nicht eindeutig auf eine einzelne Spule. Die App zeigt dafür einen Hinweis, statt willkürlich eine Spule auszuwählen.

### Ohne Spoolman-Verbindung

Lesbare OpenSpool-Daten einschließlich Farbe werden lokal angezeigt und bleiben bei fehlgeschlagener Serverabfrage erhalten. Bereits entschlüsselte Bambu-Metadaten können ebenfalls lokal angezeigt werden.

Eine im Tag enthaltene Spulen-ID ist offline keine bestätigte Serverzuordnung. Auch eine alte zwischengespeicherte UID-Zuordnung wird dafür nicht übernommen. Ein leeres oder verschlüsseltes Tag, das nur seine Hardware-UID liefert, enthält offline keine lesbare Farbe.

## 6. Eine gescannte UID verknüpfen

**Link scanned UID** verwendet die zuletzt eingelesene Hardware-UID und verknüpft sie mit der gewählten Spoolman-Spule. Es schreibt keine neuen Materialdaten auf das physische Tag.

1. Tag mit **Read RFID** einlesen.
2. Die gewünschte vorhandene Spule auswählen und deren ID prüfen.
3. **Link scanned UID** wählen und die Rückfrage beachten.
4. Nach Abschluss erneut lesen und die Spoolman-ID kontrollieren.
5. Bei Druckeranbindung auch am Drucker erneut scannen und im Filament Manager oder Toolhead-Status prüfen.

Eine vorhandene native Spulen-Zuordnung kann durch diese ausdrückliche Aktion auf die gewählte Spule verschoben werden. Gewöhnliches Speichern verschiebt native Tags nicht stillschweigend.

### Manage tag links und Unlink

**Manage tag links** steht im Aktionsbereich direkt unter **Link scanned UID**, sofern dessen Aktion verfügbar ist; andernfalls folgt es auf **Read RFID**. Für eine gewählte bestehende Spule werden Anzahl und Liste der Verknüpfungen aus den ausgewählten Speichern angezeigt.

- **Manage tag links** öffnet die Liste; **Hide tag links** klappt sie zu.
- **Unlink** entfernt die ausgewählte UID nach Bestätigung aus den gewählten Speichern.
- In Both werden beide Speicher berücksichtigt; im Einzelmodus nur der ausgewählte.
- Unlink verändert weder den Inhalt des physischen Tags noch automatisch die bereits geladene Druckerzuordnung.

Wenn beim erneuten Lesen eine unerwartete Spule erscheint, zuerst Server, Speichermodus und beide möglichen Zuordnungen kontrollieren.

## 7. Ein Tag schreiben

**Write RFID** schreibt die aktuellen OpenSpool-Daten auf ein geeignetes beschreibbares Tag. Die App verwendet den UID-basierten v3-Ablauf; alte Spool-Studio-Tags können nach Bestätigung konvertiert werden.

Vorher die Spule in Spoolman speichern bzw. auswählen, die richtige ID prüfen und anschließend das Tag bis zur Abschlussmeldung an der Antenne halten. Danach erneut lesen und die Zuordnung kontrollieren. Ein Tag-Link und sein physischer Inhalt sind getrennte Dinge: Nur **Link scanned UID** auszuführen aktualisiert die Materialdaten auf dem Tag nicht.

OpenTag3D-Kodierung/Dekodierung und die Anbindung externer Scanner sind nicht Bestandteil dieser Version. Ein vom Drucker unterstütztes Tag-Format bedeutet nicht automatisch, dass die App dieses Format schreiben kann.

## 8. Toolhead Status, Zuordnen und Clear

**Toolhead Status** zeigt die vom Drucker gemeldeten Daten. **Refresh toolhead status** lädt sie neu.

- **App labels** zeigt die von der App zusammengesetzten Spulenangaben.
- **SpoolLink** erlaubt den Vergleich mit den gemeldeten Druckerdaten.
- Das kleine **Stiftsymbol** an einer Toolhead-Karte öffnet die manuelle Zuordnung.
- Im Dialog kann eine Spule gewählt oder **Clear printer assignment** ausgeführt werden.

**Clear printer assignment** entfernt die Spoolman-Zuordnung des Toolheads, lässt aber die Tag-Verknüpfungen bestehen. Danach kann **No ID** erscheinen, während Material, Name oder Farbe weiterhin als geladene Druckerdaten sichtbar sind. Das ist nicht gleichbedeutend mit einer weiterhin vorhandenen Spoolman-ID. Ein erneuter RFID-Scan kann die verknüpfte Spule wieder zuordnen.

| Gewünschte Aktion | Funktion |
|---|---|
| Geladene Druckerzuordnung entfernen | Stiftsymbol → Clear printer assignment |
| Tag von einer Spule lösen | Manage tag links → Unlink |
| Tag einer anderen Spule zuordnen | Lesen → Zielspule auswählen → Link scanned UID |
| Physische Materialdaten des Tags ändern | Write RFID |

Manuelle Zuordnung ersetzt keinen RFID-Scan und keinen tatsächlichen Filament-Ladevorgang. Beim Vergleich mehrerer Druckeroberflächen jeweils nach einem frischen Scan bzw. echtem Laden prüfen.

## 9. First use, Last use und Restmenge

**First use** und **Last use** stammen aus Spoolman. Wenn das betreffende Feld leer ist, zeigt die App **–**. Das Anlagedatum einer Spule ist davon getrennt. Das bloße Lesen eines Tags setzt diese Nutzungsdaten nicht automatisch.

**Remaining** zeigt die verbleibenden Gramm und den Anteil am Ausgangsgewicht. Der Prozentwert wird als ganze Zahl angezeigt. Die Warnfarbe richtet sich nach den verbleibenden **Gramm**, nicht nach Prozent:

| Restmenge | Farbe |
|---|---|
| Über 150 g | Weiß |
| Über 100 bis einschließlich 150 g | Orange |
| Über 50 bis einschließlich 100 g | Hellrot |
| Bis einschließlich 50 g | Rot |

Beispiel: 185 g von 1000 g werden als 18 % angezeigt; der Balken bleibt weiß. Diese Anzeige ist keine Prüfung, ob die Menge für einen bestimmten Druckauftrag ausreicht.

## 10. Archivieren, Löschen und Fehlersuche

**Archive selected spool** dient zum Archivieren einer ausgewählten Spule; **Delete selected spool** zum Löschen nach Bestätigung. Vorher die ausgewählte ID kontrollieren. Zur bloßen Änderung einer Tag-Zuordnung weder die Spule löschen noch archivieren.

| Beobachtung | Prüfen |
|---|---|
| Keine Spulen geladen | Spoolman-Adresse, Port, Netzwerk und Verbindungstest |
| App erkennt richtige Spule, Drucker nicht | Derselbe Server? Derselbe aktive Tag-Speicher? Frischer Drucker-Scan? |
| Offline keine Farbe | Enthält das Tag lesbare Materialdaten oder nur eine UID? |
| Nach Clear weiterhin Material sichtbar | Spoolman-ID getrennt von geladenen Druckermetadaten betrachten |
| Nach Unlink weiterhin alte Drucker-ID | Unlink leert keine bereits geladene Zuordnung; getrennt Clear und erneut scannen |
| Native tags nicht verfügbar | Spoolman-Version ≥ 0.27.0 und Verbindung prüfen |
| Nach Migration keine Zuordnung | App und Drucker auf den tatsächlich befüllten Speicher einstellen |

Bei einer Fehlermeldung nach einer Verknüpfungsänderung aktualisieren und den tatsächlichen Serverzustand prüfen, bevor dieselbe Aktion wiederholt wird.

## 11. Geprüfte Abläufe und Grenzen

Die fünf Live-Testgruppen umfassten Offline-Farbe, Legacy-Zuordnung und Wechsel, Both, getrenntes Clear/Unlink einschließlich erneutem Scan sowie native Migration, Neuzuordnung und Rückmigration. Beim Wechsel in native Tags und zurück wurden jeweils 20 Verknüpfungen kontrolliert. Die App-Modi und Druckerzuordnungen wurden über die Server-/Druckerdaten gegengeprüft. Zusätzlich bestanden 143 automatisierte Tests.

Eine frühere Helix-Beobachtung betraf eine bereits grün dargestellte Spule, deren Detailansicht noch alte Material-/Farbangaben zeigte. Dabei wurde lediglich die Zuordnung gewechselt und das Filament nie tatsächlich neu in den Extruder geladen. Diese Beobachtung wurde **nicht weiter verfolgt oder als Fehler verifiziert**; daraus wird kein bestätigter Helix-Fehler abgeleitet.

[Zur Projektübersicht](../README.md) · [Version 3.1.3 herunterladen](https://github.com/GeorgHo/SpoolStudio/releases/tag/v3.1.3)
