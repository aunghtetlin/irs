var searchCondition;
var searchValue;
var stopwords;

$(document).ready(function() {
	$('#searchValue').val('');
	$('#searchValue').focus();
	getStopWords();
});

function searchInformations(){
	searchCondition = $('#searchCondition').val();
	searchValue = $('#searchValue').val();
	if(searchValue) {
		getInformations(searchCondition, searchValue);
	} else {
		$('#messageModal #messageText').text("Type something to search!");
		$('#messageModal').modal();
	}
}

function getInformations(searchCondition, searchValue) {
	$.ajax({
	    type: "POST",
	    contentType: "application/json",
	    url: "/search?searchCondition=" + searchCondition + "&searchValue=" + searchValue,
	    success: function (data) {
	    	removeInformations();
	    	createDataWrapper();
	    	
	    	if( !$.isArray(data) ||  !data.length ) {
			    insertEmptyMsg(searchValue);
			} else {
				let title, description;
				insertSearchResult(searchValue, data.length);
		    	for (var key in data) {
				   if (data.hasOwnProperty(key)) {
				   		title = data[key].title;
				   		description = data[key].description;
				   		removeStopWords(searchValue).forEach(function(value) {
						    title = title.replace(new RegExp('('+ value +')','ig'), '<span class=\"highlight\">$1</span>');
				   			description = description.replace(new RegExp('('+ value +')','ig'), '<span class=\"highlight\">$1</span>');
						});
						insertInformation(data[key].id, title, description);
				   }
				}
			}
	    },
	    error: function (xhr, status, error) {
	        $('#messageModal #messageText').text(xhr.responseText);
			$('#messageModal').modal();
	    }
	});
}

function removeInformations(){
	$('.search-data-wrapper').remove();
}

function createDataWrapper(){
	let dataWrapper = document.createElement("div");
	dataWrapper.setAttribute("id", "dataWrapper");
	dataWrapper.className = "search-data-wrapper col-md-10 col-md-offset-1";
	$('.searchBoxRowWrapper').after(dataWrapper);
}

function insertSearchResult(keyword, count) {
	var result = (count == 0) ? count + " document" : count + " documents";
	dataWrapper.innerHTML = "<p class=\"search-info-msg\">" + "Search result for <b>" + keyword + "</b> - " + result + "</p>";
}

function insertEmptyMsg(keyword) {
	dataWrapper.innerHTML = "<p class=\"search-info-msg\">" + "You search - <b>" + keyword + "</b> - did not match any documents." + "</p>";
}

function insertInformation(id, title, description){
	let information;
	let dataWrapper = $('#dataWrapper');
	
	information = document.createElement("div");
	information.className = "pt-25";
	information.innerHTML= "<h4><a class=\"link-irs pointer\" onclick=showDetailInformation(" + id + ")>" + title + "</a></h4><p class=\"justify\">" + description + "</p>";
	
	dataWrapper.append(information);
}

function insertDetailInformation(title, content){
	let information;
	let dataWrapper = $('#dataWrapper');
	
	removeStopWords(searchValue).forEach(function(value) {
		title = title.replace(new RegExp('('+ value +')','ig'), '<span class=\"highlight\">$1</span>');
		content = content.replace(new RegExp('('+ value +')','ig'), '<span class=\"highlight\">$1</span>');
	});
	
	information = document.createElement("div");
	information.className = "pt-25";
	information.innerHTML= "<h4>" + title + "</h4><p class=\"justify indent\">" + content + "</p>" + "<a onclick=\"backToSearchList();\"><i class=\"fa fa-reply link-irs pull-right pointer\"></i></a>";
	
	dataWrapper.append(information);
}

function showDetailInformation(id) {	
	$.ajax({
	    type: "POST",
	    contentType: "application/json",
	    url: "/find?id=" + id,
	    success: function (data) {
	    	removeInformations();
	    	createDataWrapper();
	    	insertDetailInformation(data.title, data.content);
	    },
	    error: function (xhr, status, error) {
	        $('#messageModal #messageText').text(xhr.responseText);
			$('#messageModal').modal();
	    }
	});
}

function backToSearchList() {
	getInformations(searchCondition, searchValue);
}

function isEnterKeyPress(e) {
	if(e.keyCode == 13) {
		searchInformations();
	}
}

function removeStopWords(searchValue) {
	return searchValue.split(' ').filter(val => !stopwords.includes(val));
}

function getStopWords(searchValue) {
	$.ajax({
	    type: "POST",
	    contentType: "application/json",
	    url: "/stopwords",
	    success: function (data) {
			stopwords = data;
	    },
	    error: function (xhr, status, error) {
	        $('#messageModal #messageText').text(xhr.responseText);
			$('#messageModal').modal();
	    }
	 });
}