/**
 * 
 */
var loadButton;
var dropdownCountries;

$(document).ready(function() {
	loadButton = $("#loadCountriesBtn");
	dropdownCountries = $("#dropdownCountries");

	loadButton.click(function() {
		loadCountries();
	});
});

function loadCountries() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropdownCountries.empty();

		$.each(responseJSON, function(index, country) {
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);
		})
	}).done(function() {
		loadButton.val("Refresh Country List");
		showToastMessage("All countries have loaded !! ")
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}