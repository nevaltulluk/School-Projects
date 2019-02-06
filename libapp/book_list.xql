xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";
let $name:= request:get-parameter("name","")
let $author:= request:get-parameter("author","")
let $genre:= request:get-parameter("genre","")


let $book:=
    <book>
        if ($name = "") then
            {
        for $x in doc("library.xml")/books/book[author = $author and genre = $genre]
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
         else if ($author = "") then
             {
             for $x in doc("library.xml")/books/book[$name = name and genre = $genre]
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
        else then
            {
            for $x in doc("library.xml")/books/book[$name = name and author = $author]
            return 
            <book>
                <name>{$x/name}</name>
                <author>{$x/author}</author>
                <genre>{$x/genre}</genre>
                <year>{$x/year}</year>
                <popularity>{$x/popularity}</popularity>
                <is_available>{$x/availability}</is_available>
            </book>
    }
   
        
    </book>
return
    <html>
        <head>
            <title>Search Results</title>
            <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
        </head>
        <body>
		<hr></hr>
		 <h2>Search Results</h2>
             <table id= "liste">
                            <tr>
                                
                                <th><h3>Name</h3> </th>
                                <th><h3>Author</h3> </th>
                                <th><h3>Genre</h3> </th>
                                <th><h3>Year</h3> </th>
                                

                            </tr>
                             
                            { for $x in $book/book return
                            <tr>

                                <th>{string($x/name)} </th>
                                <th>{string($x/author)} </th>
                                <th>{string($x/genre)} </th>
                                <th>{string($x/year)} </th>
                                
                            </tr>
                            }
                        </table>
        </body>
        
    </html>