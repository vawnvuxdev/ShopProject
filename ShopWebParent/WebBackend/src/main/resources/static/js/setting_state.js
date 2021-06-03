/**
 * 
 */
var loadCountriesBtnForStates;
var dropdownCountriesForStates;
var dropdownStates;
var stateNameLabel;
var stateNameField;
var addStateBtn;
var updateStateBtn;
var deleteStateBtn;

$(document).ready(() => {
	loadCountriesForStatesBtn = $("#loadCountriesForStatesBtn");
	dropdownCountriesForStates = $("#dropdownCountriesForStates");
	dropdownStates = $("#dropdownStates");
	stateNameLabel = $("#stateNameLabel");
	stateNameField = $("#stateNameField");
	addStateBtn = $("#addStateBtn");
	updateStateBtn = $("#updateStateBtn");
	deleteStateBtn = $("#deleteStateBtn");

	loadCountriesForStatesBtn.click(function() { loadCountriesForStates(); });

	dropdownCountriesForStates.on("change", function() { loadStatesByCountry(); });

	dropdownStates.on("change", function() { changeFormStateToSelectedState(); });

	addStateBtn.on("click", function() {
		if (addStateBtn.val() == "Add") {
			addState();
		} else {
			changeFormStateToNew();
		}
	});

	updateStateBtn.on("click", function() { updateState(); });

	deleteStateBtn.on("click", function() { deleteState(); });
});

/*1*/
function loadCountriesForStates() {
	url = contextPath + "countries/list";

	$.get(url, (responseJSON) => {
		dropdownCountriesForStates.empty();

		$.each(responseJSON, (index, country) => {
			$("<option>").val(country.id).text(country.name).appendTo(dropdownCountriesForStates);
		})
	}).done(() => {
		loadCountriesForStatesBtn.val("Refresh Countries");
		showToastMessage("All countries have loaded!!");
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

/*2*/
function loadStatesByCountry() {
	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list/" + countryId;

	$.get(url, (responseJSON) => {
		dropdownStates.empty();

		$.each(responseJSON, (index, state) => {
			$("<option>").val(state.id).text(state.name).appendTo(dropdownStates);
		})
	}).done(() => {
		changeFormStateToNew();
		showToastMessage("All states of selected country have loaded!!");
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});

}

/*3*/
function changeFormStateToSelectedState() {
	addStateBtn.prop("value", "New");
	updateStateBtn.prop("disabled", false);
	deleteStateBtn.prop("disabled", false);

	stateNameLabel.text("Selected state:")
	selectedStateName = $("#dropdownStates option:selected").text();
	stateNameField.val(selectedStateName);
}

/*4*/
function changeFormStateToNew() {
	addStateBtn.val("Add");
	stateNameLabel.text("Country Name:");

	updateStateBtn.prop("disabled", true);
	deleteStateBtn.prop("disabled", true);

	stateNameField.val("").focus();
}

/*5*/
function addState() {
	url = contextPath + "states/save";
	stateName = stateNameField.val();

	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();

	jsonData = { name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: (xhr) => {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done((stateId) => {
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("Selected state has saved sucessfully!");
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}


/*6*/
function updateState() {
	url = contextPath + "states/save";
	stateId = dropdownStates.val();
	stateName = stateNameField.val();

	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();

	jsonData = { id: stateId, name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: (xhr) => {
			xhr.setRequestHeader(csrfHeaderName, csrfValue)
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done((stateId) => {
		$("#dropdownStates option:selected").text(stateName);
		showToastMessage("Selected state has updated sucessfully!");
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}


/*7*/
function deleteState() {
	optionValue = dropdownStates.val();
	stateId = dropdownStates.val();
	url = contextPath + "states/delete/" + stateId;
	
	$.get(url, () => {
		$("#dropdownStates option[value = '" + optionValue + "']").remove();
	}).done(() => {
		showToastMessage("Selected state has been deleted!!");
	}).fail(() => {
		showToastMessage("ERROR: Could not connect to server or have some ERROR !!!");
	});
}

/*7*/
function selectNewlyAddedState(stateId, stateName) {
	optionValue = stateName;
	$("<option>").val(optionValue).text(stateName).appendTo(dropdownStates);

	$("#dropdownStates option[value = '" + optionValue + "']").prop("selected", true);

	stateNameField.val("").focus();
}