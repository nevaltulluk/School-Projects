xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";
let $library:=doc("library.xml")
let $name:=request:get-parameter("name","")
let $author:=request:get-parameter("author","")
let $year:=request:get-parameter("year","")
let $genre:=request:get-parameter("genre","")
let $book:=
    <book>
        <name>{$name}</name>
        <author>{$author}</author>
        <genre>{$genre}</genre>
        <year>{$year}</year>
        <popularity>1</popularity>
        <available>1</available>
    </book>
   return

<html>
<head>
    <title>Library</title>
    <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
</head>

<body>
 {update insert $book into $library/books}
 book added succesfully
 return to <a href="admin.xql">homepage</a>
</body>

</html>
    