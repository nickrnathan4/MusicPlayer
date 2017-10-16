
function playback(button) {

    var map = {};
    map["songId"] = button.value;

    $(".btn-play").prop("disabled", true);  
    $(".btn-pause").prop("disabled", false); 

    $.post("/play", {"songId":button.value},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		    $(".btn-play").prop("disabled", false);
		});    

}

function pause_playback(button) {

    var map = {};
    map["songId"] = button.value;

 	$(".btn-pause").prop("disabled", true);
 	$(".btn-play").prop("disabled", false); 

    $.post("/pause", {"songId":button.value},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		    $(".btn-pause").prop("disabled", false);
		});    
}

function stop_playback(button) {

    var map = {};
    map["songId"] = button.value;

    $(".btn-pause").prop("disabled", true);
 	$(".btn-play").prop("disabled", false); 

    $.post("/stop", {"songId":button.value},
	    function(returnedData){
		}).fail(function(xhr, status, error){
		    console.log("ERROR: ",error);
		});    
}

