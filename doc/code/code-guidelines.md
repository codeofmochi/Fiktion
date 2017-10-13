## Code Guidelines

Google java guidelines: https://google.github.io/styleguide/javaguide.html

### Formatting

- Braces always used on if, else, for, do and while statements
- No line break before the opening brace
- Line break after the opening brace
- Line break before the closing brace
- Only empty blocks that are single statements can be opened and closed on the same line
- No space between the statement and the parenthesis
- One statement per line

```java
if() {

} else if {

} else {

}

// This is also fine if you prefer, but try to be consistent
if () {

}
else {

}
```
```java
// single statement with empty block
void exemple() {}
```

### Variables, declarations and naming

- Names starts with a lower case and each new word in the name starts with an upper case
- One declaration per line
- On arrays declaration the brackets are on the type

```java
String[] args (not String args[])
```
- Try to use if/else instead of Switch statements
- Class names starts with an Uppercase
- Constants are in upper case, underscore between multi-words names

```java
CONSTANT_NUMBER
```

### Javadoc

- Try to follow this format for method declarations

```java
/**
* Returns something ....
* ...
* ...
*
* @param
* @return
*/
```

