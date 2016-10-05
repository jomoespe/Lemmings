Basher.startListening( function(message) {
    console.log(message)
    document.querySelector('#console').innerHTML += message + "</br>";
});
