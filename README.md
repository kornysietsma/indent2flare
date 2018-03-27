# indent2flare

A Clojure library designed to calculate indentation data for source code,
merging the indent data into a d3 flare json file.

This is one component in the tools I use to produce my [toxic code explorer visualisation](https://github.com/kornysietsma/toxic-code-explorer-demo)

## Rationale

Indentation of source code can be used as a very rough proxy for complexity -
more complex software is often much more heavily indented than simpler software;
complex software with high cyclomatic complexity often has deeply nested `if` and
`switch` statements, not to mention nested classes and inner classes.  Also very
long parameter lists are often formatted into vertical formatting that produces
high levels of indentation as well.

Simpler that has been factored into lots of small methods and small classes will
generally have smaller text indentation.

Credit to [Adam Tornhill](https://github.com/adamtornhill) who described similar
approaches in his book "Your code as a crime scene" - I have found he also
has a similar tool at https://github.com/adamtornhill/indent-complexity-proxy but
it's such a simple concept I'm using my own code rather than trying to integrate
with his library!

See also https://github.com/kornysietsma/toxic-code-explorer-demo where
there will eventually be documentation on how this all fits together

## Usage

The easiest way to run this without needing clojure is to use an uberjar -
a bundled jar file of the program and all dependencies (including clojure).

You can download a `indent2flare.jar` file from this project's releases page
at https://github.com/kornysietsma/indent2flare/releases

Then you can run
`java -jar indent2flare.jar -h` for help, it will produce something like this:

```
Usage: java -jar indent2flare.jar [options]

Options:
  -i, --input filename   select an input flare-format json file for merging - if you don't specify a -b option, the program will try to read flare data from standard input for piping
  -o, --output filename  select an output file name (default is STDOUT)
  -r, --root path        select a root directory matching the flare root (default is current dir)
  -h, --help
```

For input, you want a flare file similar to that produced by [cloc2flare](https://github.com/kornysietsma/cloc2flare):
```
{
  "name" : "flare",
  "children" : [ {
    "name" : "project.clj",
    "data" : {
      "cloc" : {
        "blank" : 0,
        "comment" : 0,
        "code" : 38,
        "language" : "Clojure"
      }
    }
  }, {
```

You can run `indent2flare` using STDIN and STDOUT or specify filenames.  By default it
will look for files in the flare file that exist in the current directory (and subdirectories)
so in this case it will look for a `project.clj` file in the current directory,
when you run

`java -jar indent2flare -i input.json -o output.json`

You should get something like:
```
{
  "name" : "flare",
  "children" : [ {
    "name" : "project.clj",
    "data" : {
      "cloc" : {
        "blank" : 0,
        "comment" : 0,
        "code" : 38,
        "language" : "Clojure"
      },
      "indents" : {
        "min" : 0,
        "mean" : 14.631578947368421,
        "variance" : 105.70637119113573,
        "stdev" : 10.281360376484026,
        "sample-count" : 38,
        "median" : 17,
        "max" : 39,
        "percentiles" : {
          "25" : 2,
          "50" : 17,
          "75" : 17,
          "90" : 25,
          "95" : 38,
          "99" : 39,
          "99.9" : 39
        },
        "sum" : 556.0
      },
```

Note this just counts spaces, and arbitrarily assumes a tab is 4 spaces.

## License

Copyright © 2018 Kornelis Sietsma

Licensed under the Apache License, Version 2.0 - see LICENSE.txt for details
