/**
 * 
 */
var loadButton;
var dropdownCountries;
var addCountryBtn;
var updateCountryBtn;
var deleteCountryBtn;
var countryNameLabel;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(() => {
	loadButton = $("#loadCountriesBtn");
	dropdownCountries = $("#dropdownCountries");
	addCountryBtn = $("#addCountryBtn");
	updateCountryBtn = $("#updateCountryBtn");
	deleteCountryBtn = $("#deleteCountryBtn");
	countryNameLabel = $("#countryNameLabel");
	fieldCountryName = $("#fieldCountryName");
	fieldCountryCode = $("#fieldCountryCode");

	loadButton.click(function() {
		loadCountries();
	});

	dropdownCountries.on("change", function() {
		changeFormStateToSelectedCountry();
	});

	addCountryBtn.on("click", function() {
		if (addCountryBtn.val() == "Add") {
			addCountry();
		} else {
			changeFormStateToNewCountry();
		}
	});

	updateCountryBtn.on("click", function() { updateCountry(); });

	deleteCountryBtn.on("click", function() { deleteCountry(); });

});

function deleteCountry() {
	optionValue = dropdownCountries.val();
	countryId = optionValue.split("-")[0];

	url = contextPath + "countries/delete/" + countryId;

	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: (xhr) => {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		}
	}).done(() => {
		$("#dropdownCountries option[value = '" + optionValue + "']").remove();
		showToastMessage("Selected country has been deleted!!");
		changeFormStateToNewCountry();
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

function updateCountry() {
	if (!validateFormCountry()) return;

	url = contextPath + "countries/save";

	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();

	countryId = dropdownCountries.val().split("-")[0];

	jsonData = { id: countryId, name: countryName, code: countryCode };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: (xhr) => {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		$("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropdownCountries option:selected").text(countryName);
		showToastMessage("Selected country has updated sucessfully!");
		changeFormStateToNewCountry();
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

function selectNewCountry(countryId, countryName, countryCode) {
	optionValue = countryName + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropdownCountries);

	$("#dropdownCountries option[value = '" + optionValue + "']").prop("selected", true);

	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function validateFormCountry() {
	formCountry = document.getElementById("formCountry");
	if (!formCountry.checkValidity()) {
		formCountry.reportValidity();
		return false;
	}

	return true;
}

function addCountry() {
	if (!validateFormCountry()) return;

	url = contextPath + "countries/save";

	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();

	jsonData = { name: countryName, code: countryCode };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		selectNewCountry(countryId, countryName, countryCode);
		showToastMessage("New country has added sucessfully!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

function changeFormStateToNewCountry() {
	addCountryBtn.val("Add");
	countryNameLabel.text("Country Name:");

	updateCountryBtn.prop("disabled", true);
	deleteCountryBtn.prop("disabled", true);

	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function changeFormStateToSelectedCountry() {
	addCountryBtn.prop("value", "New");
	updateCountryBtn.prop("disabled", false);
	deleteCountryBtn.prop("disabled", false);

	countryNameLabel.text("Selected country:")
	selectedCountryName = $("#dropdownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName);

	countryCode = dropdownCountries.val().split("-")[1];
	fieldCountryCode.val(countryCode);

}

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
		showToastMessage("All countries have loaded!!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}