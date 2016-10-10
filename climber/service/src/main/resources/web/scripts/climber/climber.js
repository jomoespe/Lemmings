(function (window) {
  'use strict';
  function define_Climber() {
    var Climber  = {};
    var name    = "Climber JS";
    var version = "1.0.0-SNAPSHOT";
    
    console.log(name + " " + version);

    Climber.version = function() { return version; };
    
    Climber.get = function(term, position, consumer ) {
      var url    = 'http://' + window.location.host + '/v1/climber?term=' + term + '&start=' + position + '&size=10';
      var client = new XMLHttpRequest();
      client.open("GET", url, false);      
      client.setRequestHeader("Accept", "application/json");      
      client.setRequestHeader("Content-Type", "application/json");      
      client.send();
      if (client.status === 200) {
          consumer( client.responseText );
      } else {
          alert('Error ' + client.statusText);
      }
    };
    return Climber;
  }
  
  //define the library globally if it doesn't already exist
  if(typeof(Climber) === 'undefined') {   
    window.Climber = define_Climber();
  } else {
    console.log("Climber library already defined.");
  }    
} (window));
