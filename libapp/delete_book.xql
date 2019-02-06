xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";
let $y:=request:get-parameter("name","")

   return

<html>
<head>
    <title>Library</title>
    <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
</head>

<body>
 {update delete doc("library.xml")/books/book[name=$y]}
 book deleted succesfully {$y}
 return to <a href="admin.xql">homepage</a>
</body>

</html>
    