# Contribute to Dragon Launcher

## Issues:

- Read previous, check for no duplicate
- Try restarting app; clearing cache/data update to latest release/beta

## Pull Requests:

- If you plan to make big modifications, ask me before, to avoid wasting time

## My values

- Always let the user the choice: everything should be customizable, that's why I have a kinda
  robust settings stores system aligned with the backups
- Never ANY internet access
- Entirely open source

## Code styles

- Use camelCase for variables and functions name, and globally everywhere
- call Context instances `ctx`, not `context`
- Use snake_case for resources like strings or images
- Use SCREAMING_SNAKE_CASE for settings keys in stores
- Always add spaces between brackets in function declaration, condition statement

```kotlin
fun foo() {
    if (cond) {
        bar()
    }
}
```

- When possible, try to add lots of docstring and comments on code to make it more clear for
  reviewing
- Use this char in comments cause I like it

```text
─
```

Ex:

```kotlin
/*  ─────────────  Tags constants  ─────────────  */
```

- Put else statements in the same line as the closing if:

```kotlin
if (cond) {
    doSomething()
} else {
    doSomethingElse()
}
```
