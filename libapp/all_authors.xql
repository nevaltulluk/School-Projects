xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";
let $opt:= request:get-parameter("option","")



let $book:=
    <book>
            {
        for $x in (doc("library.xml")/books/book )
        return 
             <book>
                <name>{$x/name}</name>
                <author>{$x/author}</author>
                <genre>{$x/genre}</genre>
                <year>{$x/year}</year>
                <popularity>{$x/popularity}</popularity>
                <is_available>{$x/is_available}</is_available>
            </book>
            }
    </book>
    let $unique-items := distinct-values($book/book/author)
return
    <html>
        <head>
            <title>List of All Authors</title>
            <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
        </head>
        <body>
		<hr></hr>
		 <h2>List of All Authors</h2>
             <table id= "liste">
                            List of all authors{$opt}
                            { for $x in $unique-items return
                            <tr>
                                <th>{string($x)} </th>
                            </tr>
                            }
                        </table>
        </body>
        
    </html>