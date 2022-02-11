---
title: Modal
layout: layouts/headlessWithContentNav.njk
permalink: /headless/modal/
eleventyNavigation:
    key: modal
    title: Modal
    parent: headless
    order: 70
demoHash: modal
teaser: "Ein modaler Dialog stellt beliebigen Content auf einer Ebene über der restlichen Applikation dar. 
Intelligentes Maus- und Tastaturmanagement beschränken die Interaktion dabei auf das modale Fenster, solange dieses
geöffnet ist."
---

## Einfaches Beispiel

Um einen modalen Dialog anzulegen, muss die `modal` Fabrik Funktion aufgerufen. Da diese per se noch nichts rendert,
bekommt diese als große Ausnahme keinerlei Standard-Parameter übergeben. Die oberste sichtbare Struktur wird durch
`modalPanel` erzeugt. Innerhalb dieses Scopes wird dann der komplette Inhalt des modalen Fensters inkl. des sogenannten
Overlay-Bereichs (`modalOverlay`) erzeugt.

Die boolesche Datenbindung muss immer angegeben werden, da ein modaler Dialog logischerweise nur durch ein von außen
kommendes Event geöffnet werden kann, wie z.B. durch den Klick auf einen Button.

Das Schließen wird idR. innerhalb des modalen Dialogs angetriggert. Dazu stellt der Scope von `modal` einen Handler
`close` bereit.

```kotlin
val toggle = storeOf(false)

button {
    +"Open"
    clicks.map { true } handledBy toggle.update
}

modal {
    openClose(toggle)
    modalPanel {
        modalOverlay {
            // add styling and mybe some pleasent transition
        }
        div {
            p { +"I am some modal dialog! Press Cancel to exit."}
            // you should at lease add one focusable element to the modal!
            button {
                type("button")
                +"Cancel"
                clicks handledBy close // use handler to set the state to `false`
            }
        }
    }
}
```

## Fokus Management

Um die Interaktion mit der restlichen Applikation auch per Tastensteuerung zu unterbinden, ist in den modalen Dialog
von Haus aus eine sogenannte Fokus-Falle integriert. Daher zirkuliert der Fokus mittels [[Tab]] durch alle
fokussierbaren Elemente, ohne das modale Panel zu verlassen.

Aus diesem Grund sollte auch immer mindestens ein fokussierbares Element in dem modalen Fenster vorhanden sein.

Per default wird immer das erste fokussierbare Element mit dem Fokus versehen. Um ein bestimmtes Tag mit dem initialen
Fokus zu versehen, kann die Funktion `setInitialFocus` in einem `Tag` aufgerufen werden. 

```kotlin
modal {
    openClose(toggle)
    modalPanel {
        p { +"I am some modal dialog! Press Cancel to exit."}
        // first focusable element
        button {
            type("button")
            +"Cancel"
            clicks handledBy close 
        }
        // second focusable element
        button {
            type("button")
            +"I will get the initial focus!"
            // adds a data-attribute to the tag so the focus can be delegated here
            setInitialFocus()
        }
    }
}
```

## Verwendung mit Titel und Beschreibung

Aus Zugänglichkeitsgründen sollte ein modaler Dialog mit einem Titel und mindestens einem beschreibenden Element
ausgestattet werden. Dies geschieht über die Fabrik Funktionen `modalTitle` und `modalDescription`.

```kotlin
modal {
    openClose(toggle)
    modalPanel {         
        // add title and description
        modalTitle { +"Example Dialog" }
        modalDescription { 
            +"I am some modal dialog! Press Cancel to exit."
        }
        
        button {
            type("button")
            +"Cancel"
            clicks handledBy close
        }
    }
}
```

## Maus Interaction

Per default wird keinerlei Maus-Interaktion durch den modalen Dialog unterstützt. Typischerweise wird innerhalb eines
solchen Dialogs ein klickbares Element eingebaut werden, welches den `close` Handler triggert und damit den Dialog
schließt.

## Keyboard Interaction

| Command               | Description                                                                                                                         |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| [[Tab]]               | Zirkuliert durch alle fokussierbaren Element eines geöffneten modalen Fensters. Es sollte immer ein solches Element vorhanden sein! |
| [[Shift]] + [[Tab]]   | Zirkuliert rückwärts durch alle fokussierbaren Element eines geöffneten modalen Fensters.                                           |

## API

### Summary / Sketch
```kotlin
modal() {
    // Felder
    openClose: DatabindingProperty<Boolean>
    close: SimpleHandler<Unit>
    
    // Bausteine
    modalPanel() {
        modalOverlay() { }
        modalTitle() { }
        modalDescription() { } // use multiple times
        
        // setInitialFocus() within one tag is possible
    }
}
```

### `modal`

Parameter: **keine**

Default-Tag: Es wird kein Tag gerendert!

| Scope Feld  | Typ                            | Description                                                               |
|-------------|--------------------------------|---------------------------------------------------------------------------|
| `openClose` | `DatabindingProperty<Boolean>` | Zwei-Wege-Datenbindung für das Öffnen und Schließen. Muss gesetzt werden! |
| `close`     | `SimpleHandler<Unit>`          | Handler zum Schließen des Dialogs von innen heraus                        |


### `modalPanel`

Verfügbar im Scope von: `modal`

Parameter: `classes`, `id`, `scope`, `tag`, `initialize`

Default-Tag: `div`

### `modalOverlay`

Verfügbar im Scope von: `modalPanel`

Parameter: `classes`, `scope`, `tag`, `initialize`

Default-Tag: `div`

### `modalTitle`

Verfügbar im Scope von: `modalPanel`

Parameter: `classes`, `scope`, `tag`, `initialize`

Default-Tag: `h2`

### `modalDescription`

Verfügbar im Scope von: `modalPanel`

Parameter: `classes`, `scope`, `tag`, `initialize`

Default-Tag: `p`