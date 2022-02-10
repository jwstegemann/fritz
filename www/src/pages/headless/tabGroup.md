---
title: TabGroup
layout: layouts/headlessWithContentNav.njk
permalink: /headless/tabgroup/
eleventyNavigation:
    key: tabgroup
    title: TabGroup
    parent: headless
    order: 60
demoHash: tabGroup
teaser: "Eine TabGroup ermöglicht das Umschalten von Inhalten über eine horizontale oder vertikale Liste von 
Tabulatoren."
---

## Einfaches Beispiel

TabGroups werden mit Hilfe der `tabGroup` Fabrik Funktion erzeugt. Es ist in zwei Bereiche unterteilt: In der
`tabList` werden die verfügbaren `tab`s dargestellt, im `tabPanel` der Inhalt des gerade aktiven Tabs.

Durch einen Mausklick auf einen Tab oder durch die Auswahl via Tastatur kann der aktuelle Tab gewählt und damit
auch der Inhalt des Panels umgeschaltet werden.

Die TabGroup ist vollständig agnostisch bezüglich des Typen eines Tabs als auch seines Inhalts. Darum benötigt die
Komponente auch keinerlei Informationen zu den verfügbaren Tabs. Die Tabs an sich werden intern über den *Index*
verwaltet, den sie beim Hinzufügen in die `tabList` inne hatten. Struktur und Inhalt von Tabs und Panels können
vollkommen frei gestaltet werden.

Eine [Datenbindung](#tab-status-von-außen-setzen-und-abfragen) ist rein optional und muss nicht angegeben werden.

```kotlin
// Some domain type and a collection of data to be displayed inside a tab-group 
data class Posting(val id: Int, val title: String, val date: String, val commentCount: Int, val shareCount: Int)

val categories = mapOf(
    "Recent" to listOf(
        Posting(1, "Does drinking coffee make you smarter?", "5h ago", 5, 2),
        Posting(2, "So you've bought coffee... now what?", "2h ago", 3, 2)
    ), // ...
)

tabGroup {
    tabList {
        // typical pattern to use a loop to create the tabList
        categories.keys.forEach { category ->
            tab { +category }
        }
    }
    tabPanels {
        // for each tab there must be a corresponding panel
        categories.values.forEach { postings ->
            panel {
                ul {
                    postings.forEach { posting ->
                        li { +posting.title }
                    }
                }
            }
        }
    }
}
```

## Aktiven Tab stylen

Um den aktiven Tab bezüglich des Styles von den restlichen abzuheben, ist im Scope von `tab` der boolesche Datenstrom
`selected` verfügbar.

Dieser kann benutzt werden, um in Kombination mit `className` verschiedene Stile auf einen Tab anzuwenden oder sogar
ganze Elemente (z.B. ein Icon für den selektierten Tab) ein- und auszublenden.

```kotlin
tabGroup {
    tabList {
        categories.keys.forEach { category ->
            tab {                 
                // use `selected` flow in order to apply separate styling to the tabs 
                className(selected.map { sel ->
                    if (sel == index) "bg-white shadow"
                    else "text-blue-100 hover:bg-white/[0.12] hover:text-white"
                })
                
                +category 
            }
        }
    }
    tabPanels {
        // omitted
    }
}
```

## Tab Status von außen setzen und abfragen

Wie bereits eingangs beschrieben, werden die Tabs lediglich über ihren Index (`0` basiert!) verwaltet.

Aus diesem Grund kann optional im Scope der `tabGroup` Fabrik-Funktion eine `Int` basierte Datenbindung `value` 
angegeben werden. Damit kann sowohl der initial aktive Tab bestimmt werden, als auch über einen anzugebenden Handler 
der aktuell gewählte Tab abgefragt werden.

Wird die Datenbindung nicht angegeben, so wird initial immer der erste, aktive Tab gewählt.

```kotlin
val currentIndex = storeOf(1) // preselect *second* tab (0-based as all collections in Kotlin)

currentIndex.data handledBy {
    console.log("Current Index is: $it")
}

tabGroup {
    
    // apply two-way-data-binding via index based store
    value(currentIndex)
    
    tabList {
        categories.keys.forEach { category ->
            tab { +category }
        }
    }
    tabPanels {
        // omitted
    }
}
```

## Deaktivieren von Tabs

Tabs können dynamisch aktiviert und auch deaktiviert werden. Deaktivierte Tabs können weder per Maus noch per Tastatur
aktiviert werden, noch werden sie als initialer Tab gewählt.

Per default ist jeder Tab zunächst immer aktiv.

Um einen Tab zu aktivieren oder zu deaktivieren, steht im Scope von `tab` der boolesche Handler `disable` zur Verfügung.

Der aktuelle Status kann über den booleschen Datenstrom `disabled` abgefragt werden. Letzteres ist primär für das
Styling relevant, denn ein deaktivierter Tab sollte sich optisch von aktiven abheben.

```kotlin
tabGroup {
    tabList {
        categories.keys.forEach { category ->
            tab {
                // reduce opacity for disabled tabs
                className(disabled.map { sel ->
                    if (sel == index) "opacity-50" else ""
                })
                
                +category
                
                // simply disable tab "Trending" forever
                if(category == "trending") disable(true)
                
                // toggle disable state of tab "Popular" every 5 seconds
                if(category == "Popular") {
                    generateSequence { listOf(true, false) }.flatten().asFlow()
                        .onEach { delay(5000) } handledBy disable
                }
            }
        }
    }
    tabPanels {
        // omitted
    }
}
```

## Vertikale TabGroup

Eine TabGroup kann sowohl horizontal (default) als auch vertikal dargestellt werden. Diese Unterscheidung ist an sich
nur abhängig von der optischen Gestaltung, aber ändert aber die Bedienung per Tastatur und muss dementsprechend der
Komponente explizit bekannt gemacht werden.

Bei einer horizontalen TabGroup kann man mittels den Pfeil-Tasten [[←]] und [[→]] zwischen den Tabs wechseln, bei einer
vertikalen TabGroup mit [[↑]] und [[↓]].

Dazu existiert die Property `orientation`, die die beiden Enum-Werte `Horizontal` oder `Vertical` annehmen kann.

```kotlin
tabGroup {
    
    // will be evaluated only once at the initial rendering, so it is not reactive!
    orientation = Orientation.Vertical
    
    tabList {
        // omitted        
    }
    tabPanels {
        // omitted
    }
}
```

## API

### Summary / Sketch
```kotlin
tabGroup() {
    // Felder
    value: DatabindingProperty<Int> // optional
    selected: Flow<Int>
    orientation: Orientation

    // Bausteine
    tabList() {
        // Bausteine
        // for each tab {
            tab() {
                // Felder
                disabled: Flow<Int>
                disable: SimpleHandler<Int>
            }
        // }
    }
    
    tabPanel() {
        // Bausteine
        // for each tab {
            panel() { }
        // }        
    }
}
```
