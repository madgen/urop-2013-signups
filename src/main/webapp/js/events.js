moduleScripts['signapp']['events'] = {
	'new' : 
		[
			function() {
			  $(".datepicker").datepicker({dateFormat: "dd/mm/yy"});
			  $("input[name='location']").autocomplete(
			  		{
			  			minLength: 2,
			  			source: function(request, response) {
			  				$.getJSON("http://map.cam.ac.uk/v6.json", {partial: "partial", "q": request.term, limit: "10"})
			  					.done(function(data) {
			  						response($.map(data, function(item) {
			  							return { 
				  								label: (item.prefix ? item.prefix + " " : "") + item.name, 
				  								value: (item.prefix ? item.prefix + " " : "") + item.name
			  								};
			  						}));
			  					});
			  			},
			  			select: function(event, resp) {
			  				$("input[name='location']").val(resp.item.label).text(resp.item.label);
			  				$("iframe").attr("src", "http://map.cam.ac.uk/" + resp.item.label);
			  			}
			  		});
			  
			  $("input[name='location'").keyup(function() {
			  	if ($(this).val() != "") {
			  		$("input[name='room']").removeAttr("disabled");
			  	} else {
			  		$("input[name='room']").attr("disabled","disabled");
			  	}
			  });

			  $("input[name='room']").autocomplete(
			  	{
			  		minLength: 2,
			  		source: function(request, response) {
			  			$.getJSON("/signapp/events/queryRooms", 
			  					{
			  						"qroom": request.term, 
			  						"qbuilding": $("input[name='location']").text()
			  					})
			  				.done(function(data) {
			  					response($.map(data,function(item) {
			  						return {
			  							value: item.room,
			  							label: item.room
			  						}
			  					}));
			  				});
			  		}
			  	});
			
			  $(".form-control-option-button").click(function() {
					var elementToClone = $(this).attr('data-element-to-clone');
				  var target = $(this).attr('data-target');
			
			    var controls = $('.' + elementToClone).clone().get(0).outerHTML;
			    var elem = $(controls).css("display","none");
			    $('.' + target).find(".single-slot-controls").last().after(elem);
			    $('.' + target).find(".single-slot-controls").last().slideDown("fast");
			    elem.find(".datepicker").removeClass("hasDatepicker").removeAttr("id").datepicker({dateFormat: "dd/mm/yy"});
			    $(".delete-time-slot").removeClass("disabled");
			  });
			  
			  $(".add-range").click(function() {
			  	if ($(".range-controls").css("display") == "none") {
			  		$(".range-controls").slideDown("fast");
			  		$(this).text("Remove Range");
			  	} else {
			  		$(".range-controls").slideUp("fast");
			  		$(this).text("Add Range");
			  	}
			  });
			  
			  $(".generate-slots").click(function() {
			  	var parentElem = $(this).parent().parent().parent();
			  	var len = parseInt(parentElem.find("#number_of_slots").val());
			  	var date = parentElem.find("#date").val();
			  	var startHour = parseInt(parentElem.find("#hour").val());
			  	var startMinute = parseInt(parentElem.find("#minute").val());
			  	var duration = parseInt(parentElem.find("#duration").val());
			  	var breakDuration = parseInt(parentElem.find("#break").val());
			  	
			  	var singleSlot;
			  	var hour;
			  	var minute;
			  	for(var i = 0; i < len; i++) {
			  		singleSlot = $(".single-slot-controls").clone().get(0).outerHTML;
			  		singleSlot = $(singleSlot);
			  		
			  		// Calculate times
			  		minute = startMinute + ((duration + breakDuration) * i);
			  		hour = startHour;
			  		while (minute >= 60) {
			  			minute -= 60;
			  			hour++;
			  		}
			  		
			  		// Set necessary attributes
			  		if (hour < 24) {
			  			singleSlot.find("select[name='available_minutes[]']").val(minute).parent().find(".current").val(minute).text(minute);
			  			singleSlot.find("select[name='available_hours[]']").val(hour).parent().find(".current").val(hour).text(hour);
			  			$(".time-controls-wrapper").find(".single-slot-controls").last().after(singleSlot);
			  			singleSlot.find(".datepicker").removeClass("hasDatepicker").removeAttr("id").datepicker({dateFormat: "dd/mm/yy"}).val(date).text(date);
			  		}
			  		$(".button").removeClass("disabled");
			  	}
			  });
			
			  $(".event-type-input").tokenInput("/signapp/events/queryTypes", {
			    theme: "facebook",
			    method: "post",
			    tokenValue: "name",
			    propertyToSearch: "name",
			    min_chars: 2,
			    hintText: "Add a new type",
			    preventDuplicates: true,
			    resultsLimit: 10,
			    resultsFormatter: function(item){ return "<li><div style='display: inline-block; padding-left: 10px;'>"+ item.name + "</div></li>" }
			  });
			  
			  $(this).on("click", ".delete-time-slot", function() {
			  	if ($(".delete-time-slot").length != 1) {
			  		$(this).parent().parent().parent().slideUp("fast", function() {
			  			$(this).remove()
			  			
				  		if($(".delete-time-slot").length == 1) {
				  			$(".delete-time-slot").addClass("disabled");
				  		}
			  		});
			  	}
			  });
			},
			
			function() {
			  $("span#datetime").click(function() {
			    $("div#manual").slideUp("fast", function() {
			      $("div#datetime").slideDown("fast");
			    });
			  });
			
			  $("span#manual").click(function() {
			    $("div#datetime").slideUp("fast", function() {
			      $("div#manual").slideDown("fast");
			    });
			  });
			}
		],
	'show' 	:	
		[
		  function() {
		  	$("#map-toggle").click(function(e){
		  		e.preventDefault();
		  		if ($(this).data("show") == "true") {
		  			$(this).data("show","false");
		  			$("#iframe-wrapper").addClass("overflow-hidden");
		  			$(this).text("Show on map");
		  		} else {
		  			$(this).data("show", "true");
		  			$("#iframe-wrapper").removeClass("overflow-hidden");
		  			$(this).text("Hide map");
		  		}
		  	});
		  	
		  	$(".slot-field").each(function(i) {
			  	$(this).tokenInput("/signapp/events/queryCRSID", {
			  		theme: "facebook",
			  		method: "post",
			  		tokenValue: "crsid",
			  		propertyToSearch: "crsid",
			  		min_chars: 2,
			  		hintText: "Type a CRSID",
			  		preventDuplicates: true,
			  		resultsLimit: 10,
			  		tokenLimit: 1,
			  		resultsFormatter: function(item){ return "<li>" + "<div style='display: inline-block; padding-left: 10px;'><div class='full_name'>" + item.name + " (" + item.crsid + ")</div><div class='email'>" + item.crsid + "@cam.ac.uk</div></div></li>" },
			      tokenFormatter: function(item) { return "<li><p>" + item.name + "<br>" + item.crsid + "</p></li>" }	
			  	});

			  	if($(this).data("crsid") != "") {
			  		$(this).tokenInput("add", {crsid: $(this).data("crsid"), name: $(this).data("name")});
			  	}
		  	});			
		  }
		]
}