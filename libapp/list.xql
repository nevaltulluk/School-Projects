xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";

let $book:=
    <book>
            {
        for $x in (doc("library.xml")/books/book )
        return 
            $x
            }
         
           
   
        
    </book>
return
    <html>
        <head>
            <title>All Books</title>
            <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
        </head>
        <body>
		<hr></hr>
		 <h2>All Books</h2>
             <table id= "liste">
                            List of all books
                            { for $x in $book/book/name return
                            <tr>
                                <th>{string($x)} </th>
                            </tr>
                            }
                        </table>
        </body>
        
    </html>