# Reflection-Extension
This extension allows you to extract names of breeds, procedures, and variables (global and breed-specific ones).
It has three simple reporters:

## `globals`
reflection:globals reports a list with the names of all global variables

## `breeds`
`reflection:breeds` reports a list of all breed names, and all their variables, default variables included.

## `procedures`
`reflection:procedures` reports a list of all procedures and their arguments. It also tells you whether they are reporters or commands.

## `current-procedure`
`reflection:current-procedure` reports the name of the current procedure.

## `callers`
`reflection:callers` reports the name of the callers to the current procedure as a list. If this is inside a procedure `foo`, which is called by `bar` this will report `["FOO" "BAR"]`.
