
var context;
var source;
var buff;
var timer;
var paused = false;
var startedAt = 0;
var elapsed = 0;

window.addEventListener('load', init, false);
function init() {
  try {
    window.AudioContext = window.AudioContext||window.webkitAudioContext;
    context = new AudioContext();
  }
  catch(e) {
    alert('Web Audio API is not supported in this browser.');
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
	$("#btn-play").prop("disabled", true); 
	if(paused) {
		resume_playback();
	}
	else {	
		var songId = $(".td-selected").find('td:first').text();
		if(songId){	     
			var formData = new FormData();
			formData.append('songId', songId);
			var request = new XMLHttpRequest();
			request.responseType = 'arraybuffer';
			request.open('POST', '/mp3', true);
			
			request.onload = function(e) {
				context.decodeAudioData(request.response, function(buffer) {
				source = context.createBufferSource(); 
				source.buffer = buffer;                    
				source.connect(context.destination);       
				source.onended = onEnd;
				source.start(0); 
				startedAt = context.currentTime;
				setTimer();
		      	getSongProfile();
			  });
			}
			request.send(formData);
		}
	}
}

function pause_playback() {
 	$("#btn-play").prop("disabled", false); 
 	buff = source.buffer;
	paused = true;
 	source.stop();
 	elapsed = context.currentTime - startedAt;
}

function stop_playback() {

 	$("#btn-play").prop("disabled", false); 
 	if (source) {          
            source.disconnect();
            source.stop(0);
            source = null;
        }
    buff = null;
 	paused = false;
 	startedAt = 0;
	elapsed = 0;
	clearInterval(timer);
 	$("#seconds").html(pad(0));
    $("#minutes").html(pad(0));
     
}

function resume_playback() {
	source = context.createBufferSource(); 
	source.buffer = buff;                    
	source.connect(context.destination);       
	source.onended = onEnd;
	source.start(0,elapsed); 
	startedAt = context.currentTime - elapsed;
	pausedAt = 0;
	paused = false;
}

function onEnd(){
	if(!paused){
		stop_playback();
	}
}

function setTimer(){
	var sec = source.buffer.duration.toFixed(0);
	$("#total_seconds").html(pad(sec%60));
    $("#total_minutes").html(pad(parseInt(sec/60,10)));
	timer = setInterval( function(){
			if(!paused) {
				sec = (context.currentTime - startedAt).toFixed(0);
			    $("#seconds").html(pad(sec%60));
			    $("#minutes").html(pad(parseInt(sec/60,10)));
			}
		}, 1000);	
}

function pad ( val ) { return val > 9 ? val : "0" + val; }


function getSongProfile() {

	var songId = $(".td-selected").find('td:first').text();
	if(songId){
		$.post("/song", {"songId":songId},
		    function(returnedData){
		    	$("svg").remove();
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

