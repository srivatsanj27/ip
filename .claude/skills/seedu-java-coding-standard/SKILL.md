---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard (https://se-education.org/guides/conventions/java/intermediate.html) that ALL Java code in this project must follow. Use this whenever writing new Java code, editing existing Java code, or reviewing/auditing code in this repository for style — naming, brace style, import ordering, line length, comments, and general layout. Trigger on any request to write, refactor, format, lint, or review Java source in this project, even if the user doesn't name the standard explicitly.
---

# SE-EDU Intermediate Java Coding Standard

This is the coding standard this project's `AGENTS.md` mandates for all Java code. Apply it whenever writing new code; when reviewing or auditing existing code, check against every rule below.

## Naming

- **Packages**: all lower case (e.g. `ace.task`).
- **Classes/enums**: nouns, PascalCase (e.g. `Deadline`, `TaskManager`).
- **Variables**: camelCase. Scope drives length — a large-scope variable deserves a long, descriptive name; a small-scope one (a loop counter, say) can be short (`i`, `j`, `k`, `n` for integers; `c`, `d` for characters).
- **Booleans**: prefix with `is`/`has`/`was`/`can` (variables: `isVisible`, `hasData`; methods: `boolean isCompleted()`, `boolean hasLicense()` — **not** `boolean getCompleted()`). Boolean setters still use `set`: `void setFound(boolean isFound)`.
- **Collections**: plural names (`Collection<Point> points`).
- **Constants**: `ALL_CAPS_WITH_UNDERSCORES` (e.g. `MAX_ITERATIONS`). Related constants share a prefix (`COLOR_RED`, `COLOR_GREEN`).
- **Methods**: verbs, camelCase (e.g. `getName()`, `computeTotalWidth()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()` (e.g. `sortList_emptyList_exceptionThrown()`).
- **Abbreviations**: not all-caps when part of a name — `exportHtmlSource()`, not `exportHTMLSource()`.
- All names in English.

## Layout & Formatting

- **Indentation**: 4 spaces, never tabs. A wrapped line indents 8 spaces (twice normal) from its parent line.
- **Line length**: soft limit 110 chars, hard limit 120 — never exceed 120.
- **Braces: K&R ("Egyptian") style, no exceptions.** The opening brace stays on the same line as the statement, and `else`/`catch`/`finally` stay on the same line as the closing brace before them:
  ```java
  if (condition) {
      statements;
  } else if (other) {
      statements;
  } else {
      statements;
  }

  try {
      statements;
  } catch (SomeException e) {
      statements;
  } finally {
      statements;
  }
  ```
  Never `}\nelse {` or `}\ncatch (...) {` on separate lines — that's Allman style, not K&R, and is a hard violation.
- **Loop and conditional bodies are always braced**, even a single statement — no bare `if (x) doThing();`.
- **Whitespace**: spaces around binary operators (`a = (b + c) * d;`), space after reserved words before `(` (`while (true) {`), space after commas (`doSomething(a, b, c);`), space after `;` in a `for` header (`for (i = 0; i < 10; i++)`).
- **Blank lines**: separate logical units within a block with one blank line.
- **Array declarations**: bracket attaches to the type, not the variable — `int[] a = new int[20];`, never `int a[] = ...`.

## Statements

- **Imports**: always explicit, never a wildcard (`import java.util.*;` is forbidden). Group and order: static imports first, then `java.*`, `javax.*`, `org.*`, `com.*`, `javafx.*` — alphabetical within each group. (This project's own `ace.*` packages aren't covered by that list; treating them as sorting before the JDK/third-party groups, as this codebase already does in its main source, is a reasonable convention — the important, checkable part is that `java.*` must still precede `org.*`/`com.*`/etc. wherever both appear.)
- **Variables**: initialize where declared, and declare in the smallest scope that works.
- **Class fields should never be `public`**, unless the class is a pure data holder with no behavior (no non-trivial methods). A class with real methods (state changes, computed behavior) keeps its fields `private` and exposes them through accessors if needed.

## Comments

- Written in English, American spelling.
- **Every public class and public method needs a header comment** — *except* getters/setters, overridden methods (when the parent's documentation already applies — use `{@inheritDoc}` instead of repeating it), and test classes/methods (their descriptive names already say what they do).
- Javadoc shape:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```
  - Opening `/**` on its own line; subsequent `*` aligned with it, one space after each `*`.
  - First sentence is a short summary, written in third person ("Returns...", "Adds...", "Marks..."), not imperative ("Return...", "Add...", "Mark...").
  - A blank `*` line separates the description from the `@param`/`@return`/`@throws` block.
  - No blank line between the Javadoc block and the class/method it documents.
  - Document either **all** parameters or none (unless a parameter is genuinely self-explanatory).
  - `@return` can be omitted for `void` methods or when the return value is obvious.

## When reviewing existing code against this standard

Check, in order: brace placement (`}` immediately followed by `else`/`catch`/`finally` on the *same* line, never the next), public fields on behavior-bearing classes, boolean-returning methods named with `get` instead of `is`/`has`, import grouping/ordering, line length, and whether public methods (other than the documented exceptions above) have header comments. Report findings with file and line, and don't silently fix anything unless asked — this project's convention is to report first and apply changes only after explicit approval.
