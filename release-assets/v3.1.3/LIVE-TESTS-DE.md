# Spool Studio 3.1.3: fünf Live-Tests

App: **3.1.3 / Versionscode 27**. Für native Tags: **Spoolman ab 0.27.0**. Verwende eine Testspule A, eine zweite Testspule B und einen entbehrlichen Tag. Notiere Firmware-Version und Ergebnisse. Vor Test 5 Spoolman sichern: Die Firmware-Speicherumschaltung migriert alle Zuordnungen des Servers, nicht nur den Test-Tag. Nicht während eines Drucks umschalten; Moonraker wird dabei neu gestartet.

## Welche Firmware?

Stand 04.11.2026: Neueste reguläre Version ist `2.0.0-paxx12-24`. Damit gehen Tests 1–4. Für Test 5 ist eine Firmware mit PR #867 nötig. Der am 04.11. geprüfte Rolling-Build `2.1.0-paxx12-develop-601e196` enthält ihn nachweislich; ein späterer Build muss ihn ebenfalls enthalten. Rolling ist laut Projekt ein experimenteller Develop-Build. Für Tests 1–4 ist kein Wechsel darauf erforderlich.

## 1. Farbe offline lesen

Lies einen bekannten OpenSpool-Tag mit gespeicherter Farbe zunächst online. Unterbrich dann nur am Telefon die Verbindung zum Spoolman-Server (z. B. Telefon-WLAN aus, NFC an) und scanne erneut. Lasse die Server-URL eingetragen, damit gerade der Fehlerfall geprüft wird.

**Erwartet:** Farbe/Material erscheinen und bleiben auch nach dem Verbindungsfehler sichtbar. Keine serverbestätigte Spule wird ausgewählt. Mit einem bereits entschlüsselbaren Bambu-Tag darfst du denselben Test wiederholen. Bei einem Tag mit ausschließlich lesbarer UID ist offline keine Farbe zu erwarten. Danach Verbindung wiederherstellen.

## 2. Nur card_uids am Drucker

Lasse den Drucker auf `card_uids`. Wähle in der App **Only card_uids**, wähle Testspule A neu aus, scanne den Tag und verknüpfe ihn über **Link scanned UID**. Lege die Spule am U1 ein bzw. löse einen erneuten RFID-Scan aus.

**Erwartet:** Der Drucker erkennt A. In Spoolman steht die UID in `card_uids`; die native Tags-Liste bleibt gegenüber vorher unverändert. Verknüpfe denselben Tag ausdrücklich mit B und wiederhole den Scan: Jetzt muss B erkannt werden.

## 3. Beide Speicher

Wähle **Both (compatibility)**, wähle A erneut aus und verknüpfe den Tag mit A. Kontrolliere in Spoolman sowohl die native Tags-Liste als auch `card_uids`.

**Erwartet:** Beide zeigen auf A; der weiterhin auf `card_uids` laufende U1 erkennt A ebenfalls. Ein erneuter Scan in der App öffnet A.

## 4. Druckerzuordnung leeren und Tag entfernen

Öffne in der App **Toolhead Status** und tippe auf das **Stiftsymbol** in der Karte des betroffenen Toolheads. Im Dialog **Assign spool** steht oberhalb der Spulenliste **Clear printer assignment**. Wähle diesen Eintrag. Prüfe danach die beiden Tag-Speicher in Spoolman. Scanne am Drucker erneut; entferne anschließend in der App unter **Manage tag links → Unlink** den Test-Tag.

**Erwartet:** Das Leeren entfernt nur die Druckerzuordnung; die UID-Verknüpfungen bleiben erhalten, und ein späterer Scan darf A erneut zuordnen. **Unlink** entfernt dagegen die Verknüpfungen aus beiden Speichern, weil Both gewählt ist. Ein bereits geladener Druckerstatus muss dadurch nicht sofort verschwinden. Nach erneutem Scan darf keine Zuordnung aus den entfernten Servereinträgen entstehen; notiere es, falls die Firmware noch einen alten Cache verwendet.

## 5. Native-only und Rückwechsel – nur mit #867

Stelle den Test-Tag zunächst wieder in Both auf A. Öffne firmware-config mit dem Parameter `?hidden`; dort ist **Spoolman Tag Storage** zunächst versteckt. Schalte die Firmware auf native Tags um und warte, bis Migration und Moonraker-Neustart abgeschlossen sind. Wähle danach in der App **Only native tags** und scanne den Tag am U1. Verknüpfe ihn ausdrücklich mit B und scanne erneut. Schalte anschließend über firmware-config zurück auf `card_uids`, warte auf den Abschluss und wähle in der App **Only card_uids**.

**Erwartet:** Im nativen Modus erkennt der Drucker erst A, dann B; `card_uids` wird durch die App nicht wieder befüllt. Nach dem Firmware-Rückwechsel ist die UID in `card_uids`, die native Verknüpfung entfernt, und der Drucker erkennt weiterhin B. Erst fortfahren, wenn die Firmware-Migration keine ungelösten Fehler meldet.

Bitte je Test **OK / Fehler** zurückmelden, dazu Firmware-Version, Spoolman-Version und bei Fehlern den genauen Schritt bzw. Screenshot. Falls Test 5 nicht ausgeführt wird, bleibt die native Firmware-Kompatibilität ausdrücklich unbestätigt.
