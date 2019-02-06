xquery version "3.1";
declare option exist:serialize "method=html5 media-type=text/html";
let $name:= request:get-parameter("name","")
let $update := doc('db/apps/libapp/library.xml')//books/book[name = $name]
return
    update replace $update/available with <available>0</available>
            
   