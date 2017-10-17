
$('#song-list').on('click', '.clickable-row', function(event) {
  
  if($(this).hasClass('td-selected')){
  	$(this).removeClass('td-selected'); 
  } 
  else {
    $(this).addClass('td-selected').siblings().removeClass('td-selected');
  }
});


function playback() {

	var songId = $(".td-selected").find('td:first').text();
	console.log(songId);
	$("#currently_playing").val(songId);
    $("#btn-play").prop("disabled", true);  

    $.post("/play", {"songId":songId},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		    $("#btn-play").prop("disabled", false);
		});    

}

function pause_playback() {

	var songId = $("#currently_playing").value;
 	$("#btn-play").prop("disabled", false); 

    $.post("/pause", {"songId":songId},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    
}

function stop_playback() {

	var songId = $("#currently_playing").value;
    $("#currently_playing").val("");
 	$("#btn-play").prop("disabled", false); 

    $.post("/stop", {"songId":songId},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    
}

