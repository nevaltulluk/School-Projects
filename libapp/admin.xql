xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";


let $book:=
    <book>
    {
        for $x in doc("library.xml")/books/book
        return 
           
            <book>
                <name>{$x/name}</name>
                <author>{$x/author}</author>
                <genre>{$x/genre}</genre>
                <year>{$x/year}</year>
                <popularity>{$x/popularity}</popularity>
            </book>
         
    }
    </book>
return 
    <html>
        <head>
            <title>Library Application</title>
            <link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
        </head>
        <body>
            <h1>Welcome to the Library Application</h1>
            <hr></hr>
            <h2>Add a book here:</h2>

            <form action="add_book.xql" method = "get">
		<p>Book name:</p><p> <input type="text" name="name"></input></p>
		<p>Author:</p><p> <input type="text" name="author"></input></p>
		<p>Genre:</p><p> <input type="text" name="genre"></input></p>
		<p>Year:</p><p> <input type="text" name="year"></input></p>
		<p><input type="submit" value="Add"></input></p>
		         </form>           

		<hr></hr>
		
		 <h2>List of books:</h2>
             <table id= "liste">
                 <tr>
                     <th></th>
                     <th><h3>Name</h3> </th>
                     <th><h3>Author</h3> </th>
                     <th><h3>Genre</h3> </th>
                     <th><h3>Year</h3> </th>
                 </tr>
                  
                 { for $x in $book/book return
                 <tr>
                     <th><a href="delete_book.xql?name={$x/name}">delete</a></th>
                     <th>{string($x/name)} </th>
                     <th>{string($x/author)} </th>
                     <th>{string($x/genre)} </th>
                     <th>{string($x/year)} </th>
                 </tr>
                            }
          </table>
        </body>
    </html>
    
