$(document).ready(function() {
	$("#buttonCancel").on("click", function() {
		//window.location = "[[@{/users}]]";
		window.location = moduleURL;
	});

	$("#fileImage").change(function() {
		if (! checkFileSize(this)){
			return;
		}
		showImageThumbnail(this);

	});

});

function checkFileSize(fileInput) {
	fileSize = fileInput.files[0].size;

	if (fileSize > (1048576)) {
		fileInput.setCustomValidity("Your image is bigger than 1Mb!");
		fileInput.reportValidity();
		
		return false;
	} else {
		fileInput.setCustomValidity("");
		
		return true;
	}
}

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
};

function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function showErrorModal(message) {
	showModalDialog("Error", message);
}

function showWarningModal(message) {
	showModalDialog("Warning", message);
}
