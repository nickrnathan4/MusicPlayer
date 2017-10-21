
var context;
var source;
window.addEventListener('load', init, false);
function init() {
  try {
    // Fix up for prefixing
    window.AudioContext = window.AudioContext||window.webkitAudioContext;
    context = new AudioContext();
  }
  catch(e) {
    alert('Web Audio API is not supported in this browser');
  }
}

$('#song-list').on('click', '.clickable-row', function(event) {
  
  if($(this).hasClass('td-selected')){
  	$(this).removeClass('td-selected'); 
  } 
  else {
    $(this).addClass('td-selected').siblings().removeClass('td-selected');
  }
});

function start_playback() {

	var songId = $(".td-selected").find('td:first').text();
	if(songId){
		
	    $("#btn-play").prop("disabled", true);  

		var formData = new FormData();
		formData.append('songId', songId);
		var request = new XMLHttpRequest();
		request.responseType = 'arraybuffer';
		request.open('POST', '/mp3', true);
		request.onload = function(e) {
			context.decodeAudioData(request.response, function(buffer) {
	      	playSound(buffer);
	      	getSongProfile() 
		  });
		}
		request.send(formData);
	}
}

function stop_playback() {

 	$("#btn-play").prop("disabled", false); 
 	$("#currently_playing").val("");
 	source.stop();
     
}

function playSound(buffer) {
	try{
	  source = context.createBufferSource(); // creates a sound source
	  source.buffer = buffer;                    // tell the source which sound to play
	  source.connect(context.destination);       // connect the source to the context's destination (the speakers)
	  source.start(0);                           // play the source now
	}
	catch(e){
		console.log(e);
	}
}

function getSongProfile() {

	var songId = $(".td-selected").find('td:first').text();
	if(songId){
		$.post("/song", {"songId":songId},
		    function(returnedData){
		    	$("svg").remove();
		    	console.log(returnedData);
		    	displaySongProfile(returnedData);
			}).fail(function(xhr, status, error){
			    console.log("ERROR: ",error);
			});  
	}  		
}

function displaySongProfile(song) {
	
	// set the dimensions and margins of the graph
	var margin = {top: 20, right: 20, bottom: 30, left: 50},
	    width = 1200
	    height = 300 - margin.top - margin.bottom;

	// set the ranges
	var x = d3.scaleTime().range([0, width]);
	var y = d3.scaleLinear().range([height, 0]);

	// define the line
	var valueline = d3.line()
	    .x(function(d) { return x(d.Time); })
	    .y(function(d) { return y(d.Value); });
	  

	var svg = d3.select("#song-display").append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	  .append("g")
	    .attr("transform",
	          "translate(" + margin.left + "," + margin.top + ")");

	function draw(data) {
	var data = song.keyPoints;
		  // format the data
		  data.forEach(function(d) {
		      d.Time = d.time
		      d.Value = +d.value;
		  });
		 
		  // Scale the range of the data
		  x.domain(d3.extent(data, function(d) { return d.time; }));
		  y.domain([d3.min(data, function(d) { return d.value})
		  		  	,d3.max(data, function(d) { return d.value})]);
		  
		  // Add the valueline path.
		  svg.append("path")
		      .data([data])
		      .attr("class", "line")
		      .attr("d", valueline);
		  }
		  
  // trigger render
  draw(song.keyPoints);

}



/*

// Server Side 
function start_playback() {

	var songId = $(".td-selected").find('td:first').text();
	if(songId){
		
	    $("#btn-play").prop("disabled", true);  

	    $.post("/play", {"songId":songId},
		    function(returnedData){
	    		getSongProfile();
			}).fail(function(xhr, status, error){
			    console.log("ERROR: ",error);
			    $("#btn-play").prop("disabled", false);
			});    
	}
}

function pause_playback() {

 	$("#btn-play").prop("disabled", false); 
    $.post("/pause", "",
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    
}

function stop_playback() {

 	$("#btn-play").prop("disabled", false); 
 	$("#currently_playing").val("");
    $.post("/stop", "",
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    
}

*/

