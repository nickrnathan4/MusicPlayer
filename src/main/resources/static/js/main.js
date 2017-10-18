

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

	    $.post("/play", {"songId":songId},
		    function(returnedData){
	    		getSongProfile();
			}).fail(function(xhr, status, error){
			    console.log("ERROR: ",error);
			    $("#btn-play").prop("disabled", false);
			});    
	}
}

function getSongProfile() {
	$.post("/song", "",
	    function(returnedData){
	    	$("svg").remove();
	    	displaySongProfile(returnedData);
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    		
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
  
// append the svg obgect to the body of the page
// appends a 'group' element to 'svg'
// moves the 'group' element to the top left margin
var svg = d3.select("#song-display").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");

function draw(data) {

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

	  // // Add the X Axis
	  // svg.append("g")
	  //     .attr("transform", "translate(0," + height + ")")
	  //     .call(d3.axisBottom(x));

	  // // Add the Y Axis
	  // svg.append("g")
	  //     .call(d3.axisLeft(y));
	  }
	  
  // trigger render
  draw(song.keyPoints);


}


