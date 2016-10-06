Climber.get('t', 2, function(result) {
    var elements = JSON.parse(result);
    elements.forEach( function(element) {
        document.querySelector('#console').innerHTML += element + "</br>";    
    });
});
