"use strict"

let body = document.body;

let myH = document.createElement( 'h1');
let headText = document.createTextNode( "Mushrooms of Utah!");
myH.appendChild( headText );
body.appendChild( myH );

let myP = document.createElement( 'p' );
let parText = document.createTextNode( "Here you'll find some of Utah's most interesting and fun fungi")
myP.appendChild( parText );
body.appendChild( myP );

let aspenBolImage = document.createElement( 'IMG');
aspenBolImage.setAttribute("src", "DSC01534.JPG");
aspenBolImage.setAttribute( "height", "300");
aspenBolImage.setAttribute( "width" , "350");
document.body.appendChild( aspenBolImage );

let myDiv = document.createElement( 'div');
myDiv.style = 'border: 1px solid red; padding: 20px';
body.appendChild( myDiv );

let btn = document.createElement( 'button' );
btn.innerHTML = "Aspen Bolete";
btn.style.background = '#3dfe3a';
//btn.onclick = "document.location='index.html'";
document.body.appendChild( btn );




