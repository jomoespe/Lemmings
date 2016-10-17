(function (window) {
  'use strict';
  function define_Basher() {
    var Basher  = {};
    var name    = "Basher JS";
    var version = "1.0.0-SNAPSHOT";
    
    console.log(name + " " + version);

    Basher.version = function() { return version; };
    
    Basher.startListening = function( consumer ) {
      var connection = new WebSocket('ws://' + window.location.host + '/v1/basher');
      connection.onopen    = function()             { console.log("Websocket opened"); };
      connection.onerror   = function(error)        { console.log("Websocket error: " + error); };
      connection.onmessage = function(messageEvent) { consumer(messageEvent.data); };
    };
    return Basher;
  }
  
  //define the library globally if it doesn't already exist
  if(typeof(Basher) === 'undefined') {   
    window.Basher = define_Basher();
  } else {
    console.log("Basher library already defined.");
  }    
} (window));
