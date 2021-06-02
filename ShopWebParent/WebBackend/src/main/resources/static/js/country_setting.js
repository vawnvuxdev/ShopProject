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

	loadButton.click(() => {
		loadCountries();
	});

	dropdownCountries.on("change", () => {
		changeFormStateToSelectedCountry();
	});

	addCountryBtn.on("click", () => {
		if (addCountryBtn.val() == "Add") {
			addCountry();
		} else {
			changeFormStateToNew();
		}
	});

	updateCountryBtn.on("click", () => { updateCountry(); });

	deleteCountryBtn.on("click", () => { deleteCountry(); });

});

function deleteCountry() {
	optionValue = dropdownCountries.val();
	countryId = optionValue.split("-")[0];

	url = contextPath + "countries/delete/" + countryId;

	$.get(url, () => {
		$("#dropdownCountries option[value = '" + optionValue + "']").remove();
	}).done(() => {
		showToastMessage("Selected country has been deleted!!");
	}).fail(() => {
		showToastMessage("ERROR: Can not connect to server!!");
	});
}

function updateCountry() {
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
	}).done((countryId) => {
		$("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropdownCountries option:selected").text(countryName);
		showToastMessage("Selected country has updated sucessfully!");
		changeFormStateToNew();
	}).fail(() => {
		showToastMessage("ERROR: Can not connect to server!!");
	});
}

function selectNewCountry(countryId, countryName, countryCode) {
	optionValue = countryName + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropdownCountries);

	$("#dropdownCountries option[value = '" + optionValue + "']").prop("selected", true);

	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function addCountry() {
	url = contextPath + "countries/save";

	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();

	jsonData = { name: countryName, code: countryCode };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: (xhr) => {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done((countryId) => {
		selectNewCountry(countryId, countryName, countryCode);
		showToastMessage("New country has added sucessfully!");
	}).fail(() => {
		showToastMessage("ERROR: Can not connect to server!!");
	});
}

function changeFormStateToNew() {
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
	$.get(url, (responseJSON) => {
		dropdownCountries.empty();

		$.each(responseJSON, (index, country) => {
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);
		})
	}).done(() => {
		loadButton.val("Refresh Country List");
		showToastMessage("All countries have loaded!!");
	}).fail(() => {
		showToastMessage("ERROR: Can not connect to server!!");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}