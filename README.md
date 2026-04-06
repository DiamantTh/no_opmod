# No OP.Mod

Eine **freie, quelloffene** Fabric-Client-Mod für Minecraft mit Fokus auf den
[OPSUCHT](https://opsucht.net)-Server.

Lizenz: [AGPLv3+](LICENSE) · Minecraft: 1.21.4 / 1.21.11 · Loader: Fabric

---

## Was die Mod macht (und machen soll)

No OP.Mod ergänzt den Minecraft-Client um Komfort- und Auswertungsfunktionen,
die speziell auf die Spielmechaniken von OPSUCHT ausgelegt sind.

### Aktuell implementiert

| Funktion | Beschreibung |
|---|---|
| **Marktpreis-Tooltips** | Zeigt aktuelle Marktpreise direkt im Item-Tooltip an |
| **Merchant- & Shard-Werte** | Lädt Händler- und Splitter-Kurse und rechnet sie in Bewertungen ein |
| **Container-Overlay** | Zeigt den Gesamtwert von Kisten- und Shulker-Inhalten |
| **Quick-Buttons** | Schaltflächen in Kisten-Screens für `/jobs`, `/shard`, `/spawn` |
| **Job-Tracker** | Liest OPSUCHT-Job-Nachrichten aus dem Chat und berechnet Stundenwerte |
| **HUD-Overlay** | Zeigt Job, XP und geschätzte Stundenwerte im Spielbildschirm |
| **/rename- & /sign-Schutz** | Fängt diese Befehle vor dem Absenden ab und fordert eine Bestätigung |
| **Command-Kurzformen** | `1k` → `1000`, `2.5m` → `2500000` in Geld-Befehlen (`/pay`, `bank`, …) |
| **Config + ModMenu** | Alle Funktionen einzeln ein-/ausschaltbar über eine Einstellungsseite |
| **Inventar-Buttons** | Schnellzugriff-Buttons für `/jobs`, `/shard`, `/spawn` direkt im Spielerinventar |
| **Shulker-Rekursion** | Shulker-Box-Inhalte im Container-Overlay mitbewerten (via Komponenten-API) |
| **Inventar-voll-Warnung** | Pulsierender roter HUD-Hinweis wenn kein freier Inventar-Slot mehr vorhanden |
| **Keybinds** | Konfigurierbare Tastenbelegungen (Einstellungen, HUD-Toggle, Markt-Refresh) |
| **Discord RPC (Stub)** | Aktivierbarer Discord-Präsenz-Service – standardmäßig deaktiviert, da viele Spieler bereits eine eigene RPC-Mod nutzen |

### Geplant (Roadmap)

- Offhand-Blocker (verhindert versehentliche Offhand-Interaktionen)
- Anvil-Normalisierung (erkennt OPSUCHT-Preiseingabe-Dialoge und wendet Kurzformen an)
- Session-Lifecycle (Job-Tracker-Reset bei Server-Join und -Disconnect)

---

## Warum eine eigene Mod?

Es gibt bereits eine OPSUCHT-eigene Client-Mod (im Folgenden „OPMOD"), die
viele dieser Funktionen enthält. No OP.Mod ist kein direkter Ersatz oder Klon,
sondern entstand aus zwei konkreten Gründen:

**1. Lizenz und Quelloffenheit**  
OPMOD wird unter einer *All Rights Reserved*-Lizenz vertrieben. Quellcode ist
nicht öffentlich zugänglich. Für mich ist Open Source keine bloße Präferenz,
sondern eine Grundüberzeugung: FOSS ist in vielen Ökosystemen wichtig – von
Betriebssystemen über Frameworks bis zu Spielmods. Ich schaue mir auch
nicht-FOSS-Projekte an und schätze sie, aber wenn ich selbst etwas baue, dann
offen und nachvollziehbar.

**2. Eigene Kontrolle über die Implementierung**  
Eine eigene Codebasis erlaubt es, Designentscheidungen selbst zu treffen,
auf Probleme schnell zu reagieren und die Mod ohne Abhängigkeit von der
Entwicklung Dritter weiterzupflegen.

Dieser Mod ist **keine Kritik** an den Entwicklern von OPMOD. Die Entscheidung
für eine proprietäre Lizenz ist legitim – jedes Team trifft diese Entscheidung
aus eigenen Gründen, und das ist ihr gutes Recht. No OP.Mod ist die freie
Alternative für alle, denen Quelloffenheit bei Software wichtig ist.

---

## Wie die Analyse erfolgte

Um zu verstehen, welche Funktionen sinnvoll sind und wie OPSUCHT-spezifische
Mechaniken funktionieren, wurde OPMOD technisch analysiert. Das Vorgehen war:

- **Werkzeuge:** Gängige Java-Dekompilier-Werkzeuge (z. B. Vineflower/Fernflower
  über IntelliJ, Bytecode-Viewer)
- **Gegenstand:** Ausschließlich Clientlogik – OPMOD liest keine geheimen
  Serverdaten, sondern wertet sichtbare Chat-, Screen- und Item-Informationen
  aus sowie öffentliche OPSUCHT-APIs
- **Ergebnis:** Kein Code wurde übernommen. Die Analyse diente ausschließlich
  dem Verständnis der Spielmechaniken und der OPSUCHT-Chat-/Screen-Formate

Das vollständige technische Analysedokument liegt unter
[`doc/OPMOD-Architektur-und-Bauplan.md`](doc/OPMOD-Architektur-und-Bauplan.md).

---

## Build

Voraussetzungen: Java 21, Gradle (Wrapper inklusive)

```bash
# Beide Versionen bauen
./gradlew :1.21.4:remapJar
./gradlew :1.21.11:remapJar
```

JARs landen in `1.21.4/build/libs/` bzw. `1.21.11/build/libs/`.

---

## Kotlin + Java

Die Codebasis nutzt beide Sprachen:

- **Mixins** (`hooks/`) → Java (sicherer mit Annotation-Prozessoren)
- **Neue Services und Datenmodelle** → Kotlin bevorzugt

---

## Lizenz

Copyright (C) 2026 DiamantTh  
Lizenziert unter der GNU Affero General Public License v3.0 oder später.  
Siehe [LICENSE](LICENSE).
